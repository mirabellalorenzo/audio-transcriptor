package view.components;

import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import util.SvgToPngConverter;

public class CustomButtonComponent extends Button {

    public enum ButtonType {
        PRIMARY, SECONDARY, OUTLINE
    }

    // Costruttore SENZA icona
    public CustomButtonComponent(String text, ButtonType type) {
        this(text, null, type); // Chiama il costruttore con icona passando `null`
    }

    // Costruttore CON icona opzionale
    public CustomButtonComponent(String text, String iconName, ButtonType type) {
        super(text);

        // Aggiunta dell'icona solo se fornita
        if (iconName != null && !iconName.isEmpty()) {
            ImageView icon = SvgToPngConverter.loadSvgAsImage(iconName, 18);
            setGraphic(icon);
        }

        setStyle(getButtonStyle(type));

        setOnMouseEntered(e -> setStyle(getHoverStyle(type)));
        setOnMouseExited(e -> setStyle(getButtonStyle(type)));

        setMinWidth(140);
        setMaxWidth(160);
    }

    private String getButtonStyle(ButtonType type) {
        return getCommonStyle() + getSpecificStyle(type);
    }

    private String getHoverStyle(ButtonType type) {
        return getCommonStyle() + getHoverSpecificStyle(type);
    }

    private String getCommonStyle() {
        return "-fx-font-size: 14px; " +
               "-fx-font-weight: bold; " +
               "-fx-border-radius: 25px; " +  // Forma a pillola
               "-fx-background-radius: 25px; " +  // Assicura bordi arrotondati
               "-fx-padding: 8px 16px; " +
               "-fx-cursor: hand; " +
               "-fx-border-width: 1px; ";  // Migliore visibilità
    }

    private String getSpecificStyle(ButtonType type) {
        switch (type) {
            case PRIMARY:
                return "-fx-background-color: black; " +
                       "-fx-text-fill: white; " +
                       "-fx-border-color: black;";
            case SECONDARY:
                return "-fx-background-color: #E0E0E0; " +
                       "-fx-text-fill: black; " +
                       "-fx-border-color: #E0E0E0;";
            case OUTLINE:
                return "-fx-background-color: transparent; " +
                       "-fx-border-color: black; " +
                       "-fx-text-fill: black;";
            default:
                return "";
        }
    }

    private String getHoverSpecificStyle(ButtonType type) {
        switch (type) {
            case PRIMARY:
                return "-fx-background-color: #222; " +
                        "-fx-text-fill: white; " +
                        "-fx-border-color: #222;";
            case SECONDARY:
                return "-fx-background-color: #D6D6D6; " +
                        "-fx-text-fill: black; " +
                        "-fx-border-color: #D6D6D6;";
            case OUTLINE:
                return "-fx-background-color: #E0E0E0; " +
                        "-fx-text-fill: black;" +
                        "-fx-border-color: black;";
            default:
                return "";
        }
    }
}
