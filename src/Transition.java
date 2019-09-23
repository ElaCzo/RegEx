public class Transition{
    public int charactere;
    public Etat noeud_suivant;
    public boolean estEpsilon;

    public Transition (int charactere, Etat noeud_suivant){
        this.charactere = charactere;
        this.noeud_suivant = noeud_suivant;
        if(charactere == -1) {
            this.estEpsilon = true;
        } else {
            this.estEpsilon = false;
        }
    }

}