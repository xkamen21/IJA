package cz.kamenak.cz.arcas.ija.projekt;

import java.time.LocalTime;
/**
 * Interface Timeupdate - slouží pro update vozidel v průběhu času
 * @author Daniel Kamenický (xkamen21)
 * @author Vojtěch Olej (xolejv00)
 *
 */
public interface TimeUpdate {
    void Update(LocalTime time);
}
