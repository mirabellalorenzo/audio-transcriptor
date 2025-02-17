package util;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import javax.imageio.ImageIO;
import org.apache.batik.transcoder.SVGAbstractTranscoder;

public class SvgToPngConverter {
    private static final Logger LOGGER = LoggerFactory.getLogger(SvgToPngConverter.class);

    public static ImageView loadSvgAsImage(String iconName, double size) {
        try {
            String path = "/icons/ionicons/" + iconName + ".svg";
            URL resourceUrl = SvgToPngConverter.class.getResource(path);

            if (resourceUrl == null) {
                LOGGER.warn("ERRORE: Icona SVG non trovata: {}", path);
                return new ImageView();
            }

            InputStream svgInput = resourceUrl.openStream();
            if (svgInput == null) {
                LOGGER.warn("ERRORE: InputStream nullo per: {}", path);
                return new ImageView();
            }

            PNGTranscoder transcoder = new PNGTranscoder();
            transcoder.addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH, (float) size);
            transcoder.addTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT, (float) size);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            TranscoderInput transcoderInput = new TranscoderInput(svgInput);
            TranscoderOutput transcoderOutput = new TranscoderOutput(outputStream);
            transcoder.transcode(transcoderInput, transcoderOutput);

            Image image = SwingFXUtils.toFXImage(ImageIO.read(new ByteArrayInputStream(outputStream.toByteArray())), null);
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(size);
            imageView.setFitHeight(size);
            return imageView;

        } catch (Exception e) {
            LOGGER.error("Errore durante la conversione SVG->PNG: {}", e.getMessage(), e);
            return new ImageView();
        }
    }
}
