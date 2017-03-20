package pers.sunke.applets;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class AddWatermarkImage {

	public static void main(String[] args) {
		if (args.length < 3) {
			args = new String[3];
			args[0] = "H:/source.jpg";
			args[1] = "H:/watermark.jpg";
			args[2] = "H:/target.jpg";
		}

		File sourceFile = new File(args[0]), watermarkFile = new File(args[1]), targetFile = new File(args[2]);
		float alpha = 0.5f, scale = 1;

		try {
			BufferedImage sourceImage = ImageIO.read(sourceFile);

			int sourceWideth = sourceImage.getWidth(null);
			int sourceHeight = sourceImage.getHeight(null);

			BufferedImage targetImage = new BufferedImage(sourceWideth, sourceHeight, BufferedImage.TYPE_INT_RGB);

			Graphics2D graphics2D = targetImage.createGraphics();
			graphics2D.drawImage(sourceImage, 0, 0, sourceWideth, sourceHeight, null);

			Image watermarkImage = ImageIO.read(watermarkFile);
			int watermarkWidth = Integer.valueOf(new Float(watermarkImage.getWidth(null) * scale).intValue());
			int watermarkHeight = Integer.valueOf(new Float(watermarkImage.getHeight(null) * scale).intValue());

			graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
			graphics2D.drawImage(watermarkImage, sourceWideth - watermarkWidth, 0, watermarkWidth, watermarkHeight,
					null);
			graphics2D.dispose();

			ImageIO.write((BufferedImage) targetImage, "JPEG", targetFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
