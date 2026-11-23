package fr.umontpellier.iut.dominionfx.views;

import fr.umontpellier.iut.dominionfx.DominionIHM;
import fr.umontpellier.iut.dominionfx.ICard;
import fr.umontpellier.iut.dominionfx.IGame;
import fr.umontpellier.iut.dominionfx.IPlayer;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class CurrentPlayerView extends StackPane {

    public ObjectProperty<? extends IPlayer> currentPlayerProperty() {
        return currentPlayer;
    }

    private ObjectProperty<? extends IPlayer> currentPlayer;

    IntegerProperty actions = new SimpleIntegerProperty();
    IntegerProperty buys = new SimpleIntegerProperty();
    IntegerProperty moneys = new SimpleIntegerProperty();
    IntegerProperty pirateShips = new SimpleIntegerProperty();

    @FXML
    private Label trash;

    @FXML
    private StackPane bob;

    @FXML
    private Button discard;

    @FXML
    private Button island;

    @FXML
    private Button village;

    @FXML
    private Label action;

    @FXML
    private Label buy;

    @FXML
    private Label money;

    @FXML
    private Label pirateShip;

    @FXML
    private Label instruction;

    @FXML
    private Label playerName;

    @FXML
    private HBox handPane;

    @FXML
    private HBox inPlayPane;

    @FXML
    private HBox yesOrNoButtons;

    public IPlayer getCurrentPlayer() {
        return currentPlayer.get();
    }

    public CurrentPlayerView(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/currentPlayerView.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void bindCurrentPlayer(){
        for (IPlayer player :DominionIHM.getGame().getPlayers()){
            player.waitForYesOrNoProperty().addListener(yesOrNo);
        }
        action.textProperty().bind(
                Bindings.concat("Actions : ", actions));
        buy.textProperty().bind(
                Bindings.concat("Achats : ", buys));
        money.textProperty().bind(
                Bindings.concat("Argent : ", moneys));
        pirateShip.textProperty().bind(
                Bindings.concat("PirateShip Count : ",pirateShips)
        );
        instruction.textProperty().bind(DominionIHM.getGame().instructionProperty());
        currentPlayer = DominionIHM.getGame().currentPlayerProperty();
        currentPlayer.addListener(currentPlayerChangeListener);
    }

    private void refreshHand() {
        handPane.getChildren().clear();
        inPlayPane.getChildren().clear();
        if (currentPlayer.getValue() == null) return;
        for (ICard card : currentPlayer.getValue().getHand()) {
            handPane.getChildren().add(createCardNodeInHand(card));
        }
    }

    public Node createCardNodeInHand(ICard card){
        String image = card.getName().replace(" ", "");
        ImageView imageView = new ImageView("images/cards/"+image+".jpg");
        imageView.setPreserveRatio(true);
        imageView.fitWidthProperty().bind(getScene().widthProperty().divide(1638).multiply(150));
        imageView.fitHeightProperty().bind(getScene().heightProperty().divide(1024).multiply(150));
        imageView.setUserData(card);
        imageView.setOnMouseClicked(event -> currentPlayer.getValue().cardInHandWasChosen(card.getName()));
        return imageView;
    }

    public Node createCardNodeInPlay(ICard card){
        String image = card.getName().replace(" ", "");
        ImageView imageView = new ImageView("images/cards/"+image+".jpg");
        imageView.setPreserveRatio(true);
        imageView.fitWidthProperty().bind(getScene().widthProperty().divide(1638).multiply(150));
        imageView.fitHeightProperty().bind(getScene().heightProperty().divide(1024).multiply(150));
        inPlayPane.getChildren().add(imageView);
        return imageView;
    }

    @FXML
    public void initialize(){
        bindCurrentPlayer();
        village.setOnMouseEntered(e -> {
            HBox hbox = new HBox();
            hbox.setMouseTransparent(true);
            for(ICard card : currentPlayer.get().getNativeVillageMat()){
                hbox.getChildren().add(createCardNodeInPlay(card));
            }
            bob.getChildren().addLast(hbox);
        });
        village.setOnMouseExited(e -> bob.getChildren().removeLast());

        island.setOnMouseEntered(e -> {
            HBox hbox = new HBox();
            hbox.setMouseTransparent(true);
            for(ICard card : currentPlayer.get().getIslandMat()){
                hbox.getChildren().add(createCardNodeInPlay(card));
            }
            bob.getChildren().addLast(hbox);
        });
        island.setOnMouseExited(e -> bob.getChildren().removeLast());

        discard.setOnMouseEntered(e -> {
            HBox hbox = new HBox();
            hbox.setMouseTransparent(true);
            for(ICard card : currentPlayer.get().getDiscard()){
                hbox.getChildren().add(createCardNodeInPlay(card));
            }
            bob.getChildren().addLast(hbox);
        });
        discard.setOnMouseExited(e -> bob.getChildren().removeLast());

        trash.setOnMouseEntered(e -> {
            trash.setText("Trash : " +DominionIHM.getGame().getNumberOfTrashedCards());
        });
        trash.setOnMouseExited(e -> trash.setText("Trash"));
    }

    private final ListChangeListener<?super ICard> handListener = change -> {
        while (change.next()) {
            if (change.wasAdded()){
                for(ICard card : change.getAddedSubList()){
                    handPane.getChildren().add(createCardNodeInHand(card));
                }
            }
            if (change.wasRemoved()){
                for(ICard card : change.getRemoved()){
                    handPane.getChildren().removeIf(node ->node.getUserData() == card);
                    createCardNodeInPlay(card);
                }
            }
        }
    };

    private final ChangeListener<IPlayer> currentPlayerChangeListener = (observableValue, oldPlayer, newPlayer) -> {
        if(oldPlayer != null){
            oldPlayer.getHand().removeListener(handListener);
            actions.unbind();
            buys.unbind();
            moneys.unbind();
            pirateShips.unbind();
        }
        if(newPlayer != null){
            playerName.setText(newPlayer.getName());
            refreshHand();
            currentPlayer.getValue().getHand().addListener(handListener);
            actions.bind(currentPlayer.getValue().numberOfActionsProperty());
            buys.bind(currentPlayer.getValue().numberOfBuysProperty());
            moneys.bind(currentPlayer.getValue().moneyProperty());
            pirateShips.bind(currentPlayer.getValue().pirateShipCounterProperty());
        }
    };

    private final ChangeListener<Boolean> yesOrNo = (observableValue, oldValue, newValue) -> {
        if(newValue){

            ImageView yes = new ImageView("images/Yes.png");
            yes.setPreserveRatio(true);
            yes.fitWidthProperty().bind(getScene().widthProperty().divide(482).multiply(30));
            yes.fitHeightProperty().bind(getScene().heightProperty().divide(518).multiply(30));

            ImageView no = new ImageView("images/No.png");
            no.setPreserveRatio(true);
            no.fitWidthProperty().bind(getScene().widthProperty().divide(483).multiply(30));
            no.fitHeightProperty().bind(getScene().heightProperty().divide(516).multiply(30));

            yes.setOnMouseClicked(e -> currentPlayer.get().answer("Yes"));
            no.setOnMouseClicked(e -> currentPlayer.get().answer("No"));
            yesOrNoButtons.getChildren().addAll(yes,no);
        }else{
            yesOrNoButtons.getChildren().clear();
        }
    };
}