package com.example.skubbepuslespil;

// Skubbepuslespillet eller 15-spillet
// EK sep. 2023
// Spillet er bygget med 15 brikker, der flyttes til en tilfældig startposition.
// Med piletaster kan man flytte brikkerne indtil de står i korrekt orden

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class SkubbePusleSpil extends Application {

    private int tomX = 3, tomY = 3;         // Koordinater for det tomme felt
    private Brik[][] brikker;               // Array der holder brikkerne. x=0..3, y=0..3
    private boolean vent = false;           // Sand, når der er en transition i gang. Forhindrer flere samtidige transitioner.
    private double transistionstid = 0.4;   // Tid det tager en brik at flytte sig (sek.).
    private final int antalNaiveTræk = 100;         // Antal forsøg der anvendes ved naiv blanding
    private boolean blander = false;        // Tilstand, der bruges når brikkerne blandes i starten af spillet.
    private Pane rod;

    public static void main(String[] args) {
        launch();
    }

    public void start(Stage mainStage)
    {
        // Sæt scene og vindue
        mainStage.setTitle("SkubbePuslespil");

        rod = new Pane();
        rod.setPrefSize(320, 320);      // Brikkerne er 80*80 på et 4*4 bræt

        Scene scene = new Scene(rod);
        scene.setFill(Color.BISQUE);
        mainStage.setScene(scene);

        // Opsæt event for tastatur
        scene.setOnKeyPressed(
                (KeyEvent event) ->
                {
                    String keyName = event.getCode().toString();
                    checkTast(keyName);
                }
        );

        // Spillet sættes først op - dernæst blandes brikkerne
        bygSpillet();

        blandBrikkerne();

        // Så er alt klart og vi kan vise vinduet
        mainStage.show();

    }

    private void checkTast(String key) {
        // Check hvilken piletast der blev trykket på og
        // lav flytteanimation af brik. Derefter byttes positionerne
        // for brikken og det tomme felt (tomX,tomY).
        // Check også at trækket er muligt for det tomme felt.
        // Når den er i blandingstilstand udføres flytning af brikken ikke.
        // Vent forhindrer nye transitoner at starte, hvis en anden er i gang
        // tomX,tomY checker om trækket er legalt
        if (!vent && key == "RIGHT" && tomX > 0) {
            if (!blander) {
                vent = true;    // Sæt vent til true. Når transitionen er slut sættes den false.
                brikker[tomX - 1][tomY].flyt(1);
            }
            brikker[tomX][tomY] = brikker[tomX-1][tomY];
            // Når brikken er flyttet ned skal det tomme "felt" flyttes op
            tomX -= 1;
        } else
        if (!vent && key == "LEFT" && tomX < 3) {
            if (!blander) {
                vent = true;
                brikker[tomX + 1][tomY].flyt(3);
            }
            brikker[tomX][tomY] = brikker[tomX+1][tomY];
            tomX += 1;
        } else
        if (!vent && key == "UP" && tomY < 3) {
            if (!blander) {
                vent = true;
                brikker[tomX][tomY + 1].flyt(0);
            }
            brikker[tomX][tomY] = brikker[tomX][tomY+1];
            tomY += 1;
        } else
        if (!vent && key == "DOWN" && tomY > 0) {
            if (!blander) {
                vent = true;
                brikker[tomX][tomY - 1].flyt(2);
            }
            brikker[tomX][tomY] = brikker[tomX][tomY-1];
            tomY -= 1;
        }

        // Check om spilleren har vundet, ved at løbe brikkerne igennem i forhold til en tælleer (id).
        if (!blander) {
            int id = 1;
            boolean vundet = true;
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++) {
                    if (!(j == 3 && i == 3)) {
                        //System.out.println("id("+j+","+i+")="+brikker[j][i].id());
                        if (brikker[j][i].id() != id) vundet = false;
                        ++id;
                    }
                }
            if (vundet) {
                // Skriv vindertekst på vinduet
                Text t = new Text(10, 170, "Du har vundet!");
                t.setFill(Color.SADDLEBROWN);
                t.setFont(Font.font("Verdana", FontWeight.BOLD, 35));
                rod.getChildren().add(t);
            }
        }

    }

    public void vent() {
        // Når vent er sand reagerer spillet ikke på tastetryk
        // Det gør at transitioner gøres færdige
        vent = false;
    }

    public double transitionstid() {
        return transistionstid;
    }

    private void bygSpillet() {
        // Init. af spil
        // Fyld brikkerne i spilarrayet
        // Det tomme felt har koordinaterne tomX, tomY.
        brikker = new Brik[16][16];
        int id = 1;
        for (int i=0; i<4; i++)
            for (int j=0; j<4; j++) {
                if (id < 16) {      // Der er kun 15 brikker - den 16. er det tomme felt.
                    // Brikkerne gemmes i et 2D array og sættes på scenen
                    brikker[j][i] = new Brik(id++, j, i, this);
                    rod.getChildren().add(brikker[j][i]);
                }
            }
    }

    private void blandBrikkerne() {
        // Så skal vi blande brikkerne.
        // Det gøres ved tilfældigt(naiv metode) at flytte en brik et antal gange (100 forsøg blander pænt meget)
        // Sæt tilstander blander, således at brikkerne ikke flyttes fysisk.
        blander = true;         // Når blander er sand udføres transionerne ikke

        for (int antal = 0; antal < antalNaiveTræk; antal++) {
            // Lav et tilfældigt træk
            int r = (int) (Math.random() * 4);
            String s;
            if (r == 0) s = "RIGHT";
            else if (r == 1) s = "LEFT";
            else if (r == 2) s = "UP";
            else s = "DOWN";
            // Flyt brik hvis træk er legalt
            checkTast(s);
        }

        // Nu er brikkerne flyttet rundt, så skal bare opdatere GUI
        // med deres nye position
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++) {
                if (!(tomX == j && tomY == i))
                    brikker[j][i].sætPosition(j * 80, i * 80);
            }

        // Klargør til at spillet kan køre
        blander = false;
        vent = false;
    }

}