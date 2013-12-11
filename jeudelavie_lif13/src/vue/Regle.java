/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package vue;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author leo
 */
public class Regle extends JPanel{
    JCheckBox checkBox;
    JLabel explication;
    boolean active;
    
    public Regle(String nom, boolean active, String explication)
    {
        super();
        this.checkBox = new JCheckBox(nom);
        this.checkBox.setName(nom);
        this.checkBox.setSelected(active);
        this.active = active;
        this.explication = new JLabel("<html>"+ explication +"</html>");
        
    }
}
