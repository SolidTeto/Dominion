package fr.umontpellier.iut.dominionfx.views;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Cette classe correspond à une nouvelle fenêtre permettant de choisir les noms des joueurs de la partie.
 * Lorsque l'utilisateur a fini de saisir les noms de joueurs, il demandera à démarrer la partie.
 */
public class ChoosePlayersView extends Stage implements Initializable {

    public static final double screenRatio = .55;/*.95*/

    private final ObservableList<String> playersNames;

    public ObservableList<String> playersNamesProperty() {
        return playersNames;
    }

    private List<TextField> textFieldList;

    @FXML
    private ImageView titre;

    @FXML
    private HBox hbox;

    @FXML
    private VBox vbox;

    @FXML
    private ImageView button;

    private ComboBox combobox;

    public ChoosePlayersView() {
        playersNames = FXCollections.observableArrayList();
        BorderPane root = new BorderPane();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/choosePlayerView.fxml"));
            loader.setRoot(root);
            loader.setController(this);
            loader.load();
            this.setScene(new Scene(root, Screen.getPrimary().getBounds().getWidth() * screenRatio,  Screen.getPrimary().getBounds().getHeight() * screenRatio));
            this.setTitle("Dominion-Seaside");
            this.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] getPlayersNames() {
        return playersNames.toArray(new String[0]);
    }

    /**
     * Définit l'action à exécuter lorsque la liste des participants est correctement initialisée
     */
    public void setPlayersNamesDefinedListener(ListChangeListener<String> whenPlayersNamesAreDefined) {
        playersNames.addListener(whenPlayersNamesAreDefined);
    }

    /**
     * Vérifie que tous les noms des participants sont renseignés
     * et affecte la liste définitive des participants
     */
    @FXML
    protected void setPlayersNamesList() {
        ArrayList<String> tempNamesList = new ArrayList<>();
        for (int i = 0; i < getNumberOfPlayers(); i++) {
            String name = getPlayerByIndex(i);
            if (name == null || name.isEmpty()) {
                tempNamesList.clear();
                break;
            } else
                tempNamesList.add(name);
        }
        if (!tempNamesList.isEmpty()) {
            hide();
            playersNames.clear();
            playersNames.addAll(tempNamesList);
        }
    }

    public void initResponsiv(){
        button.setPreserveRatio(true);
        button.fitWidthProperty().bind(getScene().widthProperty().divide(590).multiply(150));
        button.fitHeightProperty().bind(getScene().heightProperty().divide(1661).multiply(150));
        titre.setPreserveRatio(true);
        titre.fitWidthProperty().bind(getScene().widthProperty().divide(544).multiply(400));
        titre.fitHeightProperty().bind(getScene().heightProperty().divide(1528).multiply(400));
    }

    public void createChoose(int nb, int old){
        if(nb>old) {
            for (int i = old+1; i < nb; i++) {
                TextField t = new TextField();
                t.setMaxWidth(300);
                vbox.getChildren().addAll(new Label("Joueur " + i + " :"), t);
                textFieldList.add(t);
            }
        }else{
            for (int i = old; i > nb-1; i--) {
                vbox.getChildren().removeLast();
                vbox.getChildren().removeLast();
                textFieldList.removeLast();
            }
        }
    }
    /**
     * Retourne le nombre de participants à la partie que l'utilisateur a renseigné
     */
    protected int getNumberOfPlayers() {
        return Integer.valueOf(combobox.getValue().toString());
    }

    /**
     * Retourne le nom que l'utilisateur a renseigné pour le ième participant à la partie
     *
     * @param playerNumber : le numéro du participant
     */
    protected String getPlayerByIndex(int playerNumber) {
        return textFieldList.get(playerNumber).getText();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        textFieldList = new ArrayList<>();
        ObservableList<Integer> choix = FXCollections.observableArrayList(2,3,4);
        Label label = new Label("Définir le nombre de joueur :  ");
        combobox = new ComboBox(choix);
        combobox.setValue(2);
        hbox.getChildren().addAll(label,combobox);
        createChoose(3, 0);

        button.setOnMouseClicked(event -> {
            boolean verif = true;
            for(TextField t : textFieldList){
                if(t.getText().isEmpty()){
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Tout les identifiants n'ont pas été saisit");
                    alert.showAndWait();
                    verif = false;
                    break;
                }
            }
            if(verif){
                setPlayersNamesList();
            }
        });

        combobox.valueProperty().addListener((observable, oldValue, newValue) -> {
            createChoose(Integer.valueOf(newValue.toString())+1, Integer.valueOf(oldValue.toString()));
        });
    }
}