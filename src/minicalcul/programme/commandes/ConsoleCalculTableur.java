/* 
 * ConsoleTableur.java                            16 avr. 2015
 * IUT info1 Groupe 3 2014-2015
 */
package minicalcul.programme.commandes;

import java.util.regex.Pattern;

import minicalcul.fenetre.FenetrePrincipale;
import minicalcul.programme.tableur.Tableur;

/**
 * Objet permettant d'int�ragir avec le tableur
 * @author Cl�ment Zeghmati
 * @version 0.1
 */
public class ConsoleCalculTableur extends ConsoleCalculSimple {

    /** R�f�rence au tableur afin de pouvoir int�ragir avec */
    private Tableur leTableur;
    
    /**
     * Constructeur de la console du tableur
     * @param laFenetre R�f�rence permettant d'acc�der � la console et aux
     *                           zones m�moires
     * @param leTableur R�f�rence permettant d'acc�der au tableur
     */
    public ConsoleCalculTableur(FenetrePrincipale laFenetre, 
            Tableur leTableur) {
        super(laFenetre);
        this.leTableur = leTableur;
    }
    
    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.Console
     *                  #traitementCommande(java.lang.String)
     */
    @Override
    public String traitementCommande(String commande) {
        // On commence par r�initialiser l'�tat de cet objet
        this.reinitialisation();

        // On d�coupe la chaine avec les espaces en conservant l'originale
        this.setCommande(commande);
        this.setInstructions(commande.split(" "));
        
        // On v�rifie la syntaxe des sous-chaines 
        if (!this.verificationSyntaxe()) {
            /*
             * On recherche le lieux de l'erreur et on retourne la localisation
             * avec un message
             */
            this.rechercheErreur(ERREUR_SYNTAXE);
        }
                        
        // On v�rifie le format de la chaine s'il n'y a pas eu d'erreur avant.
        if (!this.isErreurTrouvee() && !this.verificationFormat()) {
            this.rechercheErreur(ERREUR_FORMAT);
        }
        
        // Le nombre d'= doit �tre �gal � 1 car il doit y avoir une affectation.
        if (!this.isErreurTrouvee() && !this.verificationAffectation()) {
            this.rechercheErreur(ERREUR_AFFECTATION);
        }
                
        /*
         * S'il n'y a pas eu d'erreur avant et que la restauration s'est mal
         * pass�, on d�clenche une erreur. Si la restauration s'est bien pass�,
         * on peut continuer
         */
        if (!this.isErreurTrouvee() && !this.restaurationSauvegarde()) {
            this.rechercheErreur(ERREUR_INTIALISATION);
        }   
        
        // On peut faire le calcul s'il n'y a pas eu d'erreur avant
        if (!this.isErreurTrouvee()) {

            if (this.getNbParentheses() != 0) {
                this.calculExpressionParenthese();

                /*
                 * Tous les calculs entre parenth�se ont �t� fait et les 
                 * parenth�ses ont �t� enlev�es. Il reste donc une op�ration 
                 * simple que l'on effectue.
                 */
                this.calculExpressionSimple(0, this.longueurInstruction() - 1);

            } else {
                this.calculExpressionSimple(0, this.getInstructions().length-1);           
            }

            // On teste si le r�sultat est un entier pour eviter la virgule
            if (estUnEntier(this.getInstructions()[0])) {
                this.getInstructions()[0] = Integer.toString(
                        (int) Double.parseDouble(this.getInstructions()[0]));
            }

            // On affecte le r�sultat � la cellule
            this.leTableur.affectationValeur(
                    this.getZoneAffecte(), this.getInstructions()[0]);
                     
        }
        
        // On retourne le r�sultat s'il n'y a pas d'erreur
        return !this.isErreurTrouvee() ? (this.getZoneAffecte() + " modifi�e.")
                        : this.getaRetourner();        
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

        // On rajoute le type d'erreur
        if (typeErreur == ERREUR_SYNTAXE) {       
            this.setaRetourner(tmpARetourner.append(
             "^\nErreur de syntaxe : symbole \"" 
             + this.getInstructions()[this.getLieuMauvaisArgument()]).toString()
             + "\" inconnu.");
        } else if (typeErreur == ERREUR_AFFECTATION) {
            this.setaRetourner(tmpARetourner.append(
              "^\nErreur d'affectation : une affectation doit �tre faite.")
              .toString());
        
        } else if (typeErreur == ERREUR_FORMAT) {
            if (this.getNbParentheses() == -1) {
                // Probl�me au niveau des parenth�ses
                this.setaRetourner(tmpARetourner.append("^\nErreur de format :"
                        + " le nombre de parenth�ses ouvrantes et fermantes"
                        + " doit �tre �quivalent.").toString());
            } else {
                // Probl�me avec le tableau de v�rit�
                this.setaRetourner(tmpARetourner.append("^\nErreur de format :"
                        + " symbole innatendu.").toString());
            }
        
        } else if (typeErreur == ERREUR_INTIALISATION) {
            this.setaRetourner(tmpARetourner.append(
             "^\nCalcul impossible : \""
             + this.getInstructions()[this.getLieuMauvaisArgument()]).toString()
             + "\" n'a pas �t� initialis�e.");
        }         
    }
         
    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleCalculSimple
     *                  #verificationAffectation()
     */
    @Override
    public boolean verificationAffectation() {
        // On contr�le qu'il y ait un �gale et que l'affectation soit correcte
        if (!(this.nombreEgaleExpression() == 1 && affectationCorrecte())) {
            this.setLieuMauvaisArgument(this.getInstructions().length - 1);
            return false;
        }
        return true;
    }
    
    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleCalcul
     *                  #restaurationSauvegarde()
     */
    @Override
    public boolean restaurationSauvegarde() {
        
        /*
         * Passe � true s'il y a eu une restauration � faire. Si c'est le cas,
         * on doit le sp�cifier dans la cellule comme quoi il s'agit d'une
         * formule.
         */
        boolean restauration = false;
                
        // On peut s'arr�ter de rechercher lorsqu'on rencontre un =
        for (int i = 0; i < this.getInstructions().length
                && !this.getInstructions()[i].equals("="); i++) {
            
            if (estUneMemoire(this.getInstructions()[i])) {
                
                // S'il s'agit d'une cellule, on v�rifie si elle est initialis�e
                if (this.leTableur.celluleInitialisee(
                        this.getInstructions()[i])) {
                    // On r�cup�re la valeur
                    this.getInstructions()[i] = this.leTableur.
                            restaurationCellule(this.getInstructions()[i]);
                    restauration = true;
                } else { // Erreur : zone non initialis�e
                    this.setLieuMauvaisArgument(i);
                    /*
                     * On remplit la cellule avec un ? afin de le sp�cifier que
                     * l'affectation est impossible. On le signale �galement �
                     * l'utilisateur  
                     */
                    this.leTableur.affectationFormule(
                            this.getZoneAffecte(), this.getCommande());
                    this.leTableur.affectationValeur(
                            this.getZoneAffecte(), "?");
                    return false;  // On sort ici car op�ration impossible
                }
            }
        }
        
        if (restauration) {
            // On sp�cifie que la cellule va contenir une formule
            this.leTableur.affectationFormule(
                    // Cellule
                    this.getInstructions()[this.getInstructions().length - 1],
                    // Formule que l'on copie pour la garder
                    new String(this.getCommande()));
        } else {
            // On sp�cifie que la cellule ne contiendra pas de formule
            this.leTableur.reinitialisationCellule(
                    // Cellule
                    this.getInstructions()[this.getInstructions().length - 1]);
        }
        
        return true;
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleCalculSimple
     *                  #estUneMemoire(java.lang.String)
     */
    @Override
    public boolean estUneMemoire(String aTester) {
        return Pattern.compile(REGEX_CELLULE).matcher(aTester).matches();
    }
    
}