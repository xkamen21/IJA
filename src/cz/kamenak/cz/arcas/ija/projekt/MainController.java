package cz.kamenak.cz.arcas.ija.projekt;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;

import java.time.LocalTime;
import java.util.*;

import static javafx.geometry.Pos.CENTER;
import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.ORANGE;
/**
 * Třída kontroler - zajišťuje a propojuje veškteré věci pro chod programu
 * @author Daniel Kamenický (xkamen21)
 * @author Vojtěch Olej (xolejv00)
 *
 */
public class MainController {

    @FXML
    private Pane content;

    @FXML
    private Slider changedTime;

    @FXML
    private TextField timeField;

    @FXML
    private TextField streetName;

    @FXML
    private TextField delayField;

    @FXML
    private TextArea timeTable;

    @FXML
    private TextArea linkInfo;

    @FXML
    private Spinner<Integer> hourSpinner;

    @FXML
    private Spinner<Integer> minuteSpinner;

    @FXML
    private Spinner<Integer> secondSpinner;

    private int countingToRealSecond = 0;
    private int seconds = java.util.Calendar.getInstance().getTime().getSeconds();
    private int minutes = java.util.Calendar.getInstance().getTime().getMinutes();
    private int hours = java.util.Calendar.getInstance().getTime().getHours();

    private final List<TimeUpdate> vehicleUpdate = new ArrayList<>();

    private List<Vehicle> VehicleList = new ArrayList<>();
    private List<Link> PathList = new ArrayList<>();
    private final List<Street> StreetList = new ArrayList<>();


    private Timer timer;
    private LocalTime time = LocalTime.now();

    /**
     * Metoda pro změnu rychlosti času. Hodnota měněna pomocí slideru v layout.fxml.
     */
    @FXML
    private void timeChange_method(){
        double scale = changedTime.getValue();
        timer.cancel();
        startTime(scale);
    }

    /**
     * Metoda pro zvětšování či zmenšování mapy.
     * @param event zachytává pohyb kolečka myši
     */
    @FXML
    private void Zoom_methode(ScrollEvent event) {
        event.consume();

        double zoom = event.getDeltaY() > 0 ? 1.1 : 0.9;

        if (event.getDeltaY() == 0)
            zoom = 1;

        content.setScaleX(zoom * content.getScaleX());
        content.setScaleY(zoom * content.getScaleY());
        content.layout();
    }

    /**
     * Metoda pro zvyšení zpoždění na označené ulici. Zvyšovaní pomocí tlačítka v layout.fxml.
     */
    @FXML
    private void increaseDelay(){
        for(Street street :StreetList){
            if(street.isSelected()){
                if((street.getDelay()/200)<100) {
                    street.setDelay(street.getDelay() + 200);
                    String strDelay = "Delay is set on: ";
                    strDelay += ((street.getDelay()) / 200) + "%";
                    delayField.clear();
                    delayField.appendText(strDelay);
                }
            }
        }
    }

    /**
     * Metoda pro snížení zpoždění na označené ulici. Sniživání pomocí tlačítka v layout.fxml.
     */
    @FXML
    private void decreaseDelay(){
        for(Street street :StreetList){
            if(street.isSelected()){
                if(street.getDelay()>0){
                    street.setDelay(street.getDelay()-200);
                    String strDelay = "Delay is set on: ";
                    strDelay += ((street.getDelay())/200) + "%";
                    delayField.clear();
                    delayField.appendText(strDelay);
                }
            }
        }
    }

    /**
     * Metoda pro anulování zpoždění na označené ulici. Anulování pomocí tlačítka v layout.fxml.
     */
    @FXML
    private void resetDelay(){
        for(Street street :StreetList){
            if(street.isSelected()){
                street.setDelay(0);
                String strDelay = "Delay is set on: 0%";
                delayField.clear();
                delayField.appendText(strDelay);
            }
        }
    }

    /**
     * Metoda pro zrušení označení ulic pomocí tlačítka v layout.fxml.
     */
    @FXML
    private void unmarkedStreets(){
        for(Street street :StreetList){
            street.setSelected(false);
            street.setGuiBlack();
        }
        setBasicText();
        linkInfo.clear();
        timeTable.clear();
    }

    /**
     * Metoda pro nastavení nového času pomocí tlačítka a spinneru v layout.fxml.
     */
    @FXML
    private void setNewTime(){
        hours = hourSpinner.getValueFactory().getValue();
        minutes = minuteSpinner.getValueFactory().getValue();
        seconds = secondSpinner.getValueFactory().getValue()-1;
        setAndPrintTime();
        for(int i = 0; i<VehicleList.size(); i++){
                content.getChildren().remove(VehicleList.get(i).getGUI().get(0));
                vehicleUpdate.remove(VehicleList.get(i));
                VehicleList.remove(VehicleList.get(i));
                i--;
        }
        setVehicleAfterTimeUpdate();
    }

    /**
     * Metoda pro nastavení vozidel, po změně času, na místo, kde by podle času měla být.
     */
    public void setVehicleAfterTimeUpdate(){
        boolean nightShift = false;
        if(hours > 21 || hours < 6){
            nightShift = true;
        }
        for(Link path : PathList){
            int interval;
            if(nightShift){
                interval = 30;
            }
            else{
                interval = path.getInterval();
            }
            double pathLength;
            int time = (hours*60*60+minutes*60+seconds)%(interval*60);
            double distance;
            if(path.isRoundabout()) {
                pathLength = path.getPathValue()*2;
            } else {
                pathLength = path.getPathValue();
            }
            double speed = (double)path.getSpeed()/20;
            int tmp = 0;
            while(true){
                time += tmp*interval*60;
                if(time!=0){
                    distance = time*speed;
                    if(distance < pathLength){
                        if(path.isRoundabout() && distance>path.getPathValue()){
                            summonVehicle(path, (path.getPathValue()-(distance-path.getPathValue())), (path.getSpeed()*(-1)), path.getCoordinateByDistance(path.getPathValue()-distance-path.getPathValue()));
                        }
                        else{
                            summonVehicle(path, distance, path.getSpeed(), path.getCoordinateByDistance(distance));
                        }
                    }
                    else{
                        break;
                    }
                }
                tmp++;
            }
        }
    }

    /**
     * Metoda pro nastavení a vykreslení všech grafických prvků.
     * @param elements prvky pro vykreslení
     */
    public void setElements(List<Drawable> elements) {
        hourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23));
        hourSpinner.getValueFactory().setValue(hours);
        minuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59));
        minuteSpinner.getValueFactory().setValue(minutes);
        secondSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59));
        secondSpinner.getValueFactory().setValue(seconds);
        timeField.setAlignment(CENTER);
        setAndPrintTime();
        for (Drawable tmp : elements) {
            content.getChildren().addAll(tmp.getGUI());
            //jelikoz vehicle implementuje TimeUpdate, prida se nam to do listu updates
            if(tmp instanceof TimeUpdate){
                vehicleUpdate.add((TimeUpdate) tmp);
            }
        }
            }

    /**
     * Metoda pro nastavení původního textu v oknech pro výpis informací.
     */
    public void setBasicText(){
        streetName.clear();
        delayField.clear();
        streetName.appendText("Mark street for it name");
        delayField.appendText("Delayed in percent");
    }


    /**
     * Metoda pro inicializaci dat načtených v hlavní třídě.
     * @param StreetList list všech ulic
     * @param PathList list všech linek
     */
    public void initData(HashMap<String, Street> StreetList, List<Link> PathList){
        this.StreetList.addAll(StreetList.values());
        this.PathList = PathList;

        delayField.setAlignment(CENTER);
        streetName.setAlignment(CENTER);
        streetName.appendText("Mark street for it name");
        delayField.appendText("Delayed in percent");
        for (Street street : this.StreetList) {
            street.getGUI().get(0).setOnMouseClicked(MouseEvent -> {
                linkInfo.clear();
                timeTable.clear();
                for (Street street1 : this.StreetList) {
                    street1.setSelected(false);
                    street1.getGUI().get(0).setStroke(BLACK);
                }
                if (street.isSelected()) {
                    street.setSelected(false);
                    street.getGUI().get(0).setStroke(BLACK);
                } else {
                    street.setSelected(true);
                    street.getGUI().get(0).setStroke(ORANGE);
                    streetName.clear();
                    streetName.appendText(street.getName());
                    String strDelay = "Delay is set on: ";
                    strDelay += ((street.getDelay()) / 10) + "%";
                    delayField.clear();
                    delayField.appendText(strDelay);
                }
            });
        }
    }

    /**
     * Metoda pro vyvolání vozdila. Vozidlo, po vyvolání, se objeví na mapě.
     * @param path trasa vozidla
     * @param distance vzdálenost vozidla
     * @param speed rychlost vozidla
     * @param position pozice vozidla
     */
    public void summonVehicle(Link path, double distance, int speed, Coordinate position){
        Vehicle vehicle = new Vehicle(position, path, path.isRoundabout(), speed);
        vehicle.setDistance(distance);
        vehicle.getGUI().get(0).setOnMouseClicked(MouseEvent -> {
            for (Vehicle vehicle1 : VehicleList) {
                if (vehicle1.isPathMarked()) {
                    for (Street street : vehicle1.getPath().getListStreets()) {
                        street.setGuiBlack();
                        street.setSelected(false);
                    }
                    vehicle.setPathMarked(false);
                }
            }
            for (Street street : vehicle.getPath().getListStreets()) {
                street.setGuiGreen();
                street.setSelected(false);
            }
            vehicle.setPathMarked(true);
            setBasicText();
            printInfoAboutLink(vehicle);
        });
        content.getChildren().addAll(vehicle.getGUI());
        vehicleUpdate.add(vehicle);
        VehicleList.add(vehicle);
    }

    /**
     * Metoda pro výpis informací o lince a vozidlu.
     * @param vehicle konkrétní vozidlo o kterém chceme znát informace
     */
    public void printInfoAboutLink(Vehicle vehicle){
        timeTable.clear();
        StringBuilder TT = new StringBuilder();
        for(int i = 0; i<24; i++){
            TT.append(i).append(":");
            if(i<6 || i>21){
                TT.append(" 00, 30\n");
            }
            else{
                if(vehicle.getPath().getInterval() < 4){
                    TT.append(" Every ").append(vehicle.getPath().getInterval()).append(" minutes\n");
                }
                else{
                    boolean skip_first = true;
                    for(int j = 0; j<60; j++){
                        if((i*60+j)%vehicle.getPath().getInterval() == 0){
                            if(skip_first){
                                skip_first = false;
                            }
                            else{
                                TT.append(",");
                            }
                            TT.append(" ").append(j);
                        }
                    }
                    TT.append("\n");
                }
            }
        }
        timeTable.appendText(TT.toString());
        linkInfo.clear();
        StringBuilder info = new StringBuilder("Streets: \n");
        for(Street street : vehicle.getPath().getListStreets()){
            info.append("  - ").append(street.getName()).append("\n");
        }
        info.append("\n---------------------------------------------------------------------------------------------------------\n");
        info.append("\nPosition: \n");
        if(vehicle.getStop() == null){
            info.append(" Start few moments ago.\n");
        }
        else{
            info.append(" Last stop was:\n  - ").append(vehicle.getStop().getName()).append("\n");
        }
        double distance;
        if(vehicle.getPath().isRoundabout()){
            if(vehicle.getSpeed() > 0){
                distance = vehicle.getDistance()/((vehicle.getPath().getPathValue()*2)/100);
            }
            else{
                distance = vehicle.getDistance()/((vehicle.getPath().getPathValue()*2)/100);
                distance = 50 + 50-distance;
            }
        }
        else{
            distance = vehicle.getDistance()/(vehicle.getPath().getPathValue()/100);
        }
        distance = Math.round((distance*100));
        info.append("\nPath length done: ").append(distance/100).append("%\n");
        linkInfo.appendText(info.toString());
    }

    /**
     * Metoda pro vyvolávání vozidel. Volá metodu summonVehicle a vyvolává vozidla čas jejich výjezdu. Rozdíl mezi nočnímy a dennímy spoji.
     */
    public void VehicleSummoner(){
        for(Link path : PathList){
            if(hours < 6 || hours > 21){
                if((minutes == 0 || minutes == 30) && seconds == 0){
                    summonVehicle(path, 0, path.getSpeed(), path.getPath().get(0));
                }
            }
            else if((hours*60+minutes)%path.getInterval()==0 && seconds == 0){
                summonVehicle(path, 0, path.getSpeed(), path.getPath().get(0));
            }
        }
        if(VehicleList.size()>0){
            for(int i = 0; i<VehicleList.size(); i++){
                if(VehicleList.get(i).isRemoveable()){
                    content.getChildren().remove(VehicleList.get(i).getGUI().get(0));
                    vehicleUpdate.remove(VehicleList.get(i));
                    VehicleList.remove(VehicleList.get(i));
                    i--;
                }
            }
        }

    }

    /**
     * Metoda pro vizualizaci virtuálního času.
     */
    public void setAndPrintTime(){
        if(seconds == 59)
        {
            if(minutes == 59){
                if(hours == 23)
                {
                    hours = 0;
                }
                else{
                    hours++;
                }
                minutes = 0;
            }
            else{
                minutes++;
            }
            seconds = 0;
        }
        else{
            seconds++;
        }
        String time;
        if(hours < 10){
            time = "0" + hours + ":";
        }
        else{
            time = hours + ":";
        }
        if(minutes < 10){
            time = time + "0" + minutes + ":";
        }
        else{
            time = time + minutes + ":";
        }
        if(seconds < 10){
            time = time + "0" + seconds;
        }
        else{
            time = time + seconds;
        }
        timeField.clear();
        timeField.appendText(time);
    }

    /**
     * Metoda pro prvotní spuštění času. Čas se nastavuje na aktualní lokální čas.
     * @param scale změna rychlosti toku času
     */
    public void startTime(double scale){
        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                time = time.plusSeconds(1);
                Platform.runLater(() -> {
                    if(countingToRealSecond == 10){
                        countingToRealSecond = 0;
                        setAndPrintTime();
                        VehicleSummoner();
                    }
                    else{
                        countingToRealSecond++;
                    }
                    for(int i = 10; i>0; i--){
                        for(TimeUpdate update : vehicleUpdate){
                            update.Update(time);
                        }
                    }
                });
            }
        }, 0, (long) (100 / scale));
    }
}
