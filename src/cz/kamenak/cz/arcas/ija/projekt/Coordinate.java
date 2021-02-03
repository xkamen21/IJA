package cz.kamenak.cz.arcas.ija.projekt;

import java.util.Objects;
import org.json.JSONObject;
/**
 * Třída Coordinate - reprezentuje souřadnice v mapě
 * @author Daniel Kamenický (xkamen21)
 * @author Vojtěch Olej (xolejv00)
 *
 */
public class Coordinate {
    private double x;
    private double y;

    /**
     * Konstruktor pro vytvoření nových souřadnic.
     * @param x velikost souradnice x
     * @param y velikost souradnice y
     */
    public Coordinate(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Konstruktor pro JSON file.
     * @param JSON JSON objekt s daty
     */
    public Coordinate(JSONObject JSON){
        this.x = JSON.getDouble("x");
        this.y = JSON.getDouble("y");
    }

    /**
     * Metoda pro získání hodnty X-ové souřadnice.
     * @return hodnotu X (double)
     */
    public double getX() {
        return x;
    }

    /**
     * Metoda pro získání hodnty Y-ové souřadnice.
     * @return hodnotu Y (double)
     */
    public double getY() {
        return y;
    }

    /**
     * Metoda pro výpis souřadnic.
     * @return velikost X a Y (String)
     */
    @Override
    public String toString() {
        return "Coordinate{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    /**
     * Metoda pro porovnání souřadnic.
     * @param o objekt typu Coordinate pro porovnani
     * @return true/false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate that = (Coordinate) o;
        return Double.compare(that.x, x) == 0 &&
                Double.compare(that.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
