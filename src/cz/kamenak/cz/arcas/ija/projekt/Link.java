package cz.kamenak.cz.arcas.ija.projekt;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/**
 * Třída Path - reprezentuje trasu/linku vozidla
 * @author Daniel Kamenický (xkamen21)
 * @author Vojtěch Olej (xolejv00)
 *
 */
public class Link {
    private List<Coordinate> path = new ArrayList<>();
    private List<Street> ListStreets;
    private String name;
    private int speed;
    private int interval;
    private boolean roundabout;
    private Paint paint;

    /**
     * Konstruktor pro trasu vozidla.
     * @param startingCoords počáteční souřadnice trasy
     * @param ListStreets list ulic které jsou na dané trase
     * @param speed rychlost kterou se vozdila pohybují
     * @param interval doba rozestupu vozidel
     * @param name název linky
     * @param roundabout zda li se vozidla na konci cesty otáčejí a jedou zpět
     */
    public Link(Coordinate startingCoords, List<Street> ListStreets, int speed, int interval, String name, boolean roundabout) {
        this.path.add(startingCoords);
        this.ListStreets = ListStreets;
        initPath();
        this.name = name;
        this.interval = interval;
        this.roundabout = roundabout;
        Random random = new Random();
        this.paint = Color.rgb(random.nextInt(256),random.nextInt(256),random.nextInt(256));
        if (speed < 30)
            this.speed = 30;
        else this.speed = Math.min(speed, 100);
    }

    /**
     * Metoda pro získání barvy vozidel.
     * @return barvu (Paint)
     */
    public Paint getPaint() {
        return paint;
    }

    /**
     * Metoda pro vrácení listu ulic.
     * @return list ulic (List class Street)
     */
    public List<Street> getListStreets() {
        return ListStreets;
    }

    /**
     * Metoda pro vrácení intervalu rozestupu vozidel.
     * @return interval (int)
     */
    public int getInterval() {
        return interval;
    }

    /**
     * Metoda pro vrácení celé trasy.
     * @return trasa linky (List class Coordinate)
     */
    public List<Coordinate> getPath() {
        return path;
    }

    /**
     * Metoda pro vrácení názvu linky.
     * @return název linky (String)
     */
    public String getName() {
        return name;
    }

    /**
     * Metoda pro vrácení, zda li se mají vozidla otáčet na konci linky.
     * @return true/false
     */
    public boolean isRoundabout() {
        return roundabout;
    }

    /**
     * Metoda pro vrácení rychlosti vozidel na lince.
     * @return rychlost vozidel (int)
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * Metoda pro vrácení vzdálenosti mezi dvoumi body.
     * @param a bod A (class Coordinate)
     * @param b bod B (class Coordinate)
     * @return vzdálenost mezi body A a B (Double)
     */
    private double getDistanceOfCoordinates(Coordinate a, Coordinate b) {
        return Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2));
    }

    /**
     * Metoda pro zjištění, zda-li se vozidlo nachází na zastávce.
     * @param positionOfVehicle pozice vozidla
     * @return true/false
     */
    public boolean PositionSameAsStop(Coordinate positionOfVehicle){
        for(Street street : ListStreets){
            for(Stop stop : street.getStopList()){
                if(Math.round(stop.getPosition().getX()) == Math.round(positionOfVehicle.getX()) && Math.round(stop.getPosition().getY()) == Math.round(positionOfVehicle.getY())){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Metoda pro vracení konkrétní zastávky na pozici vozidla.
     * @param positionOfVehicle pozice vozidla
     * @return zastávku (class Stop)
     */
    public Stop getStopOnPosition(Coordinate positionOfVehicle){
        for(Street street : ListStreets){
            for(Stop stop : street.getStopList()){
                if(Math.round(stop.getPosition().getX()) == Math.round(positionOfVehicle.getX()) && Math.round(stop.getPosition().getY()) == Math.round(positionOfVehicle.getY())){
                    return stop;
                }
            }
        }
        return null;
    }

    /**
     * Metoda pro inicializaci trasy linky. Získá počáteční bod a projde všechny zastávky trasy a vytvoří si cestu pomocí spojování počatečních a koncových bodů ulice.
     */
    private void initPath(){
        int position = -1;
        for(int i = 0; i<ListStreets.size(); i++){
            for(int j = 0; j<ListStreets.size(); j++) { //PREDELAT NA WHILE CYKLUS
                if(path.get(path.size()-1).getX() == ListStreets.get(j).getStart().getX() && path.get(path.size()-1).getY() == ListStreets.get(j).getStart().getY() && position != j){
                    path.add(ListStreets.get(j).getEnd());
                    position = j;
                    j=ListStreets.size();
                }
                else if(path.get(path.size()-1).getX() == ListStreets.get(j).getEnd().getX() && path.get(path.size()-1).getY() == ListStreets.get(j).getEnd().getY() && position != j){
                    path.add(ListStreets.get(j).getStart());
                    position = j;
                    j=ListStreets.size();
                }
            }
        }
    }

    /**
     * Metoda pro získání souřadnic vozidla podle ujeté vzdálenosti.
     * @param distance ujetá vzdálenost vozidla
     * @return nové souřadnice vozidla (class Coordinate)
     */
    public Coordinate getCoordinateByDistance(double distance) {
        double length = 0;

        Coordinate a = null;
        Coordinate b = null;

        for (int i = 0; i < path.size()-1; i++){
            a = path.get(i);
            b = path.get(i + 1);

            if(length + getDistanceOfCoordinates(a, b) >= distance){
                break;
            }
            length += getDistanceOfCoordinates(a, b);
        }

        if(a == null){
            return null;
        }

        double driven = (distance - length) / getDistanceOfCoordinates(a, b);

        return new Coordinate(a.getX() + (b.getX() - a.getX()) * driven, a.getY() + (b.getY() - a.getY()) * driven);
    }

    /**
     * Metoda pro vracení celkové vzdálenosti cesty vozidla.
     * @return vzdálensot cesty (double)
     */
    public double getPathValue(){
        double size = 0;
        for (int i = 0; i < path.size()-1; i++){
            size += getDistanceOfCoordinates(path.get(i), path.get(i+1));
        }
        return size;
    }

    /**
     * Meotda pro vracení ulice po které jede konkretní vozidlo. Slouží pro nastavení rychlosti podle prvku zpoždění ulice(delay).
     * @param distance ujetá vzdálenost vozidla
     * @return ulici (class Street)
     */
    public Street getStreetWithVehicle(double distance) {
        double length = 0;

        //promenne pro souradnice
        Coordinate a;
        Coordinate b;

        //cyklus pro projeti vsech casti cesty
        for (int i = 0; i < path.size()-1; i++){
            a = path.get(i);
            b = path.get(i + 1);

            //pokud ujeta vzdalenost je vetsi jak vzdalenost mezi prvnimi souradnicemi, pokracuje cyklus
            if(length + getDistanceOfCoordinates(a, b) >= distance){
                return ListStreets.get(i);
            }
            //pricteni prvni vasti trasy
            length += getDistanceOfCoordinates(a, b);
        }
        return ListStreets.get(0);
    }
}
