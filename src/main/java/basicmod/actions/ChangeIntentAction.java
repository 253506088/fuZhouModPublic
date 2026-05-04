package basicmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class ChangeIntentAction extends AbstractGameAction {
    private AbstractMonster m;

    public ChangeIntentAction(AbstractMonster m) {
        this.m = m;
    }

    @Override
    public void update() {
        if (m != null && !m.isDeadOrEscaped()) {
            int oldDamage = calculateCurrentIntentDamage(m);

            // 重新骰点意图
            m.rollMove();
            m.createIntent();

            // 检查新意图。如果伤害变高了，重试几次
            int retries = 5;
            while (retries > 0) {
                int newDamage = calculateCurrentIntentDamage(m);
                
                // 如果原意图不是攻击，或者新意图由于某种原因不是攻击，或者新意图伤害更低
                if (newDamage == -1 || oldDamage == -1 || newDamage <= oldDamage) {
                    break;
                }
                
                m.rollMove();
                m.createIntent();
                retries--;
            }
        }
        this.isDone = true;
    }

    private int calculateCurrentIntentDamage(AbstractMonster monster) {
        // STS 的 intentDmg 基础值存放在 m.intentDmg 中
        // 实际显示给玩家的是包含了力量等修正后的值
        // 这里通过反射或内置方法获取真实的显示伤害
        if (monster.intent == AbstractMonster.Intent.ATTACK || 
            monster.intent == AbstractMonster.Intent.ATTACK_BUFF || 
            monster.intent == AbstractMonster.Intent.ATTACK_DEBUFF || 
            monster.intent == AbstractMonster.Intent.ATTACK_DEFEND) {
            
            int base = monster.getIntentDmg();
            int mult = 1;
            try {
                // Use reflection to access private field intentMultiAmt
                java.lang.reflect.Field multiAmtField = AbstractMonster.class.getDeclaredField("intentMultiAmt");
                multiAmtField.setAccessible(true);
                int intentMultiAmt = (int) multiAmtField.get(monster);
                if (intentMultiAmt > 0) {
                    mult = intentMultiAmt;
                }
            } catch (Exception e) {
                // Ignore
            }
            return base * mult;
        }
        return -1; // 非攻击意图
    }
}
