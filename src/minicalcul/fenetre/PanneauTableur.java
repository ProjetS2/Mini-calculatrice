/* 
 * PanneauTableur.java                            9 avr. 2015
 * IUT info1 Groupe 3 2014-2015
 */
package minicalcul.fenetre;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Panneau du tableur
 * @author Cl�ment Zeghmati
 * @version 0.1
 */
@SuppressWarnings("serial")
public class PanneauTableur extends JPanel {
    
    /** Panneau permettant de rajouter un scroll au tableur */
    private JScrollPane scrollPane;
    
    /** Premi�re colone du tableau qui reste fig�e */
    private JPanel premiereColone;
    
    /** Tableau d'affichage du tableur */
    private JPanel tableau;

    /** Labels d'affichage des chiffres dans la premi�re colone */
    private JLabel[] lignes;

    /** Labels d'affichage des lettres dans la premi�re ligne */
    private JLabel[] colones;
    
    /** Grille correspondant aux cellules du tableur */
    private GrilleCellules grilleTableur;

    /**
     * Construction du panneau
     */
    public PanneauTableur() {
        this.setLayout(null);
        
        // Cr�ation des headers du tableur (on ajoute 1 pour les headers)
        this.lignes = new JLabel[21]; 
        this.colones = new JLabel[26];
                
        this.creationPremiereColone();

        // On va cr�er le tableur ligne par ligne en commancant par le header
        this.creationTableur();
        this.add(this.scrollPane);
    }

    /**
     * Cr�� la premi�re colone du tableur
     */
    private void creationPremiereColone() {

        this.premiereColone = new JPanel(new GridLayout(20,1));
        this.premiereColone.setBounds(20, 34, 35, 491);
        
        for (int i = 0; i < this.lignes.length - 1; i++) {
            this.lignes[i] = new JLabel(Integer.toString(i + 1), JLabel.CENTER);
            this.lignes[i].setBorder(
                    BorderFactory.createLineBorder(Color.GRAY));
            this.premiereColone.add(this.lignes[i]);
        }
        this.premiereColone.setOpaque(true);

        this.add(this.premiereColone);
    }

    /**
     * Cr�� et affiche le tableur
     */
    private void creationTableur() {
        
        // Cr�ation des cellules
        this.grilleTableur = new GrilleCellules();

        // Cr�ation du tableur
        this.tableau = new JPanel(new GridLayout(21,26));
        this.tableau.setPreferredSize(new Dimension(2200,0));

        this.scrollPane = new JScrollPane(this.tableau);
        this.scrollPane.setBounds(55, 10, 370, 530);      // Taille
        
        char tmp = 65; // A = 65 .. Z = 90

        for (int i = 0; i < this.colones.length; i++) {
            Character nom = new Character(tmp);
            this.colones[i] = new JLabel(nom.toString(), JLabel.CENTER);
            this.colones[i].setBorder(
                    BorderFactory.createLineBorder(Color.GRAY));
            this.tableau.add(this.colones[i]);
            tmp++;
        }     
        
        // Vitesses de d�filement du scroll avec la fl�che et la molette
        this.scrollPane.getHorizontalScrollBar().setBlockIncrement(50);
        this.scrollPane.getHorizontalScrollBar().setUnitIncrement(50);
                 
        // On continue pour les autres lignes
        for (int i = 0; i < this.lignes.length - 1; i++) {
            
            // On cr�� chaque cellule de la ligne
            for (int j = 0; j < this.colones.length; j++) {
                this.grilleTableur.constructionCellule(i,j);
                this.tableau.add(this.grilleTableur.getTableau()[i][j]);   
            }
        }        
    }

    /**
     * Acceseur � grilleTableur
     * @return grilleTableur 
     */
    public GrilleCellules getGrilleTableur() {
        return grilleTableur;
    }
}