/* 
 * GrilleCellules.java                            14 avr. 2015
 * IUT INFO1 Projet S2 2014-2015
 */
package minicalcul.fenetre;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JTextField;

/**
 * Grille de JTextFields correspondant aux cellules du tableur 
 * @author Thomas Affre
 * @author Thibaut Méjane
 * @author Florian Louargant
 * @author Clément Zeghmati
 * @version 1.1
 */
public class GrilleCellules {

    /** 
     * Cellules du tableur 
     *      - 1ère dimension : lignes du tableur
     *      - 2ème dimension : cellules de la ligne 
     */
    private JTextField[][] tableau;
    
    /**
     * Constructeur du tableur
     */
    public GrilleCellules() {
        this.tableau = new JTextField[20][26];
    }
    
    /**
     * Construction des cellules du tableau une par une
     * @param numLigne Numéro de la ligne -1 où se trouve la cellule 
     * @param numColone Numéro de la colone -1 où se trouve la cellule 
     */
    public void constructionCellule(int numLigne, int numColone) {

        // Construction à vide
        this.tableau[numLigne][numColone] = new JTextField();
        
        // Propriétés
        
        this.tableau[numLigne][numColone].setEditable(false); // Non éditable
        this.tableau[numLigne][numColone].setBackground(Color.WHITE);// Fond
        // Police
        this.tableau[numLigne][numColone].setFont(new Font("Courier", 0, 12));
        this.tableau[numLigne][numColone].setBorder(    // Bordure
                BorderFactory.createLineBorder(Color.GRAY));
        // Positionnement du texte
        this.tableau[numLigne][numColone]
                .setHorizontalAlignment(JTextField.CENTER);
    }

    /**
     * Teste si une cellule passée en argument est initialisée
     * @param coordonnees Coordonnées de la cellule à tester
     * @return true si la cellule est initialisée, false sinon
     */
    public boolean estInitialisee(int[] coordonnees) {
        return !(this.tableau[coordonnees[0]][coordonnees[1]].getText()
                                .equals("")
                 || this.tableau[coordonnees[0]][coordonnees[1]].getText()
                                .equals("?"));   
    }

    /**
     * Recherche la valeur brute contenue dans la cellule passée en argument
     * @param coordonnees Coordonnées de la cellule
     * @return valeur de la cellule passée en argument
     */
    public String valeurCellule(int[] coordonnees) {
        return this.tableau[coordonnees[0]][coordonnees[1]].getText();
    }

    /**
     * Met à jour le contenu de la cellule passée en argument par la valeur
     * passée en argument sans prendre en compte une éventuelle formule
     * @param coordonnees Coordonnées de la cellule
     * @param valeur Nouvelle valeur de la cellule
     */
    public void miseAJourValeur(int[] coordonnees, String valeur) {
        this.tableau[coordonnees[0]][coordonnees[1]].setText(valeur);
    }
    
    /**
     * Réinitialise toutes les cellules du tableur
     */
    public void reinitialisationInterfaceTableur() {
        
        for (int i = 0; i < this.tableau.length; i++) {
            for (int j = 0; j < this.tableau[i].length; j++) {
                this.tableau[i][j].setText("");   
            }
        }   
    }

    /**
     * Accesseur à tableau
     * @return tableau 
     */
    public JTextField[][] getTableau() {
        return tableau;
    }
}