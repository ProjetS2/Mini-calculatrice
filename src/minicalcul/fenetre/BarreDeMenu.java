/* 
 * BarreDeMenu.java                            4 mai 2015
 * IUT info1 Groupe 3 2014-2015
 */
package minicalcul.fenetre;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import minicalcul.programme.tableur.Tableur;

/**
 * Barre de menu que l'on d�finiera sur la fen�tre
 * @author Cl�ment Zeghmati
 * @version 0.1
 */
@SuppressWarnings("serial")
public class BarreDeMenu extends JMenuBar implements ActionListener {
    
    /** R�f�rence � la fen�tre � laquelle la barre est attach�e */
    private FenetrePrincipale laFenetre;
    
    /** R�f�rence au tableur */
    private Tableur leTableur;
    
    /** Bouton "Fichier" dans la barre de menu */
    private JMenu fichier = new JMenu("Fichier");
    
    /** R�initialise l'�tat du tableur */
    private JMenuItem nouveau = new JMenuItem("Nouveau tableur");
    
    /** Ouvrir un tableur existant */
    private JMenuItem ouvrir = new JMenuItem("Ouvrir...");
    
    /** Enregistre l'�tat de la machine */
    private JMenuItem enregistrer = new JMenuItem("Enregistrer");

    /** Enregistre l'�tat du tableur dans un nouveau fichier binaire */
    private JMenuItem enregistrerSous = new JMenuItem("Enregistrer sous...");
    
    /** Bouton du menu permettant de se d�connecter */
    private JMenuItem quitter = new JMenuItem("Quitter");
    
    /** Fichier actuellement ouvert */
    private String fichierOuvert;

    /**
     * Construction de la barre de menu
     * @param laFenetre R�f�rence � la fen�tre � laquelle la barre est attach�e
     */
    public BarreDeMenu(FenetrePrincipale laFenetre) {
        this.laFenetre = laFenetre;
        
        // Menu fichier
        this.add(this.fichier);
        this.fichier.add(this.nouveau);
        this.fichier.add(this.ouvrir);
        this.fichier.addSeparator();
        this.fichier.add(this.enregistrer);
        this.fichier.add(this.enregistrerSous);
        this.fichier.addSeparator();
        this.fichier.add(this.quitter);
        
        // Ecouteurs
        this.nouveau.addActionListener(this);
        this.ouvrir.addActionListener(this);
        this.enregistrer.addActionListener(this);
        this.enregistrerSous.addActionListener(this);
        this.quitter.addActionListener(this);
        
    }

    /**
     * Restaure un fichier de sauvegarde du tableur
     */
    public void ouvirFichier() {
        
        // objet tampon dans lequel est plac� l'objet lu dans le fichier  
        String[][] tampon = null;
        
        // Boite de dialogue d'ouverture de fichier
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(
                new FileNameExtensionFilter("Binary Files (*.bin)", "bin"));
        fileChooser.setDialogTitle("Ouvrir"); 
        
        int selection = fileChooser.showOpenDialog(this.laFenetre);
        
        if (selection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            // On garde le chemin de sauvegarde
            this.fichierOuvert =
                    fileToSave.getAbsolutePath();
            
            // On met � jour le titre de la fen�tre pour le savoir
            this.laFenetre.setTitle(this.fichierOuvert
                     + " - Mini-Calcultrice");
            
            // Ouverture du fichier et lecture de l'objet qu'il contient
            try (ObjectInputStream fichier = new ObjectInputStream(
                    new FileInputStream(this.fichierOuvert))) {           

                // lecture de l'objet contenu dans le fichier
                tampon = (String[][]) fichier.readObject();
                
                // On l'applique au tableur
                this.leTableur.restaurationTableurFichier(tampon);
                
                // On le sp�cifie sur la console
                this.laFenetre.ajoutLigneConsole(this.fichierOuvert 
                        + " : restaur�.");

            } catch (ClassNotFoundException erreur) {
                // Le contenu du fichier n'est pas un objet           
            } catch (ClassCastException erreur) {
                // Le contenu du fichier n'est pas un objet �2 dimensions    
            } catch (IOException erreur) {
                // Probl�me d'acc�s au fichier
            }
                    }
    }

    /**
     * Sauvegarde le contenu du tableur dans le fichier sp�cifi� dans
     * fichierEnregistrement
     */
    private void sauvegarderFichier() {
        String[][] aSauvegarder = this.leTableur.sauvegardeEtatTableur();

        // cr�ation et ouverture du fichier o� l'on effectuera la sauvegarde
        try (ObjectOutputStream fichier = new ObjectOutputStream(
             new FileOutputStream(this.fichierOuvert))) {
            
            // on �crit le tableau dans le fichier
            fichier.writeObject(aSauvegarder);  
                        
            // On met � jour le titre de la fen�tre pour le savoir
            this.laFenetre.setTitle(this.fichierOuvert
                     + " - Mini-Calcultrice");
            
            // On le sp�cifie sur la console
            this.laFenetre.ajoutLigneConsole("Tableur sauvegard� sur : " 
                    + this.fichierOuvert);
            
        } catch (IOException e) {
            System.out.println("Echec");
        }        
    }

    /**
     * Demande via une boite dialoque le chemin vers lequel l'utilisateur
     * souhaite sauvegarder dans un fichier le contenu actuel du tableur
     */
    private void sauvegarderFichierSous() {
        
        // Boite de dialogue de sauvegarde
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(
                new FileNameExtensionFilter("Binary Files (*.bin)", "bin"));
        fileChooser.setDialogTitle("Enregistrer sous");  
        
        int selection = fileChooser.showSaveDialog(this.laFenetre);
        
        if (selection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            // On garde le chemin de sauvegarde
            this.fichierOuvert =
                    fileToSave.getAbsolutePath() + ".bin";
            
            // On proc�de � la sauvegarde
            this.sauvegarderFichier();            
        }
    }
    
    /**
     * Appel� lorsque le raccourci de sauvegarde est utilis� Ctrl + S
     */
    public void raccourciSauvegarde() {
        if (this.fichierOuvert == null) {
            /*
             * Si l'utilisateur souhaite choisir le chemin de sauvegarde ou
             * s'il n'y a pas eu d'enregistrement au pr�alable, on r�oriente
             * vers "Enregistrer sous..."
             */
            this.sauvegarderFichierSous();
            
        } else {            
            this.sauvegarderFichier();
        }
        
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener
     *                  #actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        if (source == this.enregistrerSous || (source == this.enregistrer 
                && this.fichierOuvert == null)) {
            /*
             * Si l'utilisateur souhaite choisir le chemin de sauvegarde ou
             * s'il n'y a pas eu d'enregistrement au pr�alable, on r�oriente
             * vers "Enregistrer sous..."
             */
            this.sauvegarderFichierSous();
            
        } else if (source == this.enregistrer) {
            // && this.fichierEnregistrement != null
            
            this.sauvegarderFichier();
            
        } else if (source == this.ouvrir) {
            
            this.ouvirFichier();
            
        } else if (source == this.nouveau) {
            
            if (JOptionPane.showConfirmDialog(this.laFenetre, "<html><center>"
                    + "Souhaitez-vous r�initialiser le tableur ?<br/>Les "
                    + "donn�es non sauvegard�es seront d�finitivement perdus."
                    + "</center</html>",
                    "Nouveau tableur", JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                this.laFenetre.setTitle("Mini-Calculatrice");
                this.leTableur.reinitialisationTableur();
                this.laFenetre.getLeTableur().getGrilleTableur()
                                    .reinitialisationInterfaceTableur();
            }
            
        } else if (source == this.quitter) {
            if (JOptionPane.showConfirmDialog(this.laFenetre, "<html><center>"
                    + "Souhaitez-vous vraiment quitter ?<br/>Les "
                    + "donn�es non sauvegard�es seront d�finitivement perdus."
                    + "</center</html>",
                    "Nouveau tableur", JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        }
    }

    /**
     * Mutateur de leTableur
     * @param leTableur nouveau leTableur
     */
    public void setLeTableur(Tableur leTableur) {
        this.leTableur = leTableur;
    }
    
}