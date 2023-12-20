import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;

import static java.lang.Math.round;

public class SHARPEN {
    public static void main(String[] args) {
        try {
            Files.createDirectories(Paths.get("src/SHARPENresults"));
            File currentDirectory = new File("src/SHARPENresults/");
            for (File subfile : currentDirectory.listFiles()) {
                if (!subfile.isDirectory()) {
                    subfile.delete();
                }
            }

            BufferedImage originalImage = ImageIO.read(new File("src/synpic33197.jpg"));


            for (double i = 0.5; i < 4; i += 0.5) {
                double sharpeningIntensity = (double)round(i*100)/100;
                System.out.println(sharpeningIntensity);
                BufferedImage sharpenedImage = sharpenImage(originalImage, sharpeningIntensity);
                ImageIO.write(sharpenedImage, "png", new File("src/SHARPENresults/sharpened_image" + sharpeningIntensity + ".png"));
            }



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage sharpenImage(BufferedImage image, double intensity) {
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage sharpenedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Define a sharpening kernel based on the intensity
        double[] sharpeningKernel = {
                -intensity, -intensity, -intensity,
                -intensity,  1 + 8 * intensity, -intensity,
                -intensity, -intensity, -intensity
        };

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int[] values = applyKernel(image, x, y, sharpeningKernel);
                int rgb = (clamp(values[0]) << 16) | (clamp(values[1]) << 8) | clamp(values[2]);
                sharpenedImage.setRGB(x, y, rgb);
            }
        }

        return sharpenedImage;
    }

    private static int[] applyKernel(BufferedImage image, int x, int y, double[] kernel) {
        int[] result = new int[3];
        int k = 0;

        for (int j = -1; j <= 1; j++) {
            for (int i = -1; i <= 1; i++) {
                int rgb = image.getRGB(x + i, y + j);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                result[0] += red * kernel[k];
                result[1] += green * kernel[k];
                result[2] += blue * kernel[k];
                k++;
            }
        }

        return result;
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(value, 255));
    }
}
