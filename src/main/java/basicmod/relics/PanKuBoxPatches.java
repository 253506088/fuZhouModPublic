package basicmod.relics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.powers.StrengthPower;
import basicmod.BasicMod;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import basemod.patches.com.megacrit.cardcrawl.saveAndContinue.SaveFile.ModSaves;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 潘库宝盒相关补丁集合：负责地图恶魔门标记、地图渲染、读档恢复和战斗强化。
 */
public class PanKuBoxPatches {

    @SpirePatch(clz = AbstractDungeon.class, method = "generateMap")
    /**
     * 新楼层地图生成后，重新按潘库宝盒当前计数标记恶魔门节点。
     */
    public static class GenerateMapPatch {
        /**
         * 地图生成结束后执行恶魔门扫描，保证新楼层一开始就能显示宝盒目标。
         */
        @SpirePostfixPatch
        public static void Postfix() {
            // 调用抽离好的统一地图标记逻辑，并设定为生成地图阶段触发（扫描全图）
            PanKuBox.markMapNodes(true);
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "populatePathTaken")
    /**
     * 读档路径恢复补丁：处理战斗中SL后，潘库恶魔门普通怪房退回普通房的问题。
     */
    public static class LoadDemonPortalRoomPatch {
        /**
         * 在读档恢复路径之后、真正进入房间之前，提前恢复潘库恶魔门的精英房类型。
         *
         * @param __instance 当前地牢实例，ModTheSpire实例方法补丁会自动传入
         * @param saveFile 当前存档数据，用来读取房间坐标和潘库宝盒保存的恶魔门节点
         */
        @SpirePostfixPatch
        public static void Postfix(AbstractDungeon __instance, SaveFile saveFile) {
            // 读档进当前房间前，先恢复潘库宝盒把普通怪房改成精英房的结果，避免战斗中SL后怪组回到普通怪池
            if (saveFile == null || AbstractDungeon.nextRoom == null || saveFile.relics == null) {
                return;
            }
            if (!isSavedDemonPortalNode(saveFile, saveFile.room_y * 100 + saveFile.room_x)) {
                return;
            }

            MapRoomNode node = AbstractDungeon.nextRoom;
            MapNodeDemonPortalField.isDemonPortal.set(node, true);

            // post_combat 存档：原版已恢复战斗奖励界面，替换 room 会让玩家重打已经赢过的战斗，保留原状态
            if (saveFile.post_combat) {
                return;
            }

            if (!(node.getRoom() instanceof MonsterRoomElite)) {
                node.setRoom(new MonsterRoomElite());
                // 必须立即补一次 onPlayerEntry：MonsterRoomElite 构造器只设 phase=COMBAT，monsters 仍为 null，
                // 否则下一帧 AbstractRoom.update 进入 COMBAT 分支调用 this.monsters.update() 会 NPE
                node.getRoom().onPlayerEntry();
            }
        }

        /**
         * 从BaseMod保存的潘库宝盒遗物数据中，检查当前坐标是否是恶魔门节点。
         *
         * @param saveFile 当前存档数据
         * @param encodedNode 当前房间坐标，格式为 y * 100 + x
         * @return 当前房间是否命中潘库宝盒保存过的恶魔门节点
         */
        private static boolean isSavedDemonPortalNode(SaveFile saveFile, int encodedNode) {
            int relicIndex = saveFile.relics.indexOf(PanKuBox.ID);
            if (relicIndex < 0) {
                return false;
            }

            try {
                // BaseMod按玩家遗物栏顺序保存CustomSavable遗物数据，所以这里必须先用潘库宝盒的遗物下标取对应JSON
                Object relicSaves = ModSaves.modRelicSaves.get(saveFile);
                if (!(relicSaves instanceof List) || relicIndex >= ((List<?>) relicSaves).size()) {
                    return false;
                }

                // 老版本存档或异常存档可能没有潘库宝盒JSON；读不到就直接放弃，不能影响正常读档
                Object panKuSave = ((List<?>) relicSaves).get(relicIndex);
                if (!(panKuSave instanceof JsonElement) || !((JsonElement) panKuSave).isJsonObject()) {
                    return false;
                }

                // PanKuBox.PanKuSaveData.nodeData保存的是所有恶魔门节点坐标
                JsonElement nodeData = ((JsonElement) panKuSave).getAsJsonObject().get("nodeData");
                if (nodeData == null || !nodeData.isJsonArray()) {
                    return false;
                }

                JsonArray nodes = nodeData.getAsJsonArray();
                for (JsonElement node : nodes) {
                    // 只要当前坐标命中保存列表，就说明这个读档房间需要恢复成潘库恶魔门精英房
                    if (node != null && node.isJsonPrimitive() && node.getAsInt() == encodedNode) {
                        return true;
                    }
                }
            } catch (Exception e) {
                BasicMod.logger.warn("【潘库宝盒】读档恢复恶魔门房间类型失败，已跳过本次恢复。", e);
            }
            return false;
        }
    }

    @SpirePatch(clz = MapRoomNode.class, method = "render")
    /**
     * 地图节点渲染补丁：给潘库恶魔门节点叠加醒目的精英图标光效。
     */
    public static class MapNodeRenderPatch {
        /**
         * 地图节点绘制结束后，如果该节点是恶魔门，就额外绘制一层带颜色的精英标识。
         */
        @SpirePostfixPatch
        public static void Postfix(MapRoomNode __instance, SpriteBatch sb) {
            if (MapNodeDemonPortalField.isDemonPortal.get(__instance)) {
                Texture img = ImageMaster.MAP_NODE_ELITE;
                if (img != null) {
                    float scale = (float) basemod.ReflectionHacks.getPrivate(__instance, MapRoomNode.class, "scale");
                    // 绿色外边框/背景光晕
                    sb.setColor(new Color(0.2f, 1.0f, 0.2f, 0.6f));
                    float scale1 = scale * 1.6f;
                    sb.draw(img, __instance.hb.cX - 32.0F, __instance.hb.cY - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, scale1,
                            scale1, 0.0F, 0, 0, 64, 64, false, false);

                    // 偏红黄色的内心效果
                    sb.setColor(new Color(1.0f, 0.6f, 0.0f, 0.8f));
                    float scale2 = scale * 0.9f;
                    sb.draw(img, __instance.hb.cX - 32.0F, __instance.hb.cY - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, scale2,
                            scale2, 0.0F, 0, 0, 64, 64, false, false);

                    // 恢复原本的颜色，防止影响后续渲染
                    sb.setColor(Color.WHITE.cpy());
                }
            }
        }
    }

    @SpirePatch(clz = MonsterGroup.class, method = "usePreBattleAction")
    /**
     * 战斗开场补丁：进入潘库恶魔门时，为精英怪追加宝盒强化。
     */
    public static class MonsterBuffPatch {
        /**
         * 怪物组执行战前动作后，给恶魔门里的精英单位增加生命和力量。
         */
        @SpirePostfixPatch
        public static void Postfix(MonsterGroup __instance) {
            // 首先判定当前节点是否为被潘库宝盒标记的恶魔传送门
            if (AbstractDungeon.getCurrMapNode() != null
                    && MapNodeDemonPortalField.isDemonPortal.get(AbstractDungeon.getCurrMapNode())) {
                // 遍历怪物组中的所有成员
                for (AbstractMonster m : __instance.monsters) {
                    // 仅强化精英级别的怪物，排除由于召唤产生的爪牙或杂鱼
                    if (m.type == AbstractMonster.EnemyType.ELITE) {
                        BasicMod.logger.info(">>> [恶魔传送门强化] 正在强化精英怪: " + m.name + " (ID: " + m.id + ")");
                        int oldMaxHp = m.maxHealth;

                        // 增加 30 点生命上限并回满该部分
                        m.increaseMaxHp(30, true);

                        // 赋予 3 点力量
                        AbstractDungeon.actionManager
                                .addToBottom(new ApplyPowerAction(m, m, new StrengthPower(m, 3), 3));

                        BasicMod.logger.info(">>> [恶魔传送门强化] " + m.name + " 强化完成：血上限从 " + oldMaxHp + " 变为 " + m.maxHealth
                                + "，额外获得 3 点力量。");
                    } else {
                        BasicMod.logger.info(">>> [恶魔传送门强化] 发现非精英单位: " + m.name + " (分类: " + m.type + ")，跳过强化逻辑。");
                    }
                }
            }
        }
    }
}
