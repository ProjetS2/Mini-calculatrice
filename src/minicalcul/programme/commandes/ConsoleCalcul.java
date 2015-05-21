/* 
 * ConsoleCalcul.java                            17 avr. 2015
 * IUT info1 Groupe 3 2014-2015
 */
package minicalcul.programme.commandes;

/**
 * Interface r�unissant les m�thodes de calcul qui pourront �tre utilis�es pour
 * la calculatrice ou le tableur.
 * @author Cl�ment Zeghmati
 * @version 0.1
 */
public interface ConsoleCalcul {
           
    /**
     * V�rifie que la syntaxe des sous-chaines est correcte
     * @return true si la syntaxe est correcte, false sinon
     */
    public boolean verificationSyntaxe();

    /**
     * V�rifie le format de la chaine selon le tableau de v�rit� apr�s avoir
     * v�rifi� s'il y a autant de parenth�ses ouvrante que de fermante
     * @return true si le format est bon, false sinon
     */
    public boolean verificationFormat();

    /**
     * V�rifie si une op�ration � une affectation correcte
     * @return true si l'affectation est correcte
     */
    public boolean verificationAffectation();
    
    /**
     * Restaure et remplace les noms de zones m�moire (ou cellules) dans
     * l'expression par la valeur qu'elle contient
     * @return true si la restauration s'est bien pass�, false si on tente de
     *          restaurer une sauvegarde qui ne contient aucune valeur
     */
    public boolean restaurationSauvegarde();

    /**
     * V�rifie si l'affectation d'un nombre dans une zone m�moire ou cellule
     * est correcte. On suppose qu'il n'y a qu'un seul = dans l'expression. 
     * Ensuite on garde la zone affect�.
     * @return true si l'affectation est possible, false sinon
     */
    public boolean affectationCorrecte();

    /**
     * Calcule l'expression de cette instruction lorsqu'elle poss�de des
     * parenth�ses
     */
    public void calculExpressionParenthese();

    /**
     * Calcule l'expression simple (sans parenth�se) d�limit�e par le chiffre du
     * d�but, et le chiffre de la fin et retourne le r�sultat dans une chaine.
     * @param debut D�but de l'expression
     * @param fin Fin de l'expression
     */
    public void calculExpressionSimple(int debut, int fin);

    /**
     * Effectue l'op�ration pass� en argument aux op�rantes situ�es avant
     * et apr�s la position de l'op�rateur
     * @param operation Op�ration � effectuer
     *          - 0 : Division
     *          - 1 : Multiplication
     *          - 2 : Addition
     *          - 3 : Soustraction
     * @param position Position de l'op�rateur
     */
    public void operateurOperationOperateur(int operation, int position);
    
    /**
     * D�callage de deux cases apr�s avoir effectu� une op�ration
     * @param position Position � partir de laquelle on commence le d�callage
     */
    public void decallage(int position);
   
    /**
     * Recherche le nombre de = dans une expression
     * @return le nombre de = dans une expression
     */
    public int nombreEgaleExpression();

    /**
     * Recherche la zone m�moire (ou cellule) � laquelle le r�sultat devra �tre 
     * affect�
     * @return Indice de la zone m�moire � laquelle le r�sultat 
     *         devra �tre affect�
     */
    public int caseAAffecter();
    
    /**
     * Longueur actuelle de l'instruction
     * @return longueur de l'instruction
     */
    public int longueurInstruction();
}
