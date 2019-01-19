
package lab18;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import static javafx.scene.text.Font.getFamilies;
import static javafx.scene.text.Font.getFontNames;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;


public class Lab18 extends Application {
    
   private Gson gson;
    private int col = 1;
    private File file = new File("data.txt");
    private static boolean flag = true;
    public static String filePath;
    private Clipboard clipboard = Clipboard.getSystemClipboard();
    private ClipboardContent content = new ClipboardContent();
    DropShadow shadow = new DropShadow();
    
    
    public Settings onLoad() {
        File file = new File ("data.txt");
        Settings settings = new  Settings();
        if (file.exists())
        {
            try
            {
                Gson gson = new Gson(); 
                BufferedReader br = new BufferedReader(  
                        new FileReader(file));
                               
                settings  = gson.fromJson(br, Settings.class);
            }
            catch (IOException e) 
            {  
                e.printStackTrace();  
            }  
        }
        else 
        {
            System.out.println("Error file not found");
            try {
                file.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(Lab18.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return settings;
    }
    
    @Override
    public void start(Stage primaryStage) {
        Settings load = onLoad();
        TextArea textArea = new TextArea();
        
        ToolBar toolbar = new ToolBar();
        
        createButtons(toolbar,primaryStage,textArea);
        textArea.positionCaret(textArea.getText().length());//so you dont type in front of the "please type here" 
        BorderPane root = new BorderPane();
        final VBox vbox = new VBox();
        
        Scene scene = new Scene(root,load.getWidth(),load.getHeight());
        scene.setFill(Color.BLUE);
        primaryStage.setTitle("Text Editor");
        scene.getStylesheets().add(getClass().getResource("stylesheet.css").toExternalForm());
        
        root.setBottom(StatusBar(textArea));
        root.setTop(vbox);
        root.setCenter(textArea);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        
        
        startMenus(primaryStage, textArea,vbox, toolbar);

        primaryStage.setOnCloseRequest((WindowEvent ev) -> {
            System.out.println("Window closing");
            saveSettings(textArea.getText(), primaryStage.getHeight(), primaryStage.getWidth());
            Platform.exit();
            System.exit(0);
        });
    }

    public void createButtons(ToolBar toolbar, Stage primaryStage, TextArea textArea){
        Image saveIcon = new Image(getClass().getResourceAsStream("saveIcon.png"));
        Image openIcon = new Image(getClass().getResourceAsStream("openIcon.png"));
        Image cutIcon = new Image(getClass().getResourceAsStream("cutIcon.png"));
        Image copyIcon = new Image(getClass().getResourceAsStream("copyIcon.png"));
        Image pasteIcon = new Image(getClass().getResourceAsStream("pasteIcon.png"));
        
        Button saveButton = new Button("",new ImageView(saveIcon));
        Button openButton = new Button("",new ImageView(openIcon));
        Button cutButton = new Button("",new ImageView(cutIcon));
        Button copyButton = new Button("",new ImageView(copyIcon));
        Button pasteButton = new Button("",new ImageView(pasteIcon));
        pasteButton.setStyle("-fx-background-color: pink;");
        setShadows(saveButton);
        setShadows(openButton);
        setShadows(cutButton);
        setShadows(copyButton);
        setShadows(pasteButton);
        
        removeShadows(saveButton);
        removeShadows(openButton);
        removeShadows(cutButton);
        removeShadows(copyButton);
        removeShadows(pasteButton);
        toolbar.getItems().addAll(saveButton,new Separator(),openButton,cutButton,new Separator(),copyButton, new Separator(), pasteButton);
        saveButton.setOnAction(s -> saveOption(primaryStage, textArea, false));
        openButton.setOnAction(o -> openOption(primaryStage, textArea));
        cutButton.setOnAction(c -> textArea.cut());
        copyButton.setOnAction(co -> textArea.copy());
        pasteButton.setOnAction(p -> onPaste(textArea));
                
    }
    public static void saveOption(Stage stage2, TextArea text, Boolean saveAs){
        String line = null;
        Menu menuSave = new Menu("_Save");
        if (flag || saveAs){
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
            fileChooser.getExtensionFilters().add(extFilter);
            File openFile = fileChooser.showSaveDialog(null);
            if(openFile != null) { // If a text file is chosen and the user clicks open
                filePath = openFile.getAbsolutePath();
                stage2.setTitle(openFile.getName());
                flag = false;
            }
            
        }
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(filePath), "utf-8"))) {
                writer.write(text.getText().replaceAll("\n", System.lineSeparator()));
            } catch (IOException ex) {
                Logger.getLogger(Lab18.class.getName()).log(Level.SEVERE, null, ex);
            } 
    }
    
    public static void openOption(Stage stage2, TextArea text){
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File openFile = fileChooser.showOpenDialog(null);
       
        if (openFile != null){
            flag = false;
            filePath = openFile.getAbsolutePath();
            stage2.setTitle(openFile.getName());
        }
        try (BufferedReader br = new BufferedReader(
            new FileReader(filePath))) {
            String line;
            StringBuilder sb = new StringBuilder();
                    
            while((line = br.readLine()) != null){
                sb.append(line);
                sb.append("\n");
            }
            String fullText = sb.toString();
            text.setText(fullText);
            
        } catch (IOException ex) { 
            Logger.getLogger(Lab18.class.getName()).log(Level.SEVERE, null, ex);
        } 

    }
    
    public static Menu createFileMenu(Stage stage, VBox vbox, TextArea textArea){
        Menu menuFile = new Menu("_File");
        MenuItem neww = new MenuItem("_New");
        MenuItem open = new MenuItem("_Open");
        MenuItem save = new MenuItem("_Save");
        MenuItem saveAs = new MenuItem("Save _As");
        MenuItem print = new MenuItem("_Print");
        MenuItem pageSetup = new MenuItem("Page Set_up");
        MenuItem exit = new MenuItem("E_xit");
        
        open.setOnAction(o -> openOption(stage, textArea));

        save.setOnAction(s -> saveOption(stage, textArea, false));
        saveAs.setOnAction(sa -> saveOption(stage, textArea, true));

        exit.setOnAction(e -> System.exit(0));   
        
        menuFile.setAccelerator(KeyCombination.keyCombination("SHORTCUT+F"));
        neww.setAccelerator(KeyCombination.keyCombination("SHORTCUT+N"));
        open.setAccelerator(KeyCombination.keyCombination("SHORTCUT+O"));
        save.setAccelerator(KeyCombination.keyCombination("SHORTCUT+S"));
        print.setAccelerator(KeyCombination.keyCombination("SHORTCUT+P"));
        
        menuFile.getItems().addAll(neww, open, save,saveAs, exit, print, pageSetup);
        
        return menuFile;
    }
    public void setShadows(Button button1){
        button1.addEventHandler(MouseEvent.MOUSE_ENTERED,
        new EventHandler<MouseEvent>() {
          @Override
          public void handle(MouseEvent e) {
            button1.setEffect(shadow);
          }
        });
        
    }
    public void removeShadows(Button button1){
        button1.addEventHandler(MouseEvent.MOUSE_EXITED,
        new EventHandler<MouseEvent>() {
          @Override
          public void handle(MouseEvent e) {
            button1.setEffect(null);
          }
        });
    }
    
    public ObservableList fontStyles(TextField tf){
        ObservableList<String>fontList = FXCollections.observableArrayList(getFontNames(tf.getFont().getFamily()));
        List<String> stylesList = new ArrayList();
        for (int i = 0; i<fontList.size();i++){
            String currentFont = fontList.get(i);
            currentFont = currentFont.replace(tf.getFont().getFamily(),"");
            if(!currentFont.equals(""))stylesList.add(currentFont);
        }
        fontList = FXCollections.observableArrayList(stylesList);
        return fontList;
    }
    public void createFont(TextArea text){
        Dialog fontDialog = new Dialog();
        GridPane gridpane = new GridPane();
        fontDialog.setTitle("Font");
        fontDialog.setHeaderText(null);
        Label fontLabel = new Label("Font:");
        Label styleLabel = new Label("Style:");
        Label sizeLabel = new Label("Size:");
        TextField fontSelected = new TextField();
        TextField styleSelected = new TextField();
        TextField sizeSelected = new TextField();
        ListView <FamilyText> fontListView = new ListView<>();
        ListView <String> styleListView = new ListView<>();
        ListView <String> sizeListView = new ListView<>();
        Label sampleLabel = new Label("Sample");
        TextField sampleTextField = new TextField("AaBbYyZz");
        sampleTextField.setDisable(true);
        
        ObservableList<String> fontList = FXCollections.observableArrayList(getFamilies());
        ArrayList<FamilyText> familyList = new ArrayList();
        for (int i = 0; i< fontList.size(); i++){
            FamilyText fam = new FamilyText(fontList.get(i), fontList.get(i));
            familyList.add(fam);
        }
        
        ObservableList<FamilyText>fontFamilyList = FXCollections.observableArrayList(familyList);
        ObservableList<String> styleList = FXCollections.observableArrayList(getFontNames(sampleTextField.getFont().getFamily()));
        ObservableList<String> sizeList = FXCollections.observableArrayList("8", "9", "10", "11", "12", "14", "16", "18",
                "20", "22", "24", "26", "28", "36", "48", "72");
        fontListView.setItems(fontFamilyList);
        fontListView.setCellFactory(l -> new ListCell<FamilyText>(){
            @Override
            protected void updateItem(FamilyText item, boolean empty){
                super.updateItem(item,empty);
                if(empty || item == null){
                    setText(null);
                }else{
                    setText(item.getText());
                    setFont(Font.font(item.getFamilyText(), 16));
                }
            }
        });
        sizeListView.setItems(sizeList);
        styleListView.setItems(styleList);
        fontListView.setPrefWidth(100);
        fontListView.setPrefHeight(250);
        styleListView.setPrefWidth(100);
        styleListView.setPrefHeight(250);
        sizeListView.setPrefWidth(100);
        sizeListView.setPrefHeight(250);
        fontListView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue)->{
            fontSelected.setText(newValue.getFamilyText());
            sampleTextField.setFont(Font.font(newValue.getFamilyText(), 16));
            styleListView.setItems(fontStyles(sampleTextField));
    });
        
        gridpane.add(fontLabel, 0, 0);
        gridpane.add(styleLabel,1,0);
        gridpane.add(sizeLabel,2,0);
        gridpane.add(fontSelected, 0, 1);
        gridpane.add(styleSelected,1,1);
        gridpane.add(sizeSelected,2,1);
        gridpane.add(fontListView, 0,2);
        gridpane.add(styleListView,1,2);
        gridpane.add(sizeListView,2,2);
        gridpane.add(sampleLabel,2,3);
        gridpane.add(sampleTextField,2,4);
        fontDialog.getDialogPane().setContent(gridpane);
        ButtonType okayButton = new ButtonType("OK", ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
        fontDialog.getDialogPane().getButtonTypes().add(okayButton);
        fontDialog.getDialogPane().getButtonTypes().add(cancelButton);
        fontDialog.showAndWait().ifPresent(response -> {
        if (response == cancelButton){
            System.out.println("CANCEL");
            fontDialog.getOnCloseRequest();
        }else if (response == okayButton){
            try{
                Font changedFont = new Font(fontSelected.getText()+styleSelected.getText(),Integer.parseInt(sizeSelected.getText()));
                text.setFont(changedFont);
            }catch(NumberFormatException e){
                Font changedFont = new Font(fontSelected.getText()+styleSelected.getText(),12);
                text.setFont(changedFont);
            }
                fontDialog.close();
            }
        });
       
    }
    
    public Menu createEditMenu(TextArea text){
        Menu menuEdit = new Menu("_Edit");
        MenuItem undo = new MenuItem("_Undo");
        MenuItem redo = new MenuItem("Redo");
        MenuItem cut = new MenuItem("Cu_t");
        MenuItem copy = new MenuItem("_Copy");
        MenuItem paste = new MenuItem("_Paste");
        MenuItem delete = new MenuItem("De_lete");
        MenuItem find = new MenuItem("_Find");
        MenuItem findNext = new MenuItem("Find _Next");
        MenuItem replace = new MenuItem("_Replace");
        MenuItem goTo = new MenuItem("_Go To");
        MenuItem selectAll = new MenuItem("Select _All");
        MenuItem timeDate = new MenuItem("Time/_Date");
        
        menuEdit.setAccelerator(KeyCombination.keyCombination("SHORTCUT+E"));
        undo.setAccelerator(KeyCombination.keyCombination("SHORTCUT+U"));
        cut.setAccelerator(KeyCombination.keyCombination("SHORTCUT+X"));
        copy.setAccelerator(KeyCombination.keyCombination("SHORTCUT+C"));
        paste.setAccelerator(KeyCombination.keyCombination("SHORTCUT+P"));
        delete.setAccelerator(KeyCombination.keyCombination("DELETE"));
        find.setAccelerator(KeyCombination.keyCombination("SHORTCUT+F"));
        findNext.setAccelerator(KeyCombination.keyCombination("F3"));
        replace.setAccelerator(KeyCombination.keyCombination("SHORTCUT+H"));
        goTo.setAccelerator(KeyCombination.keyCombination("SHORTCUT+G"));
        selectAll.setAccelerator(KeyCombination.keyCombination("SHORTCUT+A"));
        timeDate.setAccelerator(KeyCombination.keyCombination("F5"));

        delete.setOnAction(d -> text.replaceText(text.getSelection(), ""));
        cut.setOnAction(c -> text.cut());
        copy.setOnAction(co -> text.copy());
        paste.setOnAction(p -> onPaste(text));
        menuEdit.getItems().addAll(undo,redo,cut,copy,paste,delete,find,findNext,replace,goTo,selectAll,timeDate);
        
        return menuEdit;
    }
    public void onPaste(TextArea text){
        if (clipboard.hasContent(DataFormat.PLAIN_TEXT)){
                    text.paste();
                }
    }
    public  Menu createFormatMenu(TextArea text){
        Menu menuFormat = new Menu("F_ormat");
        MenuItem wordWrap = new MenuItem("_Word Wrap");
        MenuItem font = new MenuItem("_Font...");
        font.setOnAction(f -> createFont(text));
        menuFormat.getItems().addAll(wordWrap, font);
        
        menuFormat.setAccelerator(KeyCombination.keyCombination("SHORTCUT+O"));
        return menuFormat;
    }
    public static Menu createViewMenu(){
        Menu menuView = new Menu("_View");
        MenuItem statusBar = new MenuItem("_Status Bar");
        menuView.getItems().addAll(statusBar);
        menuView.setAccelerator(KeyCombination.keyCombination("SHORTCUT+V"));
        return menuView;
    }
    public static Menu createHelpMenu(){
        Menu menuHelp = new Menu("_Help");
        MenuItem viewHelp = new MenuItem("View _Help");
        MenuItem aboutHelp = new MenuItem("_About");
        menuHelp.getItems().addAll(viewHelp, aboutHelp);
        menuHelp.setAccelerator(KeyCombination.keyCombination("SHORTCUT+H"));
        
        return menuHelp;
    }
    public String GetCaretPos(Number carPos, TextArea text){
        String subStr = text.getText().substring(0, (int) carPos);
        String[] strArr = (subStr).split("\n", -1);
        int row = strArr.length;
        int column = strArr[row-1].length() + 1;
        String result = "Ln "+ row + " Col "+ column;
        return result;
    }
    
    public VBox StatusBar(TextArea text){
        VBox vbox = new VBox();
        BorderPane bottom = new BorderPane();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss ");
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        String currentTime = formatter.format(zonedDateTime);
        Label currentTimeLabel = new Label(currentTime);
        final Timeline clock = new Timeline(
                new KeyFrame(Duration.millis( 1000 ),event-> {currentTimeLabel
                                                                .setText(LocalDateTime.now()
                                                                .format(formatter));
                })); 
        clock.setCycleCount( Animation.INDEFINITE );
        clock.play();
        bottom.setLeft(currentTimeLabel);
        Label caretPosLabel = new Label(GetCaretPos(text.getCaretPosition(), text));
        text.caretPositionProperty().addListener((obs, oldValue, newValue) -> caretPosLabel.setText(GetCaretPos(newValue, text)));
        bottom.setRight(caretPosLabel);
        vbox.getChildren().addAll(bottom);
        return vbox; 
    }
    
    public void startMenus(Stage stage, TextArea text,VBox vbox, ToolBar toolbar){
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = createFileMenu(stage, vbox, text);
        Menu editMenu = createEditMenu(text);
        Menu formatMenu = createFormatMenu(text);
        Menu viewMenu = createViewMenu();
        Menu helpMenu = createHelpMenu();

        menuBar.getMenus().addAll(fileMenu,editMenu,formatMenu,viewMenu, helpMenu);

        vbox.getChildren().addAll(menuBar, toolbar);
    }
    
    public void saveSettings(String text, double height, double width) {
        Settings settings = new Settings(text, height, width);
        Gson gson1 = new Gson();
        String json = gson1.toJson(settings);
        try (BufferedWriter br = new BufferedWriter(
                new FileWriter("data.txt"))) {

            br.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {
        launch(args);
    }
}
