import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.lang.Math.round;

public class GAMMA {

    public static void main(String[] args) {
        try {
            Files.createDirectories(Paths.get("src/GAMMAresults"));
            File currentDirectory = new File("src/GAMMAresults/");
            for (File subfile : currentDirectory.listFiles()) {
                if (!subfile.isDirectory()) {
                    subfile.delete();
                }
            }

            BufferedImage originalImage = ImageIO.read(new File("src/synpic33197.jpg"));
            for (double i = 0.2; i < 3; i += 0.2) {
                double gamma = (double)round(i*100)/100;
                System.out.println(gamma);
                BufferedImage gammaCorrectedImage = adjustGamma(originalImage, gamma);
                ImageIO.write(gammaCorrectedImage, "png", new File("src/GAMMAresults/result" + gamma + ".png"));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static BufferedImage adjustGamma(BufferedImage image, double gamma) {
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage adjustedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                int adjustedRed = (int) (255 * Math.pow(red / 255.0, gamma));
                int adjustedGreen = (int) (255 * Math.pow(green / 255.0, gamma));
                int adjustedBlue = (int) (255 * Math.pow(blue / 255.0, gamma));

                int adjustedRGB = (adjustedRed << 16) | (adjustedGreen << 8) | adjustedBlue;

                adjustedImage.setRGB(x, y, adjustedRGB);
            }
        }

        return adjustedImage;
    }
}
