import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.lang.Math.round;

public class GAMMA {

    public static void main(String[] args) {
        String[] imagePaths = {
                "src/aabdom.jpg",   // https://medpix.nlm.nih.gov/case?id=c0a43269-30d7-4afd-8cde-d0028bd5ce04
                "src/ahip.jpg",    // https://medpix.nlm.nih.gov/case?id=dcdd0d66-67c9-4236-80e7-77fae4cb2257
                "src/ateeth.jpg"  //https://medpix.nlm.nih.gov/case?id=829f7a67-0aad-417f-8d90-daba4af2096b
        };

        for(String Image : imagePaths){
            try {
                String imagePathShort = extractFileName(Image);
                String pathText = "src" + File.separator + imagePathShort + "_GAMMA_results";
                Files.createDirectories(Paths.get(pathText));
                File currentDirectory = new File(pathText);

                for (File subfile : currentDirectory.listFiles()) {
                    if (!subfile.isDirectory()) {
                        subfile.delete();
                    }
                }

                BufferedImage originalImage = ImageIO.read(new File(Image));
                for (double i = 0.2; i < 3; i += 0.3) {
                    double gamma = (double)round(i*100)/100;
                    System.out.println("loading image \"" + imagePathShort + "\" with gamma value " + gamma + "...");

                    BufferedImage gammaCorrectedImage = adjustGamma(originalImage, gamma);
                    ImageIO.write(gammaCorrectedImage, "png", new File("src" + File.separator + imagePathShort + "_GAMMA_results" + File.separator + imagePathShort + "_result_" + gamma + ".png"));

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
