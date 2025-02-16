package view.components;

import boundary.HomeBoundary;
import entity.Note;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import util.SvgToPngConverter;

import java.util.List;
import java.util.stream.Collectors;

public class FlatNotesListComponent extends VBox {
    public interface NoteSelectionListener {
        void onNoteSelected(Note note);
    }

    private final VBox notesContainer;
    private final NoteSelectionListener listener;
    private final List<Note> notes;

    public FlatNotesListComponent(HomeBoundary boundary, Stage primaryStage, List<Note> notes, NoteSelectionListener listener) {
        this.notes = notes;
        this.listener = listener;

        this.setStyle(
            "-fx-padding: 30px 200px; " + // Maggiore padding laterale per adattarsi allo schermo
            "-fx-spacing: 20; " +
            "-fx-background-color: white;"
        );

        // **Titolo e pulsante per creare una nuova nota**
        Label titleLabel = new Label("Notes");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #222;");

        Button newNoteButton = new Button("New Note");
        newNoteButton.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #E0E0E0; " +
            "-fx-border-width: 1px; " +
            "-fx-border-radius: 30px; " +
            "-fx-background-radius: 30px; " +
            "-fx-padding: 8px 16px; " +
            "-fx-font-size: 14px; " +
            "-fx-text-fill: #222; " +
            "-fx-cursor: hand;"
        );
        newNoteButton.setOnAction(e -> {
            Note newNote = boundary.createNewNote();
            if (newNote != null) {
                notes.add(0, newNote);
                refreshNotesList();
            }
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox headerBox = new HBox(10, titleLabel, spacer, newNoteButton);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        // **Barra di ricerca**
        TextField searchField = new TextField();
        searchField.setPromptText("Search notes...");
        searchField.setMaxWidth(Double.MAX_VALUE);
        searchField.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: transparent; " +
            "-fx-font-size: 14px; " +
            "-fx-padding: 8px 12px; " +
            "-fx-border-radius: 30px; " +
            "-fx-pref-width: 100%; " +
            "-fx-text-fill: black;" +
            "-fx-prompt-text-fill: #999;"
        );

        ImageView searchIcon = SvgToPngConverter.loadSvgAsImage("search-outline", 20);
        searchIcon.setFitHeight(20);
        searchIcon.setFitWidth(20);
        HBox.setHgrow(searchField, Priority.ALWAYS);
        HBox searchBox = new HBox(10, searchIcon, searchField);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setStyle(
            "-fx-background-color: #FFFFFF; " +
            "-fx-border-radius: 30px; " +
            "-fx-border-color: #E0E0E0; " +
            "-fx-padding: 8px 16px; " +
            "-fx-pref-width: 100%; "
        );

        // **Contenitore note con bordo più chiaro**
        notesContainer = new VBox(5);
        notesContainer.setPadding(new Insets(10));
        notesContainer.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-radius: 12px; " +
            "-fx-border-color: #ddd; " + // Bordo più chiaro per il contenitore
            "-fx-border-width: 1px;"
        );

        refreshNotesList();

        ScrollPane scrollPane = new ScrollPane(notesContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle(
            "-fx-background: white; " +
            "-fx-border-color: transparent; " +
            "-fx-background-insets: 0; " +
            "-fx-padding: 0;"
        );

        this.getChildren().addAll(headerBox, searchBox, scrollPane);

        // **Filtraggio note in base alla barra di ricerca**
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            List<Note> filteredNotes = notes.stream()
                .filter(note -> note.getTitle().toLowerCase().contains(newText.toLowerCase()))
                .collect(Collectors.toList());
            refreshNotesList(filteredNotes);
        });
    }

    public void refreshNotesList() {
        refreshNotesList(notes);
    }

    private void refreshNotesList(List<Note> noteList) {
        notesContainer.getChildren().clear();
        for (int i = 0; i < noteList.size(); i++) {
            Note note = noteList.get(i);
            HBox noteItem = createNoteItem(note, i % 2 == 0);
            notesContainer.getChildren().add(noteItem);
        }
    }

    private HBox createNoteItem(Note note, boolean isEven) {
        HBox noteItem = new HBox();
        noteItem.setSpacing(10);
        noteItem.setPadding(new Insets(12, 15, 12, 15));
        noteItem.setStyle(
            "-fx-background-color: " + (isEven ? "#FAFAFA;" : "#F0F0F0;") +
            "-fx-border-radius: 8px; " +
            "-fx-padding: 12px; " +
            "-fx-cursor: hand;"
        );
        noteItem.setOnMouseClicked(e -> listener.onNoteSelected(note));

        VBox textContainer = new VBox(5);
        Label title = new Label(note.getTitle());
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #333;");
        
        Label preview = new Label(trimContent(note.getContent(), 80));
        preview.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        preview.setWrapText(true);
        
        textContainer.getChildren().addAll(title, preview);
        noteItem.getChildren().add(textContainer);

        return noteItem;
    }

    private String trimContent(String content, int maxLength) {
        if (content.length() > maxLength) {
            return content.substring(0, maxLength) + "...";
        }
        return content;
    }
}
