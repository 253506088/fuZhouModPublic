import os
from PIL import Image

# pip install Pillow
# conda activate ui-ux-skill
# cd D:\code\my\2\fuZhouMod\处理卡牌图片工具\工序1_py处理到合适的比例
# python crop_cards.py

def process_images(input_path):
    # 设定目标尺寸
    size_large = (500, 380)
    size_small = (250, 190)
    target_ratio = 25 / 19
    
    # 创建输出文件夹
    output_folder = os.path.join(input_path, "结果")
    if not os.path.exists(output_folder):
        os.makedirs(output_folder)
    
    # 支持的图片格式
    valid_extensions = ('.jpg', '.jpeg', '.png', '.webp', '.bmp')

    print(f"开始处理路径: {input_path}")
    
    count = 0
    for filename in os.listdir(input_path):
        if filename.lower().endswith(valid_extensions):
            img_path = os.path.join(input_path, filename)
            try:
                with Image.open(img_path) as img:
                    # 转为 RGB 模式（防止 RGBA 转换为 JPG 时报错）
                    if img.mode in ("RGBA", "P"):
                        img = img.convert("RGB")
                    
                    curr_width, curr_height = img.size
                    curr_ratio = curr_width / curr_height
                    
                    # --- 计算裁剪区域 (Center Crop) ---
                    if curr_ratio > target_ratio:
                        # 图片太宽，按高度缩放，裁剪左右
                        new_width = int(target_ratio * curr_height)
                        offset = (curr_width - new_width) // 2
                        crop_box = (offset, 0, curr_width - offset, curr_height)
                    else:
                        # 图片太高，按宽度缩放，裁剪上下
                        new_height = int(curr_width / target_ratio)
                        offset = (curr_height - new_height) // 2
                        crop_box = (0, offset, curr_width, curr_height - offset)
                    
                    # 执行裁剪
                    img_cropped = img.crop(crop_box)
                    
                    # --- 生成并保存 500x380 ---
                    img_500 = img_cropped.resize(size_large, Image.LANCZOS)
                    img_500.save(os.path.join(output_folder, filename))
                    
                    # --- 生成并保存 250x190 ---
                    name_part, ext_part = os.path.splitext(filename)
                    small_filename = f"{name_part}Small{ext_part}"
                    img_250 = img_cropped.resize(size_small, Image.LANCZOS)
                    img_250.save(os.path.join(output_folder, small_filename))
                    
                    count += 1
                    print(f"已处理: {filename}")
                    
            except Exception as e:
                print(f"处理 {filename} 时出错: {e}")

    print(f"\n处理完成！共处理 {count} 张图片。结果保存在: {output_folder}")

if __name__ == "__main__":
    # 在这里输入你的文件夹路径
    path = input("请输入图片所在的文件夹路径: ").strip()
    if os.path.isdir(path):
        process_images(path)
    else:
        print("错误：路径不存在，请检查后重新运行。")