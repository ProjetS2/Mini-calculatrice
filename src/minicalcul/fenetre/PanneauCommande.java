/* 
 * PanneauCommande.java                            14 avr. 2015
 * IUT INFO1 Projet S2 2014-2015
 */
package minicalcul.fenetre;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import minicalcul.programme.commandes.Commandes;
import minicalcul.programme.commandes.ConsoleGestionTableur;

/**
 * Panneau de l'interpréteur de commandes situé en bas de l'écran
 * @author Thomas Affre
 * @author Thibaut Méjane
 * @author Florian Louargant
 * @author Clément Zeghmati
 * @version 1.1
 */
@SuppressWarnings("serial")
public class PanneauCommande extends JPanel {
    
    /** Message affiché lorsqu'une instruction est trop longue */
    private static final String ERREUR_LONGUEUR_CHAINE = "\nErreur de "
            + "longueur : la commande ne peut excéder 75 caractères.";

    /** Différents modes sur lesquels on peut se situer */
    private final static String[] MODES = 
        {"Calculatrice", "Gestionnaire de la mémoire", "Tableur"};
    
    /** Référence à la fenêtre pour intéragir avec la mémoire et le tableur */
    private FenetrePrincipale laFenetre;

    /** Référence à la console permettant d'envoyer les commandes passées */
    private Commandes commandes;
    
    /** Label du mode */
    private JLabel mode;
    
    /** Label de la flèche indiquant l'invite de commandes */ 
    private JLabel fleche;
    
    /** Label du mode sur lequel on se situe */
    private JLabel modeActuel;

    /** Champs de saisie des commandes d'intéraction avec l'application */
    private JTextField ligneDeCommande;

    /** Bouton de validation d'une commande */
    private JButton validerCommande;
    
    /** Sauvegarde des dernières commandes saisies par l'utilisateur */
    private ArrayList<String> sauvegardeCommandes;
    
    /** Position dans la liste de sauvegarde */
    private int positionSauvegarde;

    /**
     * Constructeur du panneau d'interpréteur de commandes
     * @param laFenetre Fenetre à laquelle le panneau est appliqué
     */
    public PanneauCommande(FenetrePrincipale laFenetre) {
        this.laFenetre = laFenetre;
        
        // Liste des commandes précédentes
        this.sauvegardeCommandes = new ArrayList<String>();
        
        this.setLayout(null);  // Positionnement des éléments par valeur absolue
        this.setBorder(BorderFactory      // Bordure
                .createTitledBorder(" Interpréteur de commandes "));
        this.setBounds(18, 605, 977, 100); // Taille
        
        // Label du mode
        this.mode = new JLabel("Mode : ");              // Constructeur
        this.mode.setBounds(20, 60, 55, 30);            // Taille et position
        this.mode.setFont(new Font("Arial", 0, 16));    // Police
        this.add(this.mode);
        
        // Fleche
        this.add(this.getFleche());
        
        // Type du mode
        this.modeActuel = new JLabel(MODES[0]); // Calculatrice au lancement
        this.modeActuel.setBounds(70, 60, 220, 30);                // Taille
        this.modeActuel.setFont(new Font("Arial", Font.BOLD, 16)); // Police
        this.add(this.modeActuel);

        // Ligne de commande
        this.add(this.getLigneDeCommande());

        // Bouton valider
        this.add(this.getValiderCommande());
    }

    /**
     * Applelée lorsque l'utilisateur clique sur le bouton valider ou appuie
     * sur la touche ENTREE
     */
    public void commandeValidee() {
        
        // On contrôle d'abord si le champs n'est pas vide
        if (this.ligneDeCommande.getText().equals("")) {
            // On montre à l'utilisateur que son action a été prise en compte
            this.laFenetre.ajoutLigneConsole("> "); 
            return; // Inutile de continuer
        }
        
        // La commande ne peut excéder 75 caractère. Si c'est le cas on stoppe
        if (this.ligneDeCommande.getText().length() > 75) {
            this.laFenetre.ajoutLigneConsole(this.ligneDeCommande.getText()
                    + ERREUR_LONGUEUR_CHAINE);
            return; // Inutile de continuer
        }

        // On ajoute la commande à la sauvegarde et on met le curseur à la fin
        this.sauvegardeCommandes.add(this.ligneDeCommande.getText());
        this.positionSauvegarde = this.sauvegardeCommandes.size();
        
        // On traite en fonction du mode sur lequel on se trouve
        if (this.modeActuel.getText() == MODES[0]) {
            // Mode calculatrice
            this.traitementCalculatrice();
        } else if (this.modeActuel.getText() == MODES[1]) {
            // Mode gestion de la memoire
            this.traitementGestionMemoire();
        } else if (this.modeActuel.getText() == MODES[2]) {
            // Mode tableur
            this.traitementTableur();
        }
        
        this.ligneDeCommande.setText(""); // On vide la ligne de commande
    }

    /**
     * Appelée lorsqu'une commande est validée et qu'on est dans le mode
     * calculatrice
     */
    private void traitementCalculatrice() {
        // On traite la demande
        if (this.ligneDeCommande.getText().equals("MEM")) {
            this.passageModeGestionMemoire();
        } else if (this.ligneDeCommande.getText().equals("TAB")) {
            this.passageModeTableur();
        } else {
            // Envoie la requête de calcul
            this.laFenetre.ajoutLigneConsole(this.commandes.getCalculs().
                    traitementCommande(this.ligneDeCommande.getText()));
        }
    }

    /**
     * Appelée lorsqu'une commande est validée et qu'on est dans le mode
     * gestionnaire de la mémoire
     */
    private void traitementGestionMemoire() {
        // On affiche sur la console la saisie
        this.laFenetre.ajoutLigneConsole("$ " + this.ligneDeCommande.getText());  
        
        // On traite la demande
        if (this.ligneDeCommande.getText().equals("QUIT")) {
            this.passageModeCalculatrice();
        } else {
            // Envoie la requête au gestionnaire de la mémoire
            this.laFenetre.ajoutLigneConsole(this.commandes.getMemoires().
                    traitementCommande(this.ligneDeCommande.getText()));
        }
    }

    /**
     * Appelée lorsqu'une commande est validée et qu'on est dans le mode
     * tableur
     */
    private void traitementTableur() {
        if (this.ligneDeCommande.getText().equals("QUIT")) {
            // On retourne au mode calculatrice
            this.laFenetre.ajoutLigneConsole("$ QUIT");
            this.passageModeCalculatrice();
            return;
        } // else traitement commande
        
        if (estUneCommandeTableur(
                this.ligneDeCommande.getText().split(" ")[0])) {            
            /*
             * Si la première sous-chaine est une commande de gestion du 
             * tableur, on ne traite pas la commande dans la console de calcul
             */
            this.laFenetre.ajoutLigneConsole(this.commandes.getGestionTableur()
                    .traitementCommande(this.ligneDeCommande.getText()));
            
        } else {
            // Envoie la requête au tableur
            this.laFenetre.ajoutLigneConsole(this.commandes.getTableur().
                    traitementCommande(this.ligneDeCommande.getText()));
        }
        
        // On met à jour toutes les cellules s'il y a eu du changement
        this.commandes.getTableauCellules().miseAJourTableur();      
    }
    
    /**
     * Appelée lorsque l'utilisateur passe au mode Calculatrice
     */
    private void passageModeCalculatrice() {
        this.modeActuel.setText(MODES[0]); // Mode gestionnaire de mémoire
        this.laFenetre.ajoutLigneConsole("? MODE CALCULATRICE");
        
        // On masque l'onglet "Tableur" et démasque "Zones mémoires"
        this.laFenetre.getLesOnglets().setEnabledAt(0, true);   // 0 : Zones
        this.laFenetre.getLesOnglets().setEnabledAt(1, false);  // 1 : Tableur
        
        // On change d'onglet en passant à "Zones mémoires"
        this.laFenetre.getLesOnglets().setSelectedIndex(0);
        
        // On ne pourra plus ouvrir ou enregistrer si on vient du mode tableur
        this.laFenetre.getLeMenu().desactivationFonctionsTableur();
    }

    /**
     * Appelée lorsque l'utilisateur passe au mode Gestionnaire de la mémoire
     */
    private void passageModeGestionMemoire() {
        this.modeActuel.setText(MODES[1]); // Mode gestionnaire de mémoire
        this.laFenetre.ajoutLigneConsole("$ MODE GESTIONNAIRE DE LA MEMOIRE");        
    }
    
    /**
     * Appelée lorsque l'utilisateur passe au mode Tableur
     */
    private void passageModeTableur() {
        this.modeActuel.setText(MODES[2]); // Mode tableur
        
        // On masque l'onglet "Zones mémoires" et démasque "Tableur"
        this.laFenetre.getLesOnglets().setEnabledAt(0, false); // 0 : Zones
        this.laFenetre.getLesOnglets().setEnabledAt(1, true);  // 1 : Tableur
        
        // On change d'onglet
        this.laFenetre.getLesOnglets().setSelectedIndex(1); 
        
        this.laFenetre.ajoutLigneConsole("$ MODE TABLEUR");   
        
        // On pourra désormais ouvrir ou enregistrer un tableur
        this.laFenetre.getLeMenu().activationFonctionsTableur();
    }

    /**
     * Remonte dans la liste des sauvegardes et les affiches sur la ligne de
     * commandes. S'arrète au début.
     */
    private void affichageSauvegardeAvant() {
        // On le fait seulement si on n'est pas au début
        if (this.positionSauvegarde > 0) {
            this.positionSauvegarde--;
            
            // Mise à jour texte console
            this.ligneDeCommande.setText(
                    this.sauvegardeCommandes.get(this.positionSauvegarde));
        }
    }

    /**
     * Redescend dans la liste des sauvegardes et les affiches sur la ligne de
     * commande. S'arrète à la fin.
     */
    private void affichageSauvegardeApres() {
        // On le fait seulement si on n'est pas à la fin de la liste
        if (this.positionSauvegarde < this.sauvegardeCommandes.size() - 1) {
            this.positionSauvegarde++;

            // Mise à jour texte console
            this.ligneDeCommande.setText(
                    this.sauvegardeCommandes.get(this.positionSauvegarde)); 
        }
    }
    
    /**
     * Vérifie si la première sous-chaine saisie est une commande du tableur
     * @param aTester Chaine correspondant à la première sous chaine de la
     *          commande passée
     * @return true s'il s'agit d'une commande, false sinon
     */
    public static boolean estUneCommandeTableur(String aTester) {
        for (int i = 0; i < ConsoleGestionTableur.COMMANDES.length; i++) {
            if (ConsoleGestionTableur.COMMANDES[i].equals(aTester)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Mutateur de commandes
     * @param commandes nouveau commandes
     */
    public void setCommandes(Commandes commandes) {
        this.commandes = commandes;
    }
    
    /**
     * Acceseur à fleche
     * @return fleche 
     */
    public JLabel getFleche() {
        
        if (this.fleche == null) {
            this.fleche = new JLabel(">");                // Constructeur
            this.fleche.setBounds(35, 25, 20, 30);        // Taille et positions
            this.fleche.setFont(new Font("Arial", Font.BOLD, 30)); // Police
        }
        return this.fleche;
    }
    

    /**
     * Acceseur à ligneDeCommande
     * @return ligneDeCommande 
     */
    public JTextField getLigneDeCommande() {
        
        if (this.ligneDeCommande == null) {
            this.ligneDeCommande = new JTextField();
            this.ligneDeCommande.setBounds(65, 25, 780, 30);          // Taille
            this.ligneDeCommande.setFont(new Font("Courier", 0, 16)); // Police
            this.ligneDeCommande.setForeground(Color.BLACK); // Couleur police
            
            // Ecouteur clavier
            this.ligneDeCommande.addKeyListener(new KeyAdapter() {
                
                /* (non-Javadoc)
                 * @see java.awt.event.KeyListener
                 *              #keyPressed(java.awt.event.KeyEvent)
                 */
                @Override
                public void keyPressed(KeyEvent e) {

                    if (e.getKeyCode() == KeyEvent.VK_ENTER) { 
                        commandeValidee();              // Touche ENTREE
                    } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                        affichageSauvegardeAvant();     // Flêche haut
                    } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                        affichageSauvegardeApres();     // Flêche bas
                    }

                    /*
                     * On empeche l'utilisation de ces raccourcis si on est pas 
                     * dans le mode tableur. 
                     * 1 : index de l'onglet du tableur 
                     */
                    if (laFenetre.getLesOnglets().getSelectedIndex() != 1) {
                        return;
                    }

                    if ((e.getKeyCode() == KeyEvent.VK_S)  // Ctrl+S
                            && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                        laFenetre.getLeMenu().raccourciSauvegarde();
                    } else if ((e.getKeyCode() == KeyEvent.VK_O) // Ctrl+O
                            && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) { 
                        laFenetre.getLeMenu().ouvirFichier();
                    }
                }
            });       
        }
        return ligneDeCommande;
    }

    /**
     * Acceseur à validerCommande
     * @return validerCommande 
     */
    private JButton getValiderCommande() {
        
        if (this.validerCommande == null) {
            this.validerCommande = new JButton("Valider");
            this.validerCommande.setBounds(855, 25, 80, 30);        // Taille
            this.validerCommande.setFont(new Font("Arial", 0, 16)); // Police
            this.validerCommande.setMargin(new Insets(0,0,0,0));    // Marges
            this.validerCommande.addActionListener(new ActionListener() {
                
                @Override
                public void actionPerformed(ActionEvent e) {
                    commandeValidee();
                    
                }
            });         // Ecouteur souris
            this.add(this.validerCommande);
        }
        
        return this.validerCommande;
    }
}