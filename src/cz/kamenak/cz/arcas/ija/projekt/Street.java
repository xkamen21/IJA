package cz.kamenak.cz.arcas.ija.projekt;

import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.List;

import static javafx.scene.paint.Color.*;

/**
 * Třída Street - reprezentuje ulici na mapě
 * @author Daniel Kamenický (xkamen21)
 * @author Vojtěch Olej (xolejv00)
 *
 */
public class Street implements Drawable {
    //souradnice pocatecni pozice ulice
    private Coordinate start;
    //souradnice koncove pozice ulice
    private Coordinate end;
    //jmeno ulice
    private String name;

    private List<Stop> StopList;

    private List<Shape> gui;

    private boolean selected = false;

    private int delay = 0;

    /**
     * Konstruktor pro ulici.
     * @param name název ulice
     * @param start počáteční souřadnice ulice
     * @param end koncové souřadnice ulice
     * @param StopList list zastávek nacházejicích se na ulici
     */
    public Street(String name, Coordinate start, Coordinate end, List<Stop> StopList) {
        this.start = start;
        this.end = end;
        this.name = name;
        this.StopList = StopList;
        gui = new ArrayList<>();
        gui.add(new Line(start.getX(), start.getY(), end.getX(), end.getY()));
        gui.get(0).setStrokeWidth(2);
        gui.get(0).setStroke(BLACK);
    }

    /**
     * Metoda pro nastavení označení ulice.
     * @param selected true/false
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * Metoda pro nastavení černé barvy ulice.
     */
    public void setGuiBlack() {
        gui.get(0).setStroke(BLACK);
    }

    /**
     * Metoda pro nastavení zelené barvy ulice. Slouží pro zvýraznění trasy vozidla.
     */
    public void setGuiGreen() {
        gui.get(0).setStroke(GREEN);
    }

    /**
     * Metoda pro nastavení zpoždění vozidla na ulici.
     * @param delay zpoždění (int)
     */
    public void setDelay(int delay) {
        this.delay = delay;
    }

    /**
     * Metoda pro vrácení pravdivostní hodnoty, zda li je ulice označena.
     * @return true/false
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Metoda pro vracení hodnoty zpoždění.
     * @return zpořdění (int)
     */
    public int getDelay() {
        return delay;
    }

    /**
     * Metoda pro vrácení listu ulic zastávky.
     * @return list class Stop
     */
    public List<Stop> getStopList() {
        return StopList;
    }

    /**
     * Metoda pro vrácení počátečních souřadnic ulice.
     * @return počátečních souřadnice (class Coordinate)
     */
    public Coordinate getStart() {
        return start;
    }

    /**
     * Metoda pro vrácení koncových souřadnic ulice.
     * @return počátečních koncových (class Coordinate)
     */
    public Coordinate getEnd() {
        return end;
    }

    /**
     * Metoda pro vrácení názvu ulice.
     * @return název (String)
     */
    public String getName() {
        return name;
    }

    /**
     * Metoda pro vracení grafické reprezentace ulice.
     * @return List prvků Shape
     */
    @Override
    public List<Shape> getGUI() {
        return gui;
    }
}
