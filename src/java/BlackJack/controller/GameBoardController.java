package BlackJack.controller;

import BlackJack.model.Card;
import BlackJack.view.CardGraph;
import BlackJack.view.Messages;
import javafx.animation.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import java.util.Objects;
import java.util.Random;

public class GameBoardController {
    @FXML
    public Button hit;
    public Button stay;
    public Button rules;
    public Button end;
    public Label balance;
    public Label bet;
    public DialogPane rulesPanel;
    public HBox dealerBox;
    public HBox dealerbackground;
    public HBox activePlayer;
    public HBox player2;
    public HBox player3;
    public Label handValue;
    public Label dealerValue;
    public Label messages;
    public AnchorPane highScorePane;
    public Label labelHSDate;
    public Label labelHSName;
    public Label labelHSScore;
    public TextField textFieldHS;
    public Button buttonHighScoreSubmit;
    public Label highScoreNotice;
    public Pane BettingScreen;
    public Button Plus;
    public Button Minus;
    public Button Bet;
    public Label BettingText;
    public Label BetAmount;
    public Pane welcome;
    public Label welcomeText;
    public FadeTransition infiniteFade;
    public Label clickToPlay;
    public Button buttonHighScore;
    public AnchorPane gameOver;
    public Button buttonQuit;
    public Button buttonResume;
    public AnchorPane highScoreList;
    int tempBet = 0;
    @FXML
    private AnchorPane gameBoardPane;
    //  private ModelTest blackJackLogic;
    private final BlackJackLogic blackJackLogic;
    private final Rectangle rect = new Rectangle();


    /*
        public GameBoardController(ModelTest blackJackLogic) {
            this.blackJackLogic = blackJackLogic;
        }

     */
    public GameBoardController(BlackJackLogic blackJackLogic) {

        this.blackJackLogic = blackJackLogic;
    }


    public void initialize() {
        welcomeAnimation();

        setListener(blackJackLogic.activePlayer.hand, activePlayer);

        setListener(blackJackLogic.dealer1.hand, dealerBox);

        setBettingScreenListener();

        rulesPanelSettings();

        setButtonListener();

        setHandValueListeners();

        setBalanceValueListener();

        setGameOverPanelListener();
        setHighScoreListener();

        messages.textProperty().bind(blackJackLogic.messages);
//Listens changes of the observableList


//balance its binded, should do a double bind? or call method from logic to change the balance?
        // balance.textProperty().bind(blackJackLogic.balanceProperty());
//        handValue.textProperty().bind(blackJackLogic.handValueSPProperty());


        //using buttons for test
        //test for changeFaceUp
        // stay.setOnAction(e-> blackJackLogic.activePlayerHandArr.get(0).changeFace());

        //testing hit
        //hit.setOnAction(e->blackJackLogic.hitListener());
        hit.setOnAction(e -> BlackJackLogic.actionQueue.add(1));
//        end.setOnAction(e -> player2.getChildren().add(new CardGraph("clubs", "ace", true)));
        end.setOnAction(e -> System.exit(0));
        buttonHighScore.setOnAction(e -> buttonHighScoreAction());

        stay.setOnAction(e -> BlackJackLogic.actionQueue.add(0));
        buttonHighScoreSubmit.setOnAction(e -> buttonHighScoreSubmitAction());
        textFieldHS.setOnKeyPressed(e -> {
            highScoreNotice.setText("Adding highscore ends current game.");
            highScoreNotice.setStyle("-fx-text-fill: white");
        });
        labelHSDate.textProperty().bind(blackJackLogic.highScore.dates);
        labelHSName.textProperty().bind(blackJackLogic.highScore.names);
        labelHSScore.textProperty().bind(blackJackLogic.highScore.scores);

        rules.setOnMouseClicked(e -> {
            rulesPanel.toFront();
            rulesPanel.setVisible(!rulesPanel.isVisible());
        });

        Plus.setOnAction(e -> plus());
        Minus.setOnAction(e -> minus());
        Bet.setOnAction(e -> betted());

        buttonQuit.setOnAction(e -> buttonQuitAction());
        buttonResume.setOnAction(e -> buttonResumeAction());
        welcome.setOnMouseClicked(e -> welcomeClose());
    }

    public void changeBalance(String string) {
        balance.setText(string);
    }

    public void setListener(ObservableList<Card> observable, HBox playerBox) {
        observable.addListener((ListChangeListener<Card>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    CardGraph c = cardToGraph(change.getAddedSubList().get(0));
                    System.out.println(change.getAddedSubList().get(0).isFaceUp());
                    fadeTransition(c);

                    if (observable.size() > 1) {
                        c.setTranslateX(-(50 * (observable.size() - 1)));//this has to be simplified
                        fadeTransition(c);
                    }
                    playerBox.getChildren().add(c);

                } else if (change.wasRemoved()) {
                    playerBox.getChildren().clear();
                } else if (change.wasUpdated()) {
                    System.out.println("UPDATED");
                    //Sets the faceUp state to true
                    ((CardGraph) playerBox.getChildren().get(change.getFrom())).setFaceUp(true);
                    //here applied changeFace method. gets the card from the box which was updated and swaps face
                    ((CardGraph) playerBox.getChildren().get(change.getFrom())).changeFace();
                }


            }
        });
    }

    public void setButtonListener() {
        blackJackLogic.disableButtons.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                hit.setDisable(newValue);
                stay.setDisable(newValue);
                buttonHighScoreSubmit.setDisable(newValue);
            }
        });
    }

    public void setHighScoreListener() {
        blackJackLogic.highScorePanel.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                highScoreList.setVisible(newValue);
                highScorePane.setVisible(newValue);
                highScorePane.toFront();
                highScoreList.toFront();
                buttonHighScore.toFront();
            }
        });
    }

    public void setHandValueListeners() {
        handValue.textProperty().bind(blackJackLogic.activePlayer.handValueSPProperty());
        dealerValue.textProperty().bind(blackJackLogic.dealer1.handValueSPProperty());
    }

    public void setBalanceValueListener() {
        blackJackLogic.activePlayer.balanceValueProperty.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                balance.setText(newValue);
            }
        });
    }

    public CardGraph cardToGraph(Card card) {
        String rank = String.valueOf(card.getRank());
        String suit = String.valueOf(card.getSuit()).toLowerCase();
        switch (rank) {
            case "1" -> rank = "ace";
            case "11" -> rank = "jack";
            case "12" -> rank = "queen";
            case "13" -> rank = "king";
            default -> rank = rank;
        }
        //System.out.println(card.isFaceUp());
        return new CardGraph(suit, rank, card.isFaceUp());
    }


    /**
     * sets up the rulesPanel
     */
    private void rulesPanelSettings() {
        rulesPanel.setVisible(false);
        Label rules = new Label("Rules");
        rules.setStyle("-fx-font-size: 40px; -fx-background-radius: 10px; -fx-text-fill: white");
        rules.prefWidth(620);
        rules.setAlignment(Pos.CENTER);
        rulesPanel.setHeader(rules);
        rulesPanel.setContentText(
                Messages.RULES.print());
    }

    public void setBettingScreenListener() {
        blackJackLogic.bettingScreen.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                BettingScreen.setVisible(newValue);
//                if(newValue)BettingScreen.toFront();

                if(BettingScreen.isVisible()){
                    betUpdater();
                }


            }
        });
    }

    public void betUpdater(){
        BettingScreen.toFront();
        tempBet = 0;
        BetAmount.setText(String.valueOf(tempBet));
        bet.setText("Bet: " + tempBet);

    }

    public void plus() {
        if (!(tempBet >= 1000) && !(tempBet >= Integer.parseInt(balance.getText().substring(balance.getText().indexOf(" ") + 1)))) { //1000 is max bet
            tempBet = tempBet + 100; //adds 100 to tempBet
            BetAmount.setText("" + tempBet);
            BettingText.setText("Set your Bet!");
        } else {
            if (tempBet >= 1000) {
                BettingText.setText("Set your Bet! \n Max bet is 1000");
            } else {
                BettingText.setText("Set your Bet! \n Bet to large for balance");
            }

        }
    }

    public void minus() {
        if (!(tempBet <= 100)) { //100 is min bet
            tempBet = tempBet - 100;
            BetAmount.setText("" + tempBet);
            BettingText.setText("Set your Bet!");
        } else {
            BettingText.setText("Set your Bet! \n Min bet is 100");
        }
    }

    public void betted() {
        //betUpdater();
        if(!(tempBet <= 0)){
            BlackJackLogic.actionQueue.add(tempBet);
            bet.setText("Bet: " + tempBet);
            BetAmount.setText("" + tempBet);
            BettingScreen.setVisible(false);
        } else{
            BettingText.setText("Set your Bet! \n Min bet is 100 !!!!!!!!!!!!!!!!!!!");
        }

    }

    private void fadeTransition(CardGraph c) {
        FadeTransition ft = new FadeTransition();
        ft.setDuration(Duration.seconds(0.5));
        ft.setNode(c);
        ft.setFromValue(0);
        ft.setToValue(100);
        ft.play();
    }

    /**
     * Show/hide HighScore-AnhorPane.
     */
    private void buttonHighScoreAction() {
        blackJackLogic.highScorePanel.setValue(!blackJackLogic.highScorePanel.getValue());
//        highScoreList.setVisible(blackJackLogic.highScorePanel.getValue());
//        highScorePane.setVisible(!highScorePane.isVisible());
        buttonHighScore.setText(highScorePane.isVisible() ? "RESUME" : "HIGHSCORE");
//        buttonHighScore.toFront();
//        highScoreList.toFront();
    }

    /**
     *
     */
    private void buttonHighScoreSubmitAction() {
        if (textFieldHS.getText().isEmpty()) {
            highScoreNotice.setText("Must enter name to submit!");
            highScoreNotice.setStyle("-fx-text-fill: pink");
        } else if (textFieldHS.getText().length() > 30) {
            highScoreNotice.setText("A bit too long, ey? Keep it under 30");
            highScoreNotice.setStyle("-fx-text-fill: pink");
            textFieldHS.clear();
        } else {
            BlackJackLogic.actionQueue.add(9);
            BlackJackLogic.actionQueue.add(textFieldHS.getText());
            textFieldHS.clear();
        }
    }

    /**
     * animation for welcome panel adds cards, animates them and starts "click here" animation.
     */
    private void welcomeAnimation() {
        Group gr = new Group();
        gr.getChildren().add(new CardGraph("diamonds","king",true));
        gr.getChildren().add(new CardGraph("spades","ace",true));
        gr.getChildren().add(new CardGraph("hearts","queen",true));
        gr.setTranslateX(welcome.getPrefWidth()/2-50);
        gr.setTranslateY(welcome.getPrefHeight()/2);
        welcome.getChildren().add(gr);

        infiniteFade=new FadeTransition();
        FadeTransition ft = new FadeTransition();
        ft.setDuration(Duration.seconds(0.5));
        ft.setNode(clickToPlay);
        ft.setFromValue(0);
        ft.setToValue(100);
        ft.setAutoReverse(true);
        ft.setCycleCount(Animation.INDEFINITE);
        ft.play();

        TranslateTransition ts = new TranslateTransition(Duration.seconds(2), gr);
        ts.setFromY(400);
        ts.setByY(gr.getTranslateY()-500);
        ts.setAutoReverse(false);
        ts.play();
       // ts.setOnFinished(e -> circleSeq());
        rotateTransition(gr.getChildren().get(0),-30);
        rotateTransition(gr.getChildren().get(2),30);
    }

    /**
     * animation to use with cards in welcomeAnimation
     * @param node the card to animate
     * @param angle the angle to rotate
     */
    private void rotateTransition(Node node,double angle){
       node.getTransforms().add(new Rotate(0,30,0));
        RotateTransition rt=new RotateTransition();
        rt.setNode(node);
        rt.setAxis(new Point3D(0,0,10));
        rt.setToAngle(angle);
        rt.setDuration(Duration.seconds(2));
        rt.play();

    }

    /**
     * scale animation when welcome pane is clicked
     */
    private void welcomeClose(){
        ScaleTransition sc=new ScaleTransition();
        sc.setNode(welcome);
        sc.setToX(0);
        sc.setToY(0);
        sc.setDuration(Duration.seconds(1));
        sc.play();
        sc.setOnFinished(e->{welcome.setVisible(false);infiniteFade.stop();});
    }

    public void setGameOverPanelListener() {
        blackJackLogic.gameOverPanel.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                gameOver.setVisible(newValue);
                highScoreList.setVisible(gameOver.isVisible());
                highScoreList.toFront();
            }
        });
    }

    private void buttonQuitAction() {
        System.exit(0);
    }

    private void buttonResumeAction() {
        BlackJackLogic.actionQueue.add(0);
        blackJackLogic.gameOverPanel.setValue(false);
        highScoreList.setVisible(gameOver.isVisible());
    }
}
