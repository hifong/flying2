package com.flying.common.image;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

/**
 * @author Eric Xu
 *
 */
public final class ImageUtils {
	private final static Logger logger = Logger.getLogger(ImageUtils.class);
	
	public static class ImageInfo {
		private Integer width;
		private Integer height;
		private Integer size; //KB
		
		private ImageInfo(Integer width, Integer height, Integer size) {
			this.width = width;
			this.height = height;
			this.size = size;
		}
		
		public Integer getWidth() {
			return width;
		}
		public void setWidth(Integer width) {
			this.width = width;
		}
		public Integer getHeight() {
			return height;
		}
		public void setHeight(Integer height) {
			this.height = height;
		}
		public Integer getSize() {
			return size;
		}
		public void setSize(Integer size) {
			this.size = size;
		}
		
	}
	
	public static ImageInfo getImageInfo(File file) {
		try {
			Image src = ImageIO.read(file);
			int width = src.getWidth(null);
			int height = src.getHeight(null);
			Long fileSize = (file.length() / 1024);
			int size = fileSize.intValue();
			return new ImageInfo(width, height, size);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 图片水印
	 * 
	 * @param pressImg 水印图片
	 * @param targetImg  目标图片
	 * @param x 修正值 默认在中间
	 * @param y 修正值 默认在中间
	 * @param alpha 透明度
	 */
	public final static void pressImage(String pressImg, String targetImg, float alpha) {
		try {
			File img = new File(targetImg);
			Image src = ImageIO.read(img);
			int wideth = src.getWidth(null);
			int height = src.getHeight(null);
			BufferedImage image = new BufferedImage(wideth, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = image.createGraphics();
			g.drawImage(src, 0, 0, wideth, height, null);
			// 水印文件
			Image src_biao = ImageIO.read(new File(pressImg));
			int wideth_biao = src_biao.getWidth(null);
			int height_biao = src_biao.getHeight(null);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
			g.drawImage(src_biao, (wideth - wideth_biao), (height - height_biao), wideth_biao, height_biao, null);
			// 水印文件结束
			g.dispose();
			ImageIO.write((BufferedImage) image, "jpg", img);
		} catch (Exception e) {
			logger.error("pressImage fail!", e);
			e.printStackTrace();
		}
	}

	/**
	 * 文字水印
	 * 
	 * @param pressText 水印文字
	 * @param targetImg  目标图片
	 * @param fontName 字体名称
	 * @param fontStyle  字体样式
	 * @param color 字体颜色
	 * @param fontSize 字体大小
	 * @param x 修正值
	 * @param y 修正值
	 * @param alpha 透明度
	 */
	public static boolean pressText(String pressText, String targetImg, 
			String fontName, int fontStyle, Color color,
			int fontSize, int x, int y, float alpha) {
		try {
			File img = new File(targetImg);
			Image src = ImageIO.read(img);
			int width = src.getWidth(null);
			int height = src.getHeight(null);
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = image.createGraphics();
			g.drawImage(src, 0, 0, width, height, null);
			g.setColor(color);
			g.setFont(new Font(fontName, fontStyle, fontSize));
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
			g.drawString(pressText, (width - (getLength(pressText) * fontSize)) / 2 + x, (height - fontSize) / 2 + y);
			g.dispose();
			ImageIO.write((BufferedImage) image, "jpg", img);
			return true;
		} catch (Exception e) {
			logger.error("pressText fail!", e);
			return false;
		}
	}

	public static boolean resizeImage(String sourceFileName, int height, int width, boolean bb) {
		try(FileOutputStream fos = new FileOutputStream(sourceFileName)) {
			resizeImage(sourceFileName, fos, height, width, bb);
			return true;
		} catch (Exception e) {
			logger.error("resizeImage fail!", e);
			return false;
		}
	}

	/**
	 * 缩放
	 * 
	 * @param sourceFileName 图片路径
	 * @param height  高度
	 * @param width 宽度
	 * @param bb 比例不对时是否需要补白
	 */
	public static void resizeImage(String sourceFileName, OutputStream os, int height, int width, boolean bb) {
		try {
			double ratio = 0.0; // 缩放比例
			File sourceFile = new File(sourceFileName);
			BufferedImage bi = ImageIO.read(sourceFile);
			Image itemp = bi.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			// 计算比例
			if ((bi.getHeight() > height) || (bi.getWidth() > width)) {
				if (bi.getHeight() > bi.getWidth()) {
					ratio = height * 1.0 / bi.getHeight();
				} else {
					ratio = width * 1.0 / bi.getWidth();
				}
				AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(ratio, ratio), null);
				itemp = op.filter(bi, null);
			}
			if (bb) {
				BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				Graphics2D g = image.createGraphics();
				g.setColor(Color.white);
				g.fillRect(0, 0, width, height);
				if (width == itemp.getWidth(null))
					g.drawImage(itemp, 0, (height - itemp.getHeight(null)) / 2, itemp.getWidth(null),
							itemp.getHeight(null), Color.white, null);
				else
					g.drawImage(itemp, (width - itemp.getWidth(null)) / 2, 0, itemp.getWidth(null),
							itemp.getHeight(null), Color.white, null);
				g.dispose();
				itemp = image;
			}
			ImageIO.write((BufferedImage) itemp, "jpg", os);
		} catch (IOException e) {
			logger.error("pressImage fail!", e);
		}
	}

	public static BufferedImage zoomInImage(BufferedImage originalImage, Integer times) {
		int width = originalImage.getWidth() * times;
		int height = originalImage.getHeight() * times;
		BufferedImage newImage = new BufferedImage(width, height, originalImage.getType());
		Graphics g = newImage.getGraphics();
		g.drawImage(originalImage, 0, 0, width, height, null);
		g.dispose();
		return newImage;
	}

	/**
	 * 对图片进行放大
	 * 
	 * @param srcPath
	 *            原始图片路径(绝对路径)
	 * @param newPath
	 *            放大后图片路径（绝对路径）
	 * @param times
	 *            放大倍数
	 * @return 是否放大成功
	 */
	public static boolean zoomInImage(String srcPath, String newPath, Integer times) {
		BufferedImage bufferedImage = null;
		try {
			File of = new File(srcPath);
			if (of.canRead()) {
				bufferedImage = ImageIO.read(of);
			}
		} catch (IOException e) {
			// TODO: 打印日志
			return false;
		}
		if (bufferedImage != null) {
			bufferedImage = zoomInImage(bufferedImage, times);
			try {
				// TODO: 这个保存路径需要配置下子好一点
				ImageIO.write(bufferedImage, "JPG", new File(newPath)); // 保存修改后的图像,全部保存为JPG格式
			} catch (IOException e) {
				// TODO 打印错误信息
				return false;
			}
		}
		return true;
	}

	/**
	 * 对图片进行缩小
	 * 
	 * @param originalImage
	 *            原始图片
	 * @param times
	 *            缩小倍数
	 * @return 缩小后的Image
	 */
	public static BufferedImage zoomOutImage(BufferedImage originalImage, Integer times) {
		int width = originalImage.getWidth() / times;
		int height = originalImage.getHeight() / times;
		BufferedImage newImage = new BufferedImage(width, height, originalImage.getType());
		Graphics g = newImage.getGraphics();
		g.drawImage(originalImage, 0, 0, width, height, null);
		g.dispose();
		return newImage;
	}

	/**
	 * 对图片进行缩小
	 * 
	 * @param srcPath  源图片路径（绝对路径）
	 * @param newPath 新图片路径（绝对路径）
	 * @param times  缩小倍数
	 * @return 保存是否成功
	 */
	public static boolean zoomOutImage(String srcPath, String newPath, Integer times) {
		BufferedImage bufferedImage = null;
		try {
			File of = new File(srcPath);
			if (of.canRead()) {
				bufferedImage = ImageIO.read(of);
			}
		} catch (IOException e) {
			return false;
		}
		if (bufferedImage != null) {
			bufferedImage = zoomOutImage(bufferedImage, times);
			try {
				ImageIO.write(bufferedImage, "JPG", new File(newPath)); // 保存修改后的图像,全部保存为JPG格式
			} catch (IOException e) {
				// TODO 打印错误信息
				return false;
			}
		}
		return true;
	}

	private static int getLength(String text) {
		int length = 0;
		for (int i = 0; i < text.length(); i++) {
			if (new String(text.charAt(i) + "").getBytes().length > 1) {
				length += 2;
			} else {
				length += 1;
			}
		}
		return length / 2;
	}

	public static void main(String[] args) throws Exception {
		// pressImage("C:\\TMP\\logo.png","C:\\TMP\\t.jpg",0.4f);
		resizeImage("C:\\temp\\2.jpg", 562, 750, true);
	}
}