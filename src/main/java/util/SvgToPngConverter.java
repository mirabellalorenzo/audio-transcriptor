package util;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import javax.imageio.ImageIO;

public class SvgToPngConverter {
    public static ImageView loadSvgAsImage(String iconName, double size) {
        try {
            String path = "/icons/ionicons/" + iconName + ".svg";
            URL resourceUrl = SvgToPngConverter.class.getResource(path);

            if (resourceUrl == null) {
                System.err.println("ERRORE: Icona SVG non trovata: " + path);
                return new ImageView();
            }

            // Carica l'SVG come InputStream
            InputStream svgInput = resourceUrl.openStream();
            if (svgInput == null) {
                System.err.println("ERRORE: InputStream nullo per: " + path);
                return new ImageView();
            }

            // Converti l'SVG in PNG
            PNGTranscoder transcoder = new PNGTranscoder();
            transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, (float) size);
            transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, (float) size);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            TranscoderInput transcoderInput = new TranscoderInput(svgInput);
            TranscoderOutput transcoderOutput = new TranscoderOutput(outputStream);
            transcoder.transcode(transcoderInput, transcoderOutput);

            // Converte il PNG generato in ImageView di JavaFX
            Image image = SwingFXUtils.toFXImage(ImageIO.read(new ByteArrayInputStream(outputStream.toByteArray())), null);
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(size);
            imageView.setFitHeight(size);
            return imageView;

        } catch (Exception e) {
            e.printStackTrace();
            return new ImageView();
        }
    }
}
