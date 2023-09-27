package com.example.skubbepuslespil;

import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class Brik extends ImageView {

    private int id;
    private SkubbePusleSpil sp;


    public Brik(int nr, int x, int y, SkubbePusleSpil s) {
        id = nr;
        String filnavn = "b" + id + ".png";
        setImage(new Image(getClass().getResource(filnavn).toString()));
        setX(x*80);
        setY(y*80);
        sp = s;
    }

    public void flyt(int retning) {
        // Afgør retning på transitionen og udfør den
        int dx = 0, dy = 0;

        if (retning == 1)                // højre
            dx = 80;
        else if (retning == 2)           // ned
            dy = 80;
        else if (retning == 3)           // venstre
            dx = -80;
        else if (retning == 0)           // op
            dy = -80;

        TranslateTransition t = new TranslateTransition(Duration.seconds(sp.transitionstid()), this);
        t.setByX(dx);
        t.setByY(dy);
        t.setOnFinished(e -> { sp.vent(); });
        t.play();
    }

    public void sætPosition(int x, int y) {
        setX(x);
        setY(y);
    }

    public int id() {
        return id;
    }
}
