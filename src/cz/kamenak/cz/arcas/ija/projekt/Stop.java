package cz.kamenak.cz.arcas.ija.projekt;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.List;
/**
 * Třída Stop - reprezentuje zastávku na mapě
 * @author Daniel Kamenický (xkamen21)
 * @author Vojtěch Olej (xolejv00)
 *
 */
public class Stop implements Drawable{
    private Coordinate position;
    private String name;
    private List<Shape> stop_gui;

    /**
     * Konstruktor pro zastávku.
     * @param position pozice zastávky na osách XY
     * @param name jméno zastávky
     */
    public Stop(Coordinate position, String name) {
        this.position = position;
        this.name = name;
        //vytvoření zastávky pro grafické prostředí
        stop_gui = new ArrayList<>();
        stop_gui.add(new Circle(position.getX(), position.getY(), 6, Color.BLACK));
    }

    /**
     * Metoda pro získání jména zastávky.
     * @return jméno ulice (String)
     */
    public String getName() {
        return name;
    }

    /**
     * Metoda pro získání pozice zastávky.
     * @return pozici zastávky (class Coordinate)
     */
    public Coordinate getPosition() {
        return position;
    }

    /**
     * Metoda pro vrácení grafické reprezentace zastávky.
     * @return List prvků Shape
     */
    @Override
    public List<Shape> getGUI() {
        return stop_gui;
    }
}

