package cz.kamenak.cz.arcas.ija.projekt;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
/**
 * Třída Link - reprezentuje pohybující se vozidlo
 * @author Daniel Kamenický (xkamen21)
 * @author Vojtěch Olej (xolejv00)
 *
 */
public class Vehicle implements Drawable, TimeUpdate {
    private Coordinate position;
    private double distance = 0;
    private Link path;
    private List<Shape> gui;
    private int speed;
    private boolean roundabout;

    private boolean eliminateMultiplyWaiting = false;
    private int stopWaiter = 0;
    private boolean pathMarked = false;
    private boolean removeAble = false;
    private Stop lastStop;

    /**
     * Konstruktor pro vytvoření vozidla
     * @param position startovní pozice na osách XY
     * @param path trasa vozidla
     * @param roundabout zda li jede tam i zpět, nebo pouze tam
     * @param speed rychlost vozidla
     */
    public Vehicle(Coordinate position, Link path, boolean roundabout, int speed) {
        this.position = position;
        this.path = path;
        this.roundabout = roundabout;
        this.speed = speed;
//        this.indexInContent = indexInContent;
        gui = new ArrayList<>();
        gui.add(new Circle(position.getX(), position.getY(), 8, path.getPaint()));
        gui.get(0).setStroke(Color.rgb(0,0,0));
    }

    //nastaveni novych souradnic vozidla do GUI

    /**
     * Metoda pro změnu souřadnic vozidla.
     * @param coordinate nové souřadnice
     */
    private void moveGui(Coordinate coordinate){
        for(Shape shape : gui){
            shape.setTranslateX((coordinate.getX() - position.getX()) + shape.getTranslateX());
            shape.setTranslateY((coordinate.getY() - position.getY()) + shape.getTranslateY());
        }
    }

    /**
     * Metoda pro získání ujeté vzdálenosti.
     * @return ujetou vzdálenost (double)
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Meotad pro získání rychlosti vozidla.
     * @return rychlost vozidla (int)
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * Metoda pro nastavení ujeté vzdálenosti.
     * @param distance nová vzdálenost
     */
    public void setDistance(double distance) {
        this.distance = distance;
    }

    /**
     * Metoda pro získání poslední projeté zastávky.
     * @return zastávku (class Stop)
     */
    public Stop getStop() {
        return lastStop;
    }

    /**
     * Metoda pro zjištění, zda li vozidlo dojelo do cíle a může být odebráno.
     * @return true/false
     */
    public boolean isRemoveable() {
        return removeAble;
    }

    /**
     * Metoda pro nastavení označení ulice pro delay.
     * @param pathMarked pravdivostní hodnota označení ulice
     */
    public void setPathMarked(boolean pathMarked) {
        this.pathMarked = pathMarked;
    }

    /**
     * Metoda pro pro zjištění, zda li je ulice označená.
     * @return true/false
     */
    public boolean isPathMarked() {
        return pathMarked;
    }

    /**
     * Metoda pro získání trasy vozidla.
     * @return trasu vozidla (class Path)
     */
    public Link getPath() {
        return path;
    }

    /**
     * Metoda pro vracení grafické reprezentace vozidla.
     * @return List prvků Shape
     */
    @Override
    public List<Shape> getGUI() {
        return gui;
    }

    /**
     * Metoda pro update vozidla. Vypočítává nové souřadnice, zjišťuje je-li vozidlo na zastávce a přiřazuje ujetou vzdálenost.
     * Pro plynulejší pohyb je rychlost vozidla dělena 2000, tím je dosažena menší vzdálenost jednoho skoku.
     * @param time aktuální čas
     */
    @Override
    public void Update(LocalTime time) {
        if(roundabout && (distance > path.getPathValue() || distance + (double)(this.speed/2000) < 0)) {
            if(this.speed<0){
                this.removeAble = true;
            }
            else{
                this.speed *= -1;
            }
        }
        else if(distance > path.getPathValue() || distance + (double)(this.speed/2000) < 0){
            this.removeAble = true;
        }
        if(stopWaiter!=0 && stopWaiter<1000){
            stopWaiter++;
            eliminateMultiplyWaiting=true;
            return;
        }
        else {
            stopWaiter=0;
        }
        if(path.PositionSameAsStop(position) && !eliminateMultiplyWaiting){
            stopWaiter++;
            lastStop = path.getStopOnPosition(position);
            return;
        }
        if(!path.PositionSameAsStop(position)&&eliminateMultiplyWaiting){
            eliminateMultiplyWaiting=false;
        }
        Street street = path.getStreetWithVehicle(distance);

        distance += (double) this.speed/(2000+street.getDelay());
        //konec cesty, vozidlo se zastavi
        if(distance > path.getPathValue())
            return;
        Coordinate coords = path.getCoordinateByDistance(distance);
        //nastaveni zmeny souradnic vozidla v GUI
        moveGui(coords);
        //zapsani novych souradnic vozidla
        position = coords;
    }
}
