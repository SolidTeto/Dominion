package fr.umontpellier.iut.dominionfx.views;

import fr.umontpellier.iut.dominionfx.ICard;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * Cette classe représente la vue d'une carte.
 */
public class CardView extends VBox {

    @FXML
    HBox inPlayPane;

    public CardView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/cardView.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void refreshHand() {
        inPlayPane.getChildren().clear();
    }

    public Node createCardNodeInPlay(ICard card){
        Button cardButton = new Button(card.getName());
        inPlayPane.getChildren().add(cardButton);
        return cardButton;
    }

    public void createBindings() {}

    public void initialize(){
        createBindings();
    }

}