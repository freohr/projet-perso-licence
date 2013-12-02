/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Controleur;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JTextField;
import modele.Monde;

/**
 *
 * @author Freohr
 */
public class InitControler implements ActionListener{
    private Monde monde;

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton init = (JButton) e.getSource();
        
        for(Component c : init.getParent().getComponents()) {
            if("textFieldTaille".equals(c.getName())) {
                JTextField txtf = (JTextField) c;
            
                monde = new Monde(new Integer(txtf.getText()), 3, 2, 3);
            }
        }
    }
    
}
