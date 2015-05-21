/* 
 * ConsoleGestionMemoire.java                            14 avr. 2015
 * IUT info1 Groupe 3 2014-2015
 */
package minicalcul.programme.commandes;

import java.util.regex.Pattern;

import minicalcul.fenetre.FenetrePrincipale;

/**
 * Objet de gestion de la m�moire
 * @author Cl�ment Zeghmati
 * @version 0.1
 */
public class ConsoleGestionMemoire extends Console {
    
    /** R�sum� des commandes de la gestion de la m�moire */
    private static final String AIDE = 
            "--------------- AIDE DU GESTIONNARE DE MEMOIRES --------------- \n"
            + " - RAZ [Zone ou plage m�moire]\n"
            + "\tRemet � 0 les plages m�moires sp�cifi�es ou toutes s'il n'y a "
            + "\n\tpas d'arguments.\n\n"
            + " - INCR [Zone ou plage m�moire]\n"
            + "\tAjoute 1 aux zones m�moires sp�cifi�es ou � toutes s'il n'y a "
            + "\n\tpas d'arguments.\n\n"
            + " - SOM [Zone ou plage m�moire]\n"
            + "\tAffiche la somme des zones m�moires sp�cifi�es ou de toutes "
            + "\n\ts'il n'y a pas d'arguments.\n\n"
            + " - PROD [Zone ou plage m�moire]\n"
            + "\tAffiche le produit des zones m�moires sp�cifi�es ou de "
            + "toutes \n\ts'il n'y a pas d'arguments.\n\n"
            + " - MOY [Zone ou plage m�moire]\n"
            + "\tAffiche la moyenne des zones m�moires sp�cifi�es ou toutes "
            + "\n\ts'il n'y a pas d'arguments.\n\n"
            + " - SQRT [Zone ou plage m�moire]\n"
            + "\tRemplace la valeur des cases m�moires sp�cifi�es ou toutes "
            + "\n\ts'il n'y a pas d'arguments, par leurs racines carr�es "
            + "\n\trespectives.\n\n"
            + " - CAR [Zone ou plage m�moire]\n"
            + "\tRemplace la valeur des cases m�moires sp�cifi�es ou toutes "
            + "\n\ts'il n'y a pas d'arguments, par leurs carr�es "
            + "respectifs.\n\n"
            + " - INIT {Zone ou plage m�moire} {valeur}\n"
            + "\tInitialise les zones m�moires sp�cifi�es � la valeur "
            + "\n\tsp�cifi�e.\n\n"
            + " - ADD {Zone ou plage m�moire} {valeur}\n"
            + "\tAjoute � la valeur des zones m�moires sp�cifi�es la valeur "
            + "\n\tsp�cifi�e.\n\n"    
            + " - MUL {Zone ou plage m�moire} {valeur}\n"
            + "\tMultiplie la valeur des zones m�moires sp�cifi�es par la "
            + "\n\tvaleur sp�cifi�e.\n\n"
            + " - EXP {Zone ou plage m�moire} {valeur}\n"
            + "\tEl�ve la valeur des zones m�moires sp�cifi�es � la puissance "
            + "\n\tsp�cifi�e.\n\n"
            + "[ ] : OPTIONNEL / { } : OBLIGATOIRE\n";
    
    /** 
     * Plage des zones m�moires consern�es par l'op�ration envoy�e sous 
     * forme d'indices (A=0..Z=25)
     */
    private int[] plageMemoire;

    /**
     * Constructeur de la console de gestionnaire de la m�moire
     * @param laFenetre R�f�rence permettant d'acc�der � la console et aux
     *                           zones m�moires
     */
    public ConsoleGestionMemoire(FenetrePrincipale laFenetre) {
        this.setLaFenetre(laFenetre);
        // Nombre de chiffre apres la virgule
        this.getDf().setMaximumFractionDigits(5); 
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.Console
     *                  #traitementCommande(java.lang.String)
     */
    @Override
    public String traitementCommande(String commande) {
        
        // On commence par r�initialiser l'�tat de cette console
        this.reinitialisation();

        // On d�coupe la chaine avec les espaces
        this.setCommande(commande);
        this.setInstructions(commande.split(" "));

        // On regarde quelle instruction est demand�e
        switch (this.getInstructions()[0].toString()) {
        case "RAZ" : // Remise � z�ro
            this.raz();
            break;
        case "INCR" : // Incr�mentation
            this.incremente();
            break;
        case "SOM" : // Somme des cases et retour r�sultat
            this.somme();
            break;
        case "PROD" : // Produit des cases et retour r�sultat
            this.produit();
            break;
        case "MOY" : // Moyenne des cases et retour r�sultat
            this.moyenne();
            break;
        case "SQRT" : // Racine carr�
            this.racineCarre();
            break;
        case "CAR" : // Carr�
            this.carre();
            break;
        case "INIT" : // Initialisation zone m�moire
            this.initialisation();
            break;
        case "ADD" : // Ajoute valeur � la zone
            this.addition();
            break;
        case "MUL" : // Multiplie la valeur par la zone
            this.multiplication();
            break;
        case "EXP" : // Eleve la valeur de la zone par l'exposant
            this.exposant();
            break;
        case "AIDE" : // Aide
            this.setaRetourner(AIDE);
            break;
        default :
            this.setaRetourner("  ^\nErreur de syntaxe : la commande \""
                    + this.getInstructions()[0] + "\" n'existe pas.");
        }
        
        return this.getaRetourner();
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.Console#reinitialisation()
     */
    @Override
    public void reinitialisation() {
        this.setCommande(null);
        this.setInstructions(null);
        this.setErreurTrouvee(false);
        this.setaRetourner(null);
        this.setLieuMauvaisArgument(-1);
        this.plageMemoire = null;        
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.Console#rechercheErreur(int)
     */
    @Override
    public void rechercheErreur(int typeErreur) {
        int posErreur = 0;
        this.setErreurTrouvee(true); // Erreur d�clench�e
        StringBuilder tmpARetourner = new StringBuilder("  ");

        // On recherche la position de l'erreur dans la chaine originale
        for (int i = 0; i < this.getCommande().length()
                && posErreur < this.getLieuMauvaisArgument(); i++) {
            if (this.getCommande().charAt(i) == ' ') {
                posErreur++;
            }
            tmpARetourner.append(" ");
        }

        switch (typeErreur) {
        case ERREUR_NB_ARGUMENTS :
            this.setaRetourner(tmpARetourner.append("^\nErreur d'arguments : "
                    + "la commande " + this.getInstructions()[0] + " prend "                  
                    + nbArgumentsCommande() + " argument(s)").toString());
            break;
        case ERREUR_PLAGE_MEMOIRES :
            this.setaRetourner(tmpARetourner.append(
                    "^\nErreur de syntaxe : \"" 
             + this.getInstructions()[this.getLieuMauvaisArgument()]).toString()
             + "\" n'est pas une zone m�moire ou une plage de zones m�moire.");
            break;
        case ERREUR_ORDRE_PLAGE_MEMOIRES :
            this.setaRetourner(tmpARetourner.append(
                    "^\nErreur de syntaxe : les bornes de la plage m�moire "
                            + "doivent �tre rang�es dans l'ordre.").toString());
            break;
        case ERREUR_VALEUR_ARGUMENT :
            this.setaRetourner(tmpARetourner.append(
                    "^\nErreur d'arguments : le deuxi�me argument "
                            + "doit �tre un r�el.").toString());
            break;
        }        
    }

    /**
     * Remet � 0 les cases sp�cifi�es en argument ou toutes si aucun argument
     * n'est sp�cifi�
     */
    private void raz() {
        // On v�rifie d'abord qu'il n'y ait qu'un seul argument ou 0
        if (!verificationNombreArgument("RAZ")) {
            return;
        } // else le nombre d'argument est bon

        // On contr�le s'il n'y a qu'une zone m�moire ou une plage
        this.controlePlageMemoire();        
        
        // On applique la remise � z�ro si l'argument de RAZ est correct
        if (this.plageMemoire != null) {
            for (int i = this.plageMemoire[0]; i <= this.plageMemoire[1]; i++) {
                this.getLaFenetre().getLaMemoire().affectationMemoire(i, "0");
            }
            
            // Toutes les zones m�moires
            if (this.getInstructions().length == 1) {
                // Toutes les zones m�moires
                this.setaRetourner("Toutes les zones m�moires ont �t� remises" 
                        + " � z�ro.");
                
            } else if (this.plageMemoire[0] != this.plageMemoire[1]) {
                // Plage de zones m�moire
                this.setaRetourner("Les zones m�moires de " 
                        + this.getInstructions()[1].charAt(0) + " � "
                        + this.getInstructions()[1].charAt(3)
                        + " ont �t� remises � z�ro.");
            } else {
                // Une seule zone m�moire
                this.setaRetourner(this.getInstructions()[1].charAt(0)
                        + " a �t� remis � z�ro.");
            }
        }
    }

    /**
     * Incr�mente de 1 les cases sp�cifi�es en argument ou toutes si la commande
     * est saisie sans argument
     */
    private void incremente() {
        // On v�rifie d'abord qu'il n'y ait qu'un seul argument ou 0
        if (!verificationNombreArgument("INCR")) {
            return;
        } // else le nombre d'argument est bon
        
        // On contr�le s'il n'y a qu'une zone m�moire ou une plage
        this.controlePlageMemoire();    
        
        /*
         * On incr�mente seulement les cases initialis�es. Donc on garde une
         * trace des actions effectu�es que l'on affichera sur la console.
         */
        StringBuilder aRetourner = new StringBuilder("");
        
        // On applique l'incr�mentation si l'argument de INCR est correct
        if (this.plageMemoire != null) {
            for (int i = this.plageMemoire[0]; i <= this.plageMemoire[1]; i++) {
                // On garde la lettre de la zone (A = 65)
                String tmpZone = new Character((char) (65 + i)).toString();
                
                // On v�rifie que la zone m�moire est initialis�e
                if (estInitialisee(tmpZone)) {
                    
                    // On prend le nouveau r�sultat
                    String resultat = Double.toString(Double.parseDouble(
                            this.contenuZoneMemoire(i)) + 1);
                    
                    // Comme c'est un double, on regarde si on peut enlev� la ,
                    this.getLaFenetre().getLaMemoire().affectationMemoire(i,
                           (estUnEntier(resultat) ? Integer.toString(
                              (int) Double.parseDouble(resultat)) : resultat));
                            
                    aRetourner.append(tmpZone + " a �t� incr�ment�e.\n");
                } else {
                    aRetourner.append(tmpZone + " n'a pu �tre incr�ment�e"
                            + " car elle n'est pas initialis�e.\n");
                }
            }
            this.setaRetourner(aRetourner.toString());
        }
    }

    /**
     * Affiche la somme des contenus des zones m�moires sp�cifi�es en argument
     * ou toutes les zones m�moires si la commande est saisie sans argument
     */
    private void somme() {
        // On v�rifie d'abord qu'il n'y ait qu'un seul argument ou 0
        if (!verificationNombreArgument("SOM")) {
            return;
        } // else le nombre d'argument est bon
        
        // On contr�le s'il n'y a qu'une zone m�moire ou une plage
        this.controlePlageMemoire();
        
        double somme = 0; // Somme a retourner
        
        // On fait la somme si l'argument de SOM est bon ou inexistant
        if (this.plageMemoire != null) { 
            for (int i = this.plageMemoire[0]; i <= this.plageMemoire[1]; i++) {
                // On garde la lettre de la zone (A = 65)
                String tmpZone = new Character((char) (65 + i)).toString();
                
                // On v�rifie que la zone m�moire est initialis�e
                if (estInitialisee(tmpZone)) {
                    somme += Double.parseDouble(this.contenuZoneMemoire(i));
                }
            }
            
            // Comme c'est un double, on regarde si on peut enlev� la virgule
            this.setaRetourner("= " + (estUnEntier(Double.toString(somme)) ? 
              Integer.toString((int) Double.parseDouble(Double.toString(somme)))
                               : Double.toString(somme)));
        }
        
    }

    /**
     * Affiche le produit des contenus des zones m�moires sp�cifi�es en argument
     * ou toutes les zones m�moires si la commande est saisie sans argument
     */
    private void produit() {
        
        // On v�rifie d'abord qu'il n'y ait qu'un seul argument ou 0
        if (!verificationNombreArgument("PROD")) {
            return;
        } // else le nombre d'argument est bon
        
        // On contr�le s'il n'y a qu'une zone m�moire ou une plage
        this.controlePlageMemoire();
        
        double produit = 1; // Produit a retourner
        boolean initialise = false; // = true si une zone est intialis�e
        
        // On fait le produit si l'argument de PROD est bon ou inexistant
        if (this.plageMemoire != null) { 
            for (int i = this.plageMemoire[0]; i <= this.plageMemoire[1]; i++) {
                // On garde la lettre de la zone (A = 65)
                String tmpZone = new Character((char) (65 + i)).toString();
                
                // On v�rifie que la zone m�moire est initialis�e
                if (estInitialisee(tmpZone)) {
                    produit *= Double.parseDouble(this.contenuZoneMemoire(i));
                    initialise = true;
                }
            }
            
            if (initialise) {
                // Comme c'est un double, on regarde si on peut enlev� la ,
                this.setaRetourner("= " + (estUnEntier(Double.toString(produit)) 
                        ? Integer.toString((int) Double.parseDouble(
                                Double.toString(produit))) 
                                : Double.toString(produit)));
            } else {
                // Aucune Zone n'est initialis�e
                this.setaRetourner("     ^\nErreur de calcul : Aucune zone"
                        + " n'est initialis�e.");
            }
        }
    }

    /**
     * Affiche la moyenne des contenus des zones m�moires sp�cifi�es en argument
     * ou toutes les zones m�moires si la commande est saisie sans argument
     */
    private void moyenne() {
        // On v�rifie d'abord qu'il n'y ait qu'un seul argument ou 0
        if (!verificationNombreArgument("MOY")) {
            return;
        } // else le nombre d'argument est bon
        
        // On contr�le s'il n'y a qu'une zone m�moire ou une plage
        this.controlePlageMemoire();
        
        double moyenne = 0; // Moyenne a retourner
        int nbNombre = 0;   // Compteur de nombres
        
        // On fait le produit si l'argument de MOY est bon ou inexistant
        if (this.plageMemoire != null) { 
            for (int i = this.plageMemoire[0]; i <= this.plageMemoire[1]; i++) {
                // On garde la lettre de la zone (A = 65)
                String tmpZone = new Character((char) (65 + i)).toString();
                
                // On v�rifie que la zone m�moire est initialis�e
                if (estInitialisee(tmpZone)) {
                    moyenne += Double.parseDouble(this.contenuZoneMemoire(i));
                    nbNombre++;
                }
            }
            
            if (nbNombre != 0) {
                moyenne /= nbNombre;
                // Comme c'est un double, on regarde si on peut enlev� la ,
                this.setaRetourner("= " + (estUnEntier(Double.toString(moyenne)) 
                        ? Integer.toString((int) Double.parseDouble(
                                Double.toString(moyenne))) 
                                : Double.toString(moyenne)));
            } else {
                // Aucune Zone n'est initialis�e
                this.setaRetourner("     ^\nErreur de calcul : Aucune zone"
                        + " n'est initialis�e.");
            }
        }
    }

    /**
     * Calcule la racine carr�e des contenus des zones m�moires sp�cifi�es en 
     * argument ou toutes les zones m�moires si la commande est saisie sans 
     * argument. Le r�sultat �crase le contenu de la zone m�moire si le calcul
     * est possible. Sinon il ne fait rien.
     */
    private void racineCarre() {
        // On v�rifie d'abord qu'il n'y ait qu'un seul argument ou 0
        if (!verificationNombreArgument("SQRT")) {
            return;
        } // else le nombre d'argument est bon
        
        // On contr�le s'il n'y a qu'une zone m�moire ou une plage
        this.controlePlageMemoire();    
        
        /*
         * On remplace le contenu par la racine carr� seulement si les cases
         * sont initialis�es et si le contenu est positif. Donc on garde une
         * trace des actions effectu�es que l'on affichera sur la console.
         */
        StringBuilder aRetourner = new StringBuilder("");
        
        // On applique la racine carr� si l'argument de SQRT est correct
        if (this.plageMemoire != null) {
            for (int i = this.plageMemoire[0]; i <= this.plageMemoire[1]; i++) {
                // On garde la lettre de la zone (A = 65)
                String tmpZone = new Character((char) (65 + i)).toString();
                
                // On v�rifie que la zone m�moire est initialis�e et positive
                if (estInitialisee(tmpZone) && estPositif(tmpZone)) {
                    // On prend le contenu
                    double contenu = Double.parseDouble(
                            this.contenuZoneMemoire(i));
                    
                    // On prend le resultat qu'on arrondi
                    String resultat = Double.toString((double) Math.round(
                            Math.sqrt(contenu) * 10000) / 10000);
                    
                    // Comme c'est un double, on regarde si on peut enlev� la ,
                    this.getLaFenetre().getLaMemoire().affectationMemoire(i,
                           (estUnEntier(resultat) ? Integer.toString(
                              (int) Double.parseDouble(resultat)) : resultat));
                            
                    aRetourner.append("Racine de " + tmpZone + " = "
                            + resultat + "\n");
                } else {
                    aRetourner.append(tmpZone + " n'est pas initialis�e"
                            + " ou son contenu est n�gatif.\n");
                }
            }
            this.setaRetourner(aRetourner.toString());
        }
    }

    /**
     * Calcule le carr� des contenus des zones m�moires sp�cifi�es en argument
     * ou toutes les zones m�moires si la commande est saisie sans 
     * argument. Le r�sultat �crase le contenu de la zone m�moire. 
     */
    private void carre() {
        // On v�rifie d'abord qu'il n'y ait qu'un seul argument ou 0
        if (!verificationNombreArgument("CAR")) {
            return;
        } // else le nombre d'argument est bon
        
        // On contr�le s'il n'y a qu'une zone m�moire ou une plage
        this.controlePlageMemoire();
        
        /*
         * On remplace le contenu par la racine carr� seulement si les cases
         * sont initialis�es. Donc on garde une trace des actions effectu�es
         * que l'on affichera sur la console.
         */
        StringBuilder aRetourner = new StringBuilder("");
        
        // On applique le carr� si l'argument de CAR est correct
        if (this.plageMemoire != null) {
            for (int i = this.plageMemoire[0]; i <= this.plageMemoire[1]; i++) {
                // On garde la lettre de la zone (A = 65)
                String tmpZone = new Character((char) (65 + i)).toString();
                
                // On v�rifie que la zone m�moire est initialis�e
                if (estInitialisee(tmpZone)) {                    
                    // On prend le nouveau r�sultat
                    String resultat = Double.toString(Double.parseDouble(
                            this.contenuZoneMemoire(i))
                            * Double.parseDouble(this.contenuZoneMemoire(i)));
                    
                    // Comme c'est un double, on regarde si on peut enlev� la ,
                    this.getLaFenetre().getLaMemoire().affectationMemoire(i,
                           (estUnEntier(resultat) ? Integer.toString(
                              (int) Double.parseDouble(resultat)) : resultat));
                            
                    aRetourner.append("Carr� de " + tmpZone + " = "
                            + resultat + "\n");
                } else {
                    aRetourner.append(tmpZone + " n'est pas initialis�e.\n");
                }
            }
            this.setaRetourner(aRetourner.toString());
        }
    }

    /**
     * Initialise les zones m�moires sp�cifi�es en premier argument avec la
     * valeur sp�cifi�e en second argument.
     */
    public void initialisation() {
        // On v�rifie d'abord qu'il y ait 2 arguments
        if (!verificationNombreArgument("INIT")) {
            return;
        } // else le nombre d'argument est bon
        
        // On contr�le s'il n'y a qu'une zone m�moire ou une plage
        this.controlePlageMemoire();
        
        /*
         * On r�cup�re le deuxi�me argument qui sera le contenu de la zone
         * s'il s'agit bien d'un double
         */
        String valeur = null; // Initialisation bidon
        if (estUnDouble(this.getInstructions()[2])) {
            // On contr�le si on peut enlev� la virgule s'il y en a une
            valeur = estUnEntier(this.getInstructions()[2]) ? Integer.toString(
                    (int) Double.parseDouble(this.getInstructions()[2])) 
                    : this.getInstructions()[2];
                    
        } else {
            // La valeur est mauvaise (2�me argument)
            this.setLieuMauvaisArgument(2);
            this.rechercheErreur(ERREUR_VALEUR_ARGUMENT);
            return;
        }
        
        // On garde une trace sur la console
        StringBuilder aRetourner = new StringBuilder("");
        
        // On applique l'initialisation si la plage est correcte
        if (this.plageMemoire != null) {
            for (int i = this.plageMemoire[0]; i <= this.plageMemoire[1]; i++) {
                // On garde la lettre de la zone (A = 65)
                String tmpZone = new Character((char) (65 + i)).toString();

                // On affecte la valeur
                this.getLaFenetre().getLaMemoire().affectationMemoire(i,valeur);

                aRetourner.append(tmpZone + " a �t� inintialis�e � "
                        + valeur + ".\n");
            }
            this.setaRetourner(aRetourner.toString());
        }
    }

    /**
     * Ajoute aux zones m�moires sp�cifi�es en premier argument la valeur
     * sp�cifi�e en second argument.    
     */
    private void addition() {
        // On v�rifie d'abord qu'il y ait 2 arguments
        if (!verificationNombreArgument("ADD")) {
            return;
        } // else le nombre d'argument est bon
        
        // On contr�le s'il n'y a qu'une zone m�moire ou une plage
        this.controlePlageMemoire();
        
        /*
         * On r�cup�re le deuxi�me argument qui sera ajout� au contenu de la 
         * zone s'il s'agit bien d'un double
         */
        String valeur = null; // Initialisation bidon
        if (estUnDouble(this.getInstructions()[2])) {
            // On contr�le si on peut enlev� la virgule s'il y en a une
            valeur = estUnEntier(this.getInstructions()[2]) ? Integer.toString(
                    (int) Double.parseDouble(this.getInstructions()[2])) 
                    : this.getInstructions()[2];
                    
        } else {
            // La valeur est mauvaise (2�me argument)
            this.setLieuMauvaisArgument(2);
            this.rechercheErreur(ERREUR_VALEUR_ARGUMENT);
            return;
        }
        
        // On garde une trace sur la console
        StringBuilder aRetourner = new StringBuilder("");
        
        // On applique l'initialisation si la plage est correcte
        if (this.plageMemoire != null) {
            for (int i = this.plageMemoire[0]; i <= this.plageMemoire[1]; i++) {
                // On garde la lettre de la zone (A = 65)
                String tmpZone = new Character((char) (65 + i)).toString();
                
                // On v�rifie que la zone m�moire est initialis�e
                if (estInitialisee(tmpZone)) {
                    // On garde le r�sultat
                    String resultat = Double.toString(
                            Double.parseDouble(this.contenuZoneMemoire(i))
                            + Double.parseDouble(valeur));
                    
                    // Comme c'est un double, on regarde si on peut enlev� la ,
                    this.getLaFenetre().getLaMemoire().affectationMemoire(i,
                            (estUnEntier(resultat) ? Integer.toString(
                            (int) Double.parseDouble(resultat)) : resultat));
                    
                    aRetourner.append(valeur + " a �t� ajout� � "
                            + tmpZone + ".\n");
                    
                } else {
                    // La zone m�moire n'est pas initialis�e
                    aRetourner.append(tmpZone + " n'a pu �tre modifi�e"
                            + " car elle n'est pas initialis�e.\n");
                }
            }
            this.setaRetourner(aRetourner.toString());
        }
    }

    /**
     * Multiplie les zones m�moires sp�cifi�es en premier argument la valeur
     * sp�cifi�e en second argument.    
     */
    private void multiplication() {
        // On v�rifie d'abord qu'il y ait 2 arguments
        if (!verificationNombreArgument("MUL")) {
            return;
        } // else le nombre d'argument est bon
        
        // On contr�le s'il n'y a qu'une zone m�moire ou une plage
        this.controlePlageMemoire();
        
        /*
         * On r�cup�re le deuxi�me argument qui sera multiplierpar le contenu de
         * la zone s'il s'agit bien d'un double
         */
        String valeur = null; // Initialisation bidon
        if (estUnDouble(this.getInstructions()[2])) {
            // On contr�le si on peut enlev� la virgule s'il y en a une
            valeur = estUnEntier(this.getInstructions()[2]) ? Integer.toString(
                    (int) Double.parseDouble(this.getInstructions()[2])) 
                    : this.getInstructions()[2];
                    
        } else {
            // La valeur est mauvaise (2�me argument)
            this.setLieuMauvaisArgument(2);
            this.rechercheErreur(ERREUR_VALEUR_ARGUMENT);
            return;
        }
        
        // On garde une trace sur la console
        StringBuilder aRetourner = new StringBuilder("");
        
        // On applique l'initialisation si la plage est correcte
        if (this.plageMemoire != null) {
            for (int i = this.plageMemoire[0]; i <= this.plageMemoire[1]; i++) {
                // On garde la lettre de la zone (A = 65)
                String tmpZone = new Character((char) (65 + i)).toString();
                
                // On v�rifie que la zone m�moire est initialis�e
                if (estInitialisee(tmpZone)) {
                    // On garde le r�sultat
                    String resultat = Double.toString(
                            Double.parseDouble(this.contenuZoneMemoire(i))
                            * Double.parseDouble(valeur));
                    
                    // Comme c'est un double, on regarde si on peut enlev� la ,
                    this.getLaFenetre().getLaMemoire().affectationMemoire(i,
                            (estUnEntier(resultat) ? Integer.toString(
                            (int) Double.parseDouble(resultat)) : resultat));
                    
                    aRetourner.append(tmpZone + " a �t� multipli�e par "
                            + valeur + ".\n");
                    
                } else {
                    // La zone m�moire n'est pas initialis�e
                    aRetourner.append(tmpZone + " n'a pu �tre modifi�e"
                            + " car elle n'est pas initialis�e.\n");
                }
            }
            this.setaRetourner(aRetourner.toString());
        }
    }

    /**
     * Eleve le contenu des zones m�moires sp�cifi�es en premier argument � la
     * puissance sp�cifi�e en second argument. 
     */
    private void exposant() {
        // On v�rifie d'abord qu'il y ait 2 arguments
        if (!verificationNombreArgument("EXP")) {
            return;
        } // else le nombre d'argument est bon
        
        // On contr�le s'il n'y a qu'une zone m�moire ou une plage
        this.controlePlageMemoire();
        
        /*
         * On r�cup�re le deuxi�me argument qui sera l'exposant du contenu de
         * la zone s'il s'agit bien d'un double
         */
        String valeur = null; // Initialisation bidon
        if (estUnDouble(this.getInstructions()[2])) {
            // On contr�le si on peut enlev� la virgule s'il y en a une
            valeur = estUnEntier(this.getInstructions()[2]) ? Integer.toString(
                    (int) Double.parseDouble(this.getInstructions()[2])) 
                    : this.getInstructions()[2];
                    
        } else {
            // La valeur est mauvaise (2�me argument)
            this.setLieuMauvaisArgument(2);
            this.rechercheErreur(ERREUR_VALEUR_ARGUMENT);
            return;
        }
        
        // On garde une trace sur la console
        StringBuilder aRetourner = new StringBuilder("");
        
        // On applique l'initialisation si la plage est correcte
        if (this.plageMemoire != null) {
            for (int i = this.plageMemoire[0]; i <= this.plageMemoire[1]; i++) {
                // On garde la lettre de la zone (A = 65)
                String tmpZone = new Character((char) (65 + i)).toString();
                
                // On v�rifie que la zone m�moire est initialis�e
                if (estInitialisee(tmpZone)) {
                    // On garde le r�sultat
                    String resultat = Double.toString(Math.pow(
                            Double.parseDouble(this.contenuZoneMemoire(i)),
                            Double.parseDouble(valeur)));
                    
                    // Comme c'est un double, on regarde si on peut enlev� la ,
                    this.getLaFenetre().getLaMemoire().affectationMemoire(i,
                            (estUnEntier(resultat) ? Integer.toString(
                            (int) Double.parseDouble(resultat)) : resultat));
                    
                    aRetourner.append(tmpZone + " a �t� �lev�e � la puissance  "
                            + valeur + ".\n");
                    
                } else {
                    // La zone m�moire n'est pas initialis�e
                    aRetourner.append(tmpZone + " n'a pu �tre modifi�e"
                            + " car elle n'est pas initialis�e.\n");
                }
            }
            this.setaRetourner(aRetourner.toString());
        }
    }
    
    /**
     * V�rifie si une commande a le bon nombre d'arguments
     * @param commande Commande � v�rifier
     * @return true si le nombre d'arguments de la commande est bon, false sinon
     */
    public boolean verificationNombreArgument(String commande) {
        if (!argumentsCommandeAttendus(commande)) {
            this.setLieuMauvaisArgument(this.getInstructions().length);
            this.rechercheErreur(ERREUR_NB_ARGUMENTS);
            return false;
        }    
        return true;
    }

    /**
     * Contr�le si le deuxi�me argument d'une commande est valide. C'est-�-dire
     * s'il contient une zone m�moire (lettre majuscule) ou une plage m�moire
     * (ex : A..Z). Si c'est le cas, sauvegarde dans le tableau plageMemoire 
     * sous la forme des indices le d�but et la fin de la plage, sinon d�clenche
     * une erreur
     */
    public void controlePlageMemoire() {
        
        if (this.getInstructions().length == 1) {
            // On l'applique sur toutes les m�moires
            this.plageMemoire = new int[]{0,25};

        } else if (this.getInstructions()[1].length() == 1 && // Une lettre
                estUneMemoire(this.getInstructions()[1])) {
            // La plage commence et fini au m�me endroit
            this.plageMemoire = new int[]{
                    this.getInstructions()[1].charAt(0) - 65,
                    this.getInstructions()[1].charAt(0) - 65};

        } else if (this.getInstructions()[1].length() == 4 &&
                estUnePlageCorrecte(this.getInstructions()[1]) == 0) {
            // 4 comme le nombre de caract�res d'un plage (ex : A..Z)
            this.plageMemoire = new int[]{
                    this.getInstructions()[1].charAt(0) - 65,
                    this.getInstructions()[1].charAt(3) - 65};

        } else { // Erreur dans la plage m�moire
            this.setLieuMauvaisArgument(1);
            if (estUnePlageCorrecte(this.getInstructions()[1]) == 2) {
                this.rechercheErreur(ERREUR_ORDRE_PLAGE_MEMOIRES);
            } else {
                this.rechercheErreur(ERREUR_PLAGE_MEMOIRES);
            }
        }
    }

    /**
     * Regarde si le nombre d'arguments qui est attendu pour la commande pass�e
     * est juste
     * @param commande Commande dont on veut connaitre le nombre d'argument
     * @return true si le nombre d'argument est juste, false sinon
     */
    public boolean argumentsCommandeAttendus(String commande) {
        
        switch (commande.toString()) {
        case "RAZ" :
        case "INCR" :
        case "SOM" :
        case "PROD" :
        case "MOY" :
        case "SQRT" :
        case "CAR" :
            // La commande et la zone
            return this.getInstructions().length <= 2;
        case "INIT" :       
        case "ADD" :
        case "MUL" :
        case "EXP" :
            // La commande, la plage m�moire et la valeur
            return this.getInstructions().length == 3;
        }
        
        throw new IllegalArgumentException("Passage impossible");
    }
    
    /**
     * @return Chaine de caract�res repr�senatnt le nombre d'arguments
     *          attendus pour la commande saisie par l'utilisateur
     */
    public String nbArgumentsCommande() {
        
        switch (this.getInstructions()[0].toString()) {
        case "RAZ" :
        case "INCR" :
        case "SOM" :
        case "PROD" :
        case "MOY" :
        case "SQRT" :
        case "CAR" :
            // La commande et la zone (facultatif)
            return "0 ou 1";
        case "INIT" :       
        case "ADD" :
        case "MUL" :
        case "EXP" :
            // La commande, la plage m�moire et la valeur
            return "2";
        }
        
        throw new IllegalArgumentException("Passage impossible");
    }

    /**
     * Controle si une chaine de 4 caract�res est une plage m�moire avec des
     * bornes dans le bon ordre
     * @param aTester Chaine � tester
     * @return 0 si la chine est bien une plage m�moire
     *         1 si le format n'est pas bon
     *         2 si le format est bon mais les bornes ne sont pas bonnes
     */
    public static int estUnePlageCorrecte(String aTester) {
        
        if (Pattern.compile(REGEX_PLAGE_MEMOIRES).matcher(aTester).matches()) {          
            // Premi�re lettre avant la derni�re
            if (aTester.charAt(0) <= aTester.charAt(3)) {
                return 0;
            } else {
                return 2;
            }
        }
        return 1;
    }
    
    /**
     * Cherche le contenu d'une zone m�moire
     * @param indice Indice de la zone m�moire a rechercher (A = 0.. Z = 25)
     * @return Contenu de la zone m�moire
     */
    private String contenuZoneMemoire(int indice) {
        return this.getLaFenetre().getLaMemoire()
                .getContenuZones()[indice].getText();
    }
    
    /**
     * Teste si le contenu d'une zone m�moire initialis�e est positif
     * @param aTester Chaine � tester qui doit �tre une zone m�moire
     * @return true si la zone m�moire est initialis�e, false sinon
     */
    private boolean estPositif(String aTester) {
        return this.getLaFenetre().getLaMemoire().getContenuZones()
                [aTester.charAt(0) - 65].getText().charAt(0) != '-';
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.Console
     *                  #estUneMemoire(java.lang.String)
     */
    @Override
    public boolean estUneMemoire(String aTester) {
        return Pattern.compile(REGEX_ZONE_MEMOIRE).matcher(aTester).matches();
    }
}