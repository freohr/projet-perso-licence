/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Controleur;

import modele.Monde;

/**
 *
 * @author p1006099
 */
public class Controle {

    protected Monde monde;

    public Controle() {
    }

    public Controle(Monde monde) {
        this.monde = monde;
    }

    public Monde getMonde() {
        return monde;
    }
    
    public void setModele(Monde monde) {
        this.monde = monde;
    }

    public void initMonde(int size) {
        if (size > 0) {
            monde.init(size);
        }
    }

    public void pause() {
        if (monde.isPaused()) {
            monde.resume();
        } else {
            monde.pause();
        }

    }
    
    public void stop() {
        if(monde.isStopped()) {
            monde.random();
            monde.run();
        } else {
            monde.stop();
        }
    }
    
    public void modifThreadSpeed(int speed) {
        monde.setThreadSpeed(speed);
    }
}
