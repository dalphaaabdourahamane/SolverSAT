package sandIOmega;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alpha.DIALLO on 21/03/2016.
 */
public class Litteral {
    protected int nbOccurence;
    protected List<Integer> listOccurence;

    public Litteral(int nbOccurence, List listOccurence) {
        this.nbOccurence = nbOccurence;
        this.listOccurence = listOccurence;
    }

    @Override
    protected Litteral clone() {
        return new Litteral(nbOccurence,new ArrayList<>(getListOccurence()));
    }

    public Litteral(int nbOccurence) {
        this.nbOccurence = nbOccurence;
        this.listOccurence = new ArrayList<>();
    }

    public int getNbOccurence() {
        this.nbOccurence = this.listOccurence.size();
        return nbOccurence;
    }

    public void setNbOccurence(int nbOccurence) {
        this.nbOccurence = nbOccurence;
    }

    public void addNbOccurence() {
        this.nbOccurence ++;
    }

    public List<Integer> getListOccurence() {
        this.nbOccurence = this.listOccurence.size();
        return listOccurence;
    }

    public void setListOccurence(List<Integer> listOccurence) {
        this.listOccurence = listOccurence;
    }

    @Override
    public String toString() {
        this.nbOccurence = this.listOccurence.size();
        return "Litteral{" +
                "nbOccurence=" + nbOccurence +
                ", listOccurence=" + listOccurence +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Litteral)) return false;

        Litteral litteral = (Litteral) o;

        if (nbOccurence != litteral.nbOccurence) return false;
        return listOccurence.equals(litteral.listOccurence);

    }

    @Override
    public int hashCode() {
        int result = nbOccurence;
        result = 31 * result + listOccurence.hashCode();
        return result;
    }
}
