package cz.kamenak.cz.arcas.ija.projekt;

import javafx.scene.shape.Shape;

import java.util.List;
/**
 * Interface Drawable - slouží pro vykreslení objektů
 * @author Daniel Kamenický (xkamen21)
 * @author Vojtěch Olej (xolejv00)
 *
 */
public interface Drawable {
    List<Shape> getGUI();
}
