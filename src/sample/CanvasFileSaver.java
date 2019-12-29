package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

public class CanvasFileSaver {
  static int fileCount=0;
   static void saveToFile(Canvas canvas, String filename) {

filename+=(++fileCount)+".png";
    File file = new File(filename);

                if(file !=null)

    {
        try {
            WritableImage writableImage = new WritableImage((int)canvas.getWidth(), (int)canvas.getHeight());
            canvas.snapshot(null, writableImage);
            RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
            ImageIO.write(renderedImage, "png", file);
            System.out.println("file : "+filename+ " saved" );
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
}
}
