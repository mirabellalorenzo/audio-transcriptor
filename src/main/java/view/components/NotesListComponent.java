package view.components;

import boundary.HomeBoundary;
import entity.Note;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import util.SvgToPngConverter;
import javafx.scene.image.ImageView;

import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotesListComponent extends VBox {
    public interface NoteSelectionListener {
        void onNoteSelected(Note note);
    }

    private HomeBoundary boundary;
    private Stage primaryStage;
    private final VBox notesContainer;
    private VBox selectedNoteCard = null;
    private final NoteSelectionListener listener;
    private final List<Note> notes;
    
    private static final Logger logger = LoggerFactory.getLogger(NotesListComponent.class);

    // Modifica del costruttore: ora richiede anche boundary e primaryStage per poter creare una nuova nota
    public NotesListComponent(HomeBoundary boundary, Stage primaryStage, List<Note> notes, NoteSelectionListener listener) {
        this.boundary = boundary;
        this.primaryStage = primaryStage;
        this.notes = notes;
        this.listener = listener;
        this.setStyle("-fx-padding: 35px 0px 0px 35px; -fx-spacing: 15; -fx-background-color: white; -fx-border-radius: 15px;");
        
        // Header con titolo "Notes" e pulsante "New Note" (a destra)
        Label titleLabel = new Label("Notes");
        titleLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #222;");
        
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
            logger.info("New Note button clicked");
            Note newNote = boundary.createNewNote();
            if (newNote != null) {
                notes.add(newNote);
                addNoteAndSelect(newNote);
            }
        });
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        HBox headerBox = new HBox(10, titleLabel, spacer, newNoteButton);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        // Barra di ricerca
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
        
        notesContainer = new VBox(10);
        notesContainer.setStyle("-fx-spacing: 10; -fx-padding: 5;");
        loadNotes(notes, listener);

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

        scrollPane.getStylesheets().add(getClass().getResource("/styles/scrollbar.css").toExternalForm());

        this.getChildren().addAll(headerBox, searchBox, scrollPane);


        // Filtraggio note in base al testo immesso nella barra di ricerca
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            List<Note> filteredNotes = notes.stream()
                .filter(note -> note.getTitle().toLowerCase().contains(newText.toLowerCase()))
                .collect(Collectors.toList());
            notesContainer.getChildren().clear();
            loadNotes(filteredNotes, listener);
        });
    }

    private void loadNotes(List<Note> notes, NoteSelectionListener listener) {
        for (Note note : notes) {
            VBox noteCard = createNoteCard(note);

            noteCard.setOnMouseEntered(e -> {
                if (selectedNoteCard != noteCard) {
                    noteCard.setStyle(
                        "-fx-background-color: #EEEEEE; " +
                        "-fx-padding: 20px; " +
                        "-fx-border-radius: 20px; " +
                        "-fx-border-color: #DADADA; " +
                        "-fx-background-insets: 0; " + 
                        "-fx-background-radius: 20px; " +
                        "-fx-spacing: 10px; " +
                        "-fx-cursor: hand;"
                    );
                }
            });

            noteCard.setOnMouseExited(e -> {
                if (selectedNoteCard != noteCard) {
                    noteCard.setStyle(
                        "-fx-background-color: #fcfbfc; " +
                        "-fx-padding: 20px; " +
                        "-fx-border-radius: 20px; " +
                        "-fx-border-color: #E0E0E0; " +
                        "-fx-background-insets: 0; " + 
                        "-fx-background-radius: 20px; " +
                        "-fx-spacing: 10px; " +
                        "-fx-cursor: hand;"
                    );
                }
            });

            noteCard.setOnMouseClicked(e -> {
                if (selectedNoteCard != null) {
                    selectedNoteCard.setStyle(
                        "-fx-background-color: #fcfbfc; " +
                        "-fx-padding: 20px; " +
                        "-fx-border-radius: 20px; " +
                        "-fx-border-color: #E0E0E0; " +
                        "-fx-background-insets: 0; " + 
                        "-fx-background-radius: 20px; " +
                        "-fx-spacing: 10px; " +
                        "-fx-cursor: hand;"
                    );
                }

                selectedNoteCard = noteCard;

                selectedNoteCard.setStyle(
                    "-fx-background-color: #edf6ff; " +
                    "-fx-padding: 20px; " +
                    "-fx-border-radius: 20px; " +
                    "-fx-border-color: #99c9ef; " +
                    "-fx-background-insets: 0; " + 
                    "-fx-background-radius: 20px; " +
                    "-fx-spacing: 10px; " +
                    "-fx-cursor: hand;"
                );

                listener.onNoteSelected(note);
            });

            notesContainer.getChildren().add(noteCard);
        }
    }    

    public void refreshNotesList() {
        notesContainer.getChildren().clear();
        loadNotes(notes, listener);
    }    

    public void addNoteAndSelect(Note newNote) {
        if (!notes.contains(newNote)) {
            notes.add(0, newNote);
        }
    
        VBox noteCard = createNoteCard(newNote);
        notesContainer.getChildren().add(0, noteCard);
    
        noteCard.setOnMouseClicked(e -> {
            if (selectedNoteCard != null) {
                selectedNoteCard.setStyle(
                    "-fx-background-color: #fcfbfc; " +
                    "-fx-padding: 20px; " +
                    "-fx-border-radius: 20px; " +
                    "-fx-border-color: #E0E0E0; " +
                    "-fx-background-insets: 0; " + 
                    "-fx-background-radius: 20px; " +
                    "-fx-spacing: 10px; " +
                    "-fx-cursor: hand;"
                );
            }
        
            selectedNoteCard = noteCard;
            selectedNoteCard.setStyle(
                "-fx-background-color: #edf6ff; " +
                "-fx-padding: 20px; " +
                "-fx-border-radius: 20px; " +
                "-fx-border-color: #99c9ef; " +
                "-fx-background-insets: 0; " + 
                "-fx-background-radius: 20px; " +
                "-fx-spacing: 10px; " +
                "-fx-cursor: hand;"
            );
        
            listener.onNoteSelected(newNote);
        });
    
        // Seleziono automaticamente la nuova nota
        noteCard.getOnMouseClicked().handle(null);
    }    
    
    private VBox createNoteCard(Note note) {
        VBox noteCard = new VBox();
        noteCard.setStyle(
            "-fx-background-color: #fcfbfc; " +
            "-fx-padding: 20px; " +
            "-fx-border-radius: 20px; " +
            "-fx-border-color: #E0E0E0; " +
            "-fx-background-insets: 0; " +
            "-fx-background-radius: 20px; " +
            "-fx-spacing: 10px; " +
            "-fx-cursor: hand;"
        );

        noteCard.setMinWidth(250);
        
        Label title = new Label(note.getTitle());
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #333;");
        title.setMaxWidth(Double.MAX_VALUE);
        title.setWrapText(true);

        Label preview = new Label(note.getContent());
        preview.setWrapText(true);
        preview.setMaxWidth(Double.MAX_VALUE);
        preview.setPrefHeight(34);
        preview.setStyle("-fx-text-fill: #666; -fx-font-size: 13px;");

        noteCard.getChildren().addAll(title, preview);
        return noteCard;
    }
}
