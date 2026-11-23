package fr.umontpellier.iut.dominionfx.views;

import fr.umontpellier.iut.dominionfx.DominionIHM;
import fr.umontpellier.iut.dominionfx.ICard;
import fr.umontpellier.iut.dominionfx.IGame;
import fr.umontpellier.iut.dominionfx.IPlayer;
import fr.umontpellier.iut.dominionfx.mechanics.SupplyPile;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.io.IOException;

/**
 * Cette classe correspond à la fenêtre principale de l'application.
 * Elle est initialisée avec une référence sur la partie en cours (Game).
 * On y définit les bindings sur les éléments internes qui peuvent changer
 * ainsi que les listeners à exécuter lorsque ces éléments changent.
 */
public class GameView extends StackPane {

    private IGame game;

    @FXML
    private FlowPane temporayCards;

    @FXML
    private ImageView skip;

    @FXML
    private FlowPane cardsInPile;

    @FXML
    private VBox right;

    public GameView(IGame game) {
        this.game = game;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/main.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createBindings() {
        game.temporaryCardsProperty().addListener(temporary);
        skip.setOnMouseClicked(defaultSkipHandler);
    }

    public Node createCardNodeForSupply(ICard card){
        String image = card.getName().replace(" ", "");
        ImageView imageView = new ImageView("images/cards/"+image+".jpg");
        imageView.setPreserveRatio(true);
        imageView.fitWidthProperty().bind(getScene().widthProperty().divide(1638).multiply(150));
        imageView.fitHeightProperty().bind(getScene().heightProperty().divide(1024).multiply(150));
        imageView.setOnMouseEntered(hoover);
        imageView.setOnMouseExited(hooverEnd);
        imageView.setOnMouseClicked(e -> game.supplyCardWasChosen(card.getName()));
        return imageView;
    }

    public ImageView createCard(ImageView card){
        ImageView imageView = new ImageView(card.getImage().getUrl());
        imageView.setPreserveRatio(true);
        imageView.fitWidthProperty().bind(getScene().widthProperty().divide(1638).multiply(150).multiply(3));
        imageView.fitHeightProperty().bind(getScene().heightProperty().divide(1024).multiply(150).multiply(3));
        return imageView;
    }

    public ImageView createTempCard(ICard card){
        String image = card.getName().replace(" ", "");
        ImageView imageView = new ImageView("images/cards/"+image+".jpg");
        imageView.setPreserveRatio(true);
        imageView.fitWidthProperty().bind(getScene().widthProperty().divide(1638).multiply(200));
        imageView.fitHeightProperty().bind(getScene().heightProperty().divide(1024).multiply(200));
        imageView.setOnMouseEntered(hoover);
        imageView.setOnMouseExited(hooverEnd);
        return imageView;
    }

    private EventHandler<? super MouseEvent> hoover = (mouseEvent -> {
        if(!right.getChildren().isEmpty()) {
            right.getChildren().clear();
        }
        ImageView card = createCard((ImageView) mouseEvent.getSource());
        right.setAlignment(Pos.CENTER_RIGHT);
        right.getChildren().add(card);
        right.setManaged(false);
    });

    private EventHandler<? super MouseEvent> hooverEnd = (mouseEvent -> {
        right.getChildren().clear();
        right.setManaged(true);
    });

    private EventHandler<? super MouseEvent> defaultSkipHandler = (mouseEvent -> {System.out.println("You chose to skip"); game.skipWasChosen();});

    public void initSupply() {
        for(SupplyPile supplyPile : game.getSupplyPiles()){
            Node node = createCardNodeForSupply(supplyPile.getFirst());
            cardsInPile.getChildren().add(node);
        }
        cardsInPile.maxWidthProperty().bind(getScene().widthProperty().divide(1024).multiply(150).multiply(4.5));
        temporayCards.maxWidthProperty().bind(getScene().widthProperty().divide(1024).multiply(200).multiply(2));
    }

    @FXML
    public void initResponsive(){
        skip.setPreserveRatio(true);
        skip.fitWidthProperty().bind(getScene().widthProperty().divide(521).multiply(50));
        skip.fitHeightProperty().bind(getScene().heightProperty().divide(471).multiply(50));
    }

    @FXML
    public void initialize(){
        createBindings();
    }

    private final ListChangeListener<ICard> temporary = change -> {
        temporayCards.setManaged(true);
        temporayCards.getChildren().clear();
        if(!game.temporaryCardsProperty().isEmpty()) {
            for (ICard card : game.temporaryCardsProperty()) {
                temporayCards.getChildren().add(createTempCard(card));
                temporayCards.getChildren().getLast().setOnMouseClicked(e -> game.temporaryCardWasChosen(card.getName()));
            }
        }else {
            temporayCards.setManaged(false);
        }
    };
}