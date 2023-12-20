import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;

import static java.lang.Math.round;

public class SHARPEN {
    public static void main(String[] args) {
        String[] imagePaths = {
                "src/aabdom.jpg",   // https://medpix.nlm.nih.gov/case?id=c0a43269-30d7-4afd-8cde-d0028bd5ce04
                "src/ahip.jpg",    // https://medpix.nlm.nih.gov/case?id=dcdd0d66-67c9-4236-80e7-77fae4cb2257
                "src/ateeth.jpg"  //https://medpix.nlm.nih.gov/case?id=829f7a67-0aad-417f-8d90-daba4af2096b
        };
        for(String Image : imagePaths){
            try {
                String imagePathShort = extractFileName(Image);
                String pathText = "src" + File.separator + imagePathShort + "_SHARPEN_results";
                Files.createDirectories(Paths.get(pathText));
                File currentDirectory = new File(pathText);
                for (File subfile : currentDirectory.listFiles()) {
                    if (!subfile.isDirectory()) {
                        subfile.delete();
                    }
                }

                BufferedImage originalImage = ImageIO.read(new File(Image));
                for (double i = 0.5; i < 4; i += 0.5) {
                    double sharpeningIntensity = (double)round(i*100)/100;
                    System.out.println("loading image \"" + imagePathShort + "\" with gamma value " + sharpeningIntensity + "...");
                    BufferedImage sharpenedImage = sharpenImage(originalImage, sharpeningIntensity);
                    ImageIO.write(sharpenedImage, "png", new File("src" + File.separator + imagePathShort + "_SHARPEN_results" + File.separator + imagePathShort + "_result_" + sharpeningIntensity + ".png"));
                }



            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String extractFileName(String filePath) {
        int lastSeparatorIndex = filePath.lastIndexOf("/");
        int dotIndex = filePath.indexOf('.', lastSeparatorIndex + 1);
        return filePath.substring(lastSeparatorIndex + 1, dotIndex);
    }

    private static BufferedImage sharpenImage(BufferedImage image, double intensity) {
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage sharpenedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

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
