package sandIOmega;

/**
 * Created by Alpha.DIALLO on 21/03/2016.
 */
public class Variable implements Comparable<Variable>{
    protected Litteral positive;
    protected Litteral negative;
    protected int total;

    public Variable(Litteral positive, Litteral negative) {
        this.positive = positive;
        this.negative = negative;
        this.total = positive.getNbOccurence() + negative.getNbOccurence();
    }

    public Variable(Variable variable) {
        this.positive = variable.getPositive();
        this.negative = variable.getNegative();
        this.total = positive.getNbOccurence() + negative.getNbOccurence();
    }

    public int getPowerfulState(){
        return (positive.getNbOccurence() < negative.getNbOccurence())?-1:1;
    }

    public Variable() {
        this.positive = new Litteral(0);
        this.negative = new Litteral(0);
        this.total = 0;
    }

    @Override
    public Variable clone() {
        return new Variable(getPositive().clone(),getNegative().clone());
    }

    public Litteral getPositive() {
        this.total = positive.getNbOccurence() + negative.getNbOccurence();
        return positive;
    }

    public void setPositive(Litteral positive) {
        this.positive = positive;
        this.total = positive.getNbOccurence() + negative.getNbOccurence();
    }

    public Litteral getNegative() {
        this.total = positive.getNbOccurence() + negative.getNbOccurence();
        return negative;
    }

    public void setNegative(Litteral negative) {
        this.negative = negative;
        this.total = positive.getNbOccurence() + negative.getNbOccurence();
    }

    public int getTotal() {
        this.total = positive.getNbOccurence() + negative.getNbOccurence();
        return total;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Variable)) return false;

        Variable variable = (Variable) o;

        if (total != variable.total) return false;
        if (!positive.equals(variable.positive)) return false;
        return negative.equals(variable.negative);

    }

    @Override
    public int hashCode() {
        int result = positive.hashCode();
        result = 31 * result + negative.hashCode();
        return result;
    }

    @Override
    public String toString() {
        this.total = positive.getNbOccurence() + negative.getNbOccurence();
        return "Variable{" +
                "total=" + total +
                ", positive=" + positive +
                ", negative=" + negative +
                "} \n";
    }

    @Override
    public int compareTo(Variable o) {
//        return o.getTotal() > this.getTotal()? -1 : o.getTotal() < this.getTotal()? 1 :
//                o.getPositive().getNbOccurence() > this.getPositive().getNbOccurence()? -1: 1;
//        if(o == null || o.getTotal() == 0 || this.total == 0) return 0;
//        return  (o.getPositive().getNbOccurence()==0 || o.getNegative().getNbOccurence() == 0)?-1:
//                (this.getPositive().getNbOccurence()==0 || this.getNegative().getNbOccurence() == 0)?1:
//                        o.getTotal() > this.getTotal()? -1 : o.getTotal() < this.getTotal()? 1 :
//                                (o.getTotal() == this.getTotal())? o.getPositive().getNbOccurence() > this.getPositive().getNbOccurence()? -1:
//                                        o.getPositive().getNbOccurence() < this.getPositive().getNbOccurence()?1:0:0;
        if(o == null || o.getTotal() == 0 || this.total == 0) return 0;
        if(o.getTotal() == this.total){
            if(o.getPositive().getNbOccurence()==0 || o.getNegative().getNbOccurence() == 0) return -1;
            if(this.getPositive().getNbOccurence()==0 || this.getNegative().getNbOccurence() == 0) return 1;

        }
        if(o.getPositive().getNbOccurence() > this.getPositive().getNbOccurence()) return -1;
        if(o.getPositive().getNbOccurence() < this.getPositive().getNbOccurence()) return 1;
        return 0;
    }
}
