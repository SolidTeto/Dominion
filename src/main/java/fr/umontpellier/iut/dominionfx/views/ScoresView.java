package fr.umontpellier.iut.dominionfx.views;

import fr.umontpellier.iut.dominionfx.DominionIHM;
import fr.umontpellier.iut.dominionfx.IPlayer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * Cette classe définit les éléments à afficher lorsque la partie est terminée.
 * Elle peut proposer de recommencer une nouvelle partie.
 */
public class ScoresView extends BorderPane {

    private DominionIHM ihm;

    @FXML
    private VBox bob;

    public ScoresView(DominionIHM ihm) {
        this.ihm = ihm;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/scoreView.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initialize() {
        IPlayer max=DominionIHM.getGame().getPlayers().getFirst();
        for(IPlayer player : DominionIHM.getGame().getPlayers()){
            Label label = new Label(player.getName()+" a un score de : "+player.getVictoryPoints());
            bob.getChildren().add(label);
            if(max.getVictoryPoints()< player.getVictoryPoints()){
                max=player;
            }
        }
        Label label = new Label(max.getName()+" gagne la partie !");
        bob.getChildren().add(label);
        Button restart = new Button("restart ?");
        restart.setOnMouseClicked(e -> {
            ihm.setPlayersAndGame();
            ihm.startGame();
        });
        bob.getChildren().add(restart);
    }
}
//