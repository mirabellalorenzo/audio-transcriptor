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
        
        // Carichiamo il file CSS
        this.getStylesheets().add(getClass().getResource("/styles/flatNotesListComponent.css").toExternalForm());
        this.getStyleClass().add("root");

        // **Titolo e pulsante per creare una nuova nota**
        Label titleLabel = new Label("Notes");
        titleLabel.getStyleClass().add("label-title");

        Button newNoteButton = new Button("New Note");
        newNoteButton.getStyleClass().add("button-new-note");
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
        searchField.getStyleClass().add("search-box");
        searchField.setMaxWidth(Double.MAX_VALUE);

        ImageView searchIcon = SvgToPngConverter.loadSvgAsImage("search-outline", 20);
        searchIcon.setFitHeight(20);
        searchIcon.setFitWidth(20);
        HBox.setHgrow(searchField, Priority.ALWAYS);
        HBox searchBox = new HBox(10, searchIcon, searchField);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.getStyleClass().add("search-container");

        // **Contenitore note**
        notesContainer = new VBox(5);
        notesContainer.getStyleClass().add("notes-container");

        refreshNotesList();

        ScrollPane scrollPane = new ScrollPane(notesContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("scroll-pane");

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
        
        // Applica la classe CSS appropriata
        noteItem.getStyleClass().add("note-item");
        noteItem.getStyleClass().add(isEven ? "note-item-even" : "note-item-odd");

        noteItem.setOnMouseClicked(e -> listener.onNoteSelected(note));

        VBox textContainer = new VBox(5);
        Label title = new Label(note.getTitle());
        title.getStyleClass().add("note-title");
        
        Label preview = new Label(trimContent(note.getContent(), 80));
        preview.getStyleClass().add("note-preview");
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
