package fr.umontpellier.iut.dominionfx.views;

import fr.umontpellier.iut.dominionfx.DominionIHM;
import fr.umontpellier.iut.dominionfx.ICard;
import fr.umontpellier.iut.dominionfx.IGame;
import fr.umontpellier.iut.dominionfx.IPlayer;
import fr.umontpellier.iut.dominionfx.mechanics.Player;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * Cette classe présente les éléments des joueurs autres que le joueur courant,
 * en cachant ceux que le joueur courant n'a pas à connaitre.
 * On y définit les bindings sur le joueur courant, ainsi que le listener à exécuter lorsque ce joueur change
 */
public class OtherPlayersView extends HBox {

    @FXML
    private HBox root;

    public OtherPlayersView(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/othersPlayersView.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void bindCurrentPlayer(){
    }

    public Node createCardNodeInHand(ICard card){
        ImageView imageView = new ImageView("images/dosCarte.png");
        imageView.setPreserveRatio(true);
        imageView.fitWidthProperty().bind(getScene().widthProperty().divide(1638).multiply(70));
        imageView.fitHeightProperty().bind(getScene().heightProperty().divide(1024).multiply(70));
        imageView.setUserData(card);
        return imageView;
    }

    public Node createCardNodeInPlay(ICard card){
        String image = card.getName().replace(" ", "");
        ImageView imageView = new ImageView("images/cards/"+image+".jpg");
        imageView.setPreserveRatio(true);
        imageView.fitWidthProperty().bind(getScene().widthProperty().divide(1638).multiply(70));
        imageView.fitHeightProperty().bind(getScene().heightProperty().divide(1024).multiply(70));
        return imageView;
    }

    private VBox refreshHands(IPlayer nextPlayer) {

        Label playerName1 = new Label(nextPlayer.getName());
        HBox handPane1 = new HBox();
        HBox inPlayPane1 = new HBox();
        if (!nextPlayer.getHand().isEmpty()) {
            for (ICard card : nextPlayer.getHand()) {
                handPane1.getChildren().add(createCardNodeInHand(card));
            }
        }
        if (!nextPlayer.getInPlay().isEmpty()) {
            for (ICard card : nextPlayer.getInPlay()) {
                inPlayPane1.getChildren().add(createCardNodeInPlay(card));
            }
        }
        VBox vBox = new VBox(playerName1, inPlayPane1, handPane1);
        vBox.setId("bob");
        return  vBox;
    }

    @FXML
    public void initialize(){
        DominionIHM.getGame().currentPlayerProperty().addListener(playerChangeListener);
        for(IPlayer player : DominionIHM.getGame().getPlayers()){
            player.getHand().addListener(playerChangeListener2);
        }
    }

    private final ChangeListener playerChangeListener = (observableValue, oldPlayer, newPlayer) -> {
        root.getChildren().clear();
        for(IPlayer player : DominionIHM.getGame().getPlayers()){
        if(player != DominionIHM.getGame().currentPlayerProperty().get()){
            root.getChildren().add(refreshHands(player));
        }
    }
    };

    private final ListChangeListener<ICard> playerChangeListener2 = change -> {
        root.getChildren().clear();
        for(IPlayer player : DominionIHM.getGame().getPlayers()){
            if(player != DominionIHM.getGame().currentPlayerProperty().get()){
                root.getChildren().add(refreshHands(player));
            }
        }
    };
}