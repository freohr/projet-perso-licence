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
    protected int taux;

    public Controle() {
        this.taux = 50;
    }

    public Controle(Monde monde) {
        this.monde = monde;
        this.taux = 50;
    }

    public Monde getMonde() {
        return monde;
    }
    
    public void setModele(Monde monde) {
        this.monde = monde;
    }

    public int getTaux() {
        return taux;
    }

    public void setTaux(int taux) {
        if(taux >= 0 && taux <= 100)
            this.taux = taux;
        else
            this.taux = 50;
    }
    
    public void initMonde(int size) {
        if (size > 0) {
            if(taux == 50)
                monde.init(size);
            else
                monde.init(size, taux);
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

    public void reset() {
        monde.empty();
    }

    public void changeCell(int x, int y) {
        if(x >= 0 && x < monde.getSize() && y >= 0 && y < monde.getSize())
            monde.inverseCell(x, y);
    }
}
