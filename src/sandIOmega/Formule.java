package sandIOmega;

import java.util.*;
import java.util.Map.Entry;

/**
 * Created by SigmaPi & Sandii on 14/04/2016. yes
 * @version 4.3
 * @author Alpha Sandratra
 */
class Formule {
    /**
     *  La formules (ensemble de clauses). Chaque clause est numérotée et contient un list de litteraux
     *  @see Map
     *  @see List
     */
    private Map<Integer,List<Integer>> clauses;
    /**
     *  L'ensemble de variable qui compose la clause et la liste des occurences
     *  @see Map
     *  @see List
     */
    private Map<Integer, Variable> variable;

    /**
     * Contruit une Formule CNF passée en parametre
     * @param clauses la collection(Map<{@link Integer},{@link List}) de clauses a placer dans la formule
     * @param variable la collection(Map<{@link Integer},{@link List}) variable
     */
    public Formule(Map<Integer, List<Integer>> clauses, Map<Integer, Variable> variable) {
        this.clauses = clauses;
        this.variable = variable;
    }

    /**
     * Returne une copie superficielle de l instance  <tt>clause</tt>.  (les
     * elements eux même sont copiés.)
     * @return  <tt>{@link Map}</tt> de clause
     */
    public  Map<Integer, List<Integer>> cloneClause(){
        Map<Integer,List<Integer>> clone = new LinkedHashMap<>();
        for (Entry<Integer,List<Integer>> entry: clauses.entrySet()){
            clone.put(entry.getKey(),new ArrayList<>(entry.getValue()));
        }
        return clone;
    }
    /**
     * Returne une copie superficielle de l instance  <tt>clause</tt>.  (les
     * elements eux même sont copiés.)
     * @return <tt>{@link Map}</tt> de variables
     */
    private  Map<Integer, Variable> cloneMapVariable(){
        Map<Integer,Variable> clone = new LinkedHashMap<>();
        for (Map.Entry<Integer,Variable> entry:variable.entrySet()){
            clone.put(entry.getKey(),entry.getValue().clone());
        }
        return clone;
    }
    /**
     * Evalation de la formule clauses avec un ensemble de <strong> variables affectées </strong>
     * et l ensemble des variables de la formule (key : value ).
     * La formule est clonée et uniquement <tt> restaurée en cas d echec de laffectation </tt>
     * La formule est <i>mise a jour</i> en fonction de l affactation pour obtenir une formule reduite
     * (ou egale à celle avant l evaluation de l affectation au  pire des cas)
     *
     * <p> <i> le premier resultat de la premiere affectation est crutial car la formule reelle qui en decoule
     *  est la formule sur la quelle toute les autres operations seront effectuées. La prémiere affectation n'est composée
     *  que de clauses unitaires et de varables pures qui va permettre de reduire la taille de la formule initiale pour
     *  faire apparaitre la formule initiale réelle
     * </i>
     *
     * <p> Aprés avoir evalué l affactation sur la formule, nous selectionons les clauses unitaires qui sont les plus
     * restrictives et la formule est mise a jour par rapport variables affectés et l'operation est reitterée jusqu'a
     * ce qu'il n y a plus de clauses unitaires
     *
     * <p>En suite ,nous selectionons les variables dites pures qui  apparaissent uniquement positives <tt> ou </tt> negatives
     * et la formule est mis a jour en consequence (suppression de clauses). Puis, si la nouvelle formule contient des clauses
     * unitaires on reviens a l etape precedente
     *
     * <p> <strong>A la sortie de c'est deux opérations si la formule est vide alors on a resolue le problème sinon on doit
     * faire un choix sur une varaible et recommencer l evalution de la nouvelles affectations  </strong>
     *
     * @param affectation  {@link Map affectation parielle des varaiables
     * @param firstTry indique si c'est la premiere affaction
     * @return  variable permetant faire un choix de type MOMM (max occu in mini m)
     * @throws IllegalStateException lors qu il y a un conflit entre une même varaibles ou une varaibles et son affectation
     */
    int eval(Map<Integer, Integer> affectation, boolean firstTry){
        final Map<Integer, List<Integer>> cloneClauses = cloneClause();
        final Map<Integer, Variable> variablesClone = cloneMapVariable();
        final Set<Integer> indiceClauseChoixVariable  = new LinkedHashSet<>();

        int sizeAffectation = 0;
        do {

          //  System.out.println(affectation );//+"       Clause  ***"+cloneClauses);

            List<Integer> indiceClauseUnitaireInClauses = new ArrayList<>(); // varibale qui indique si une clause unitaire apparait
            Set<Integer> indiceVariablePureInClauses = new LinkedHashSet<>(); // varibale qui indique si une clause unitaire apparait

            final List<Entry<Integer,Integer>> entryAffectationList = new ArrayList<>(affectation.entrySet());

            for (int j = sizeAffectation; j < affectation.entrySet().size(); j++) {
                Entry<Integer,Integer> affectationCourent = entryAffectationList.get(j);
                if(1 == affectationCourent.getValue()){
                    //cas ou la VA est Positive
                    if (variablesClone.get(affectationCourent.getKey())!= null) {
                        List<Integer> listOccurence = variablesClone.get(affectationCourent.getKey()).getPositive().getListOccurence();

                        for (int i: listOccurence){

                            if (cloneClauses.get(i)!=null) {
                                final List<Integer> clause = cloneClauses.get(i);
                                for (Integer k: clause){ // k variablesClone contenus dans la clause i
                                    if(Math.abs(k) == affectationCourent.getKey()) continue;
                                    if (k < 0) { // var avec occu negative
                                        variablesClone.get(Math.abs(k)).getNegative().getListOccurence().remove((Integer) i);
                                        if(variablesClone.get(Math.abs(k)).getNegative().getListOccurence().size() == 0){
                                            indiceVariablePureInClauses.add(Math.abs(k));
                                        }
                                    } else {// var avec occu positive
                                        variablesClone.get(Math.abs(k)).getPositive().getListOccurence().remove((Integer) i);
                                        if(variablesClone.get(Math.abs(k)).getPositive().getListOccurence().size() == 0){
                                            indiceVariablePureInClauses.add(Math.abs(k));
                                        }
                                    }
                                }
                            }
                            cloneClauses.remove(i);

                            /*
                             */
                            indiceClauseChoixVariable.remove(i);
                        }
                        listOccurence.clear();
                        listOccurence = variablesClone.get(affectationCourent.getKey()).getNegative().getListOccurence();

                        for (int i: listOccurence){
                            try {
                                cloneClauses.get(i).remove( new Integer(-affectationCourent.getKey()));
                            } catch (NullPointerException ignored) {
                                Main.LOGGER.info(ignored.getMessage());
                            }
                            if(cloneClauses.get(i).size() == 1){
                                indiceClauseUnitaireInClauses.add(i);
                            }
                            if(cloneClauses.get(i).size() == 2 ){ indiceClauseChoixVariable.add(i);}
                            if(cloneClauses.get(i).size() != 2 ){ indiceClauseChoixVariable.remove(i);}
                        }
                        listOccurence.clear();
                    }
                }else {
                    //cas ou VA negative
                    if (variablesClone.get(affectationCourent.getKey()) !=  null) {
                        List<Integer> listOccurence = variablesClone.get(affectationCourent.getKey()).getNegative().getListOccurence();
                        for (int i: listOccurence){ // i indice de la clause qui contient la variable affectation.getKey

                            if (cloneClauses.get(i)!=null) {
                                final List<Integer> clause = cloneClauses.get(i);
                                for (Integer k: clause){// k variablesClone contenus dans la clause i
                                    if(Math.abs(k) == affectationCourent.getKey()) continue;
                                    if (k < 0) { // var avec occu negative
                                        variablesClone.get(Math.abs(k)).getNegative().getListOccurence().remove((Integer) i);
                                        if(variablesClone.get(Math.abs(k)).getNegative().getListOccurence().size() == 0){
                                            indiceVariablePureInClauses.add(Math.abs(k));
                                        }
                                    } else {// var avec occu positive
                                        variablesClone.get(Math.abs(k)).getPositive().getListOccurence().remove((Integer) i);
                                        if(variablesClone.get(Math.abs(k)).getPositive().getListOccurence().size() == 0){
                                            indiceVariablePureInClauses.add(Math.abs(k));
                                        }
                                    }
                                }
                            }
                            cloneClauses.remove(i);
                            indiceClauseChoixVariable.remove(i);

                        }
                        listOccurence.clear();
                        listOccurence = variablesClone.get(affectationCourent.getKey()).getPositive().getListOccurence();
                        for (int i: listOccurence){
                            cloneClauses.get(i).remove(affectationCourent.getKey());
                            cloneClauses.get(i).remove( new Integer(-affectationCourent.getKey()));
                            if(cloneClauses.get(i).size() == 1){
                                indiceClauseUnitaireInClauses.add(i);
                            }
                            if(cloneClauses.get(i).size() == 2 ){ indiceClauseChoixVariable.add(i);}
                            if(cloneClauses.get(i).size() != 2 ){ indiceClauseChoixVariable.remove(i);}
                        }
                        listOccurence.clear();
                    }
                }
                variablesClone.remove(affectationCourent.getKey());
            }//for principal

            //recupere les clauses unitaire et les variablesClone pures
            do {
                sizeAffectation = affectation.size();
                if(cloneClauses.size()==0){
                    System.out.println("SAT");
                    System.err.println(affectation);
                    System.err.println(System.currentTimeMillis()-Main.tdebut);
                    System.exit(0);
                }
                List<Integer> uniqueVariableInFormule = new ArrayList<>();
                List<Integer> indiceClauseUnitaireAux = new ArrayList<>();
                int key, val;

                if( ! indiceClauseUnitaireInClauses.isEmpty()){
                    for (Integer i: indiceClauseUnitaireInClauses) {
                        if (cloneClauses.get(i) != null) {
                            if (cloneClauses.get(i).size() == 0) {
                                throw new IllegalStateException("Deux clauses unitaire contradictoire !x et !x : "+i);
                            }
                            if (cloneClauses.get(i).size() == 1) {
                                //unique clause
                                if (uniqueVariableInFormule.contains(-(cloneClauses.get(i).get(0)))) {
                                    throw new IllegalStateException("Deux clauses unitaire contradictoire !x et !x : " + cloneClauses.get(i).get(0));
                                }
                                key = cloneClauses.get(i).get(0);

                                uniqueVariableInFormule.add(cloneClauses.get(i).get(0));
                                val = (key < 0) ? -1 : 1;
                                affectation.putIfAbsent(Math.abs(key), val);
                                if (val < 0) {
                                    try {
                                        for (Integer k : variablesClone.get(Math.abs(key)).getNegative().getListOccurence()) {
                                            final Iterator<Entry<Integer, Variable>> it = variablesClone.entrySet().iterator();
                                            while (it.hasNext()) {
                                                Entry<Integer, Variable> aux = it.next();
                                                if (aux.getKey().equals(Math.abs(key))) continue;

                                                aux.getValue().getPositive().getListOccurence().remove(k);
                                                if(aux.getValue().getPositive().getListOccurence().size() == 0){
                                                    indiceVariablePureInClauses.add(aux.getKey());
                                                }


                                                aux.getValue().getNegative().getListOccurence().remove(k);
                                                if(aux.getValue().getNegative().getListOccurence().size() == 0){
                                                    indiceVariablePureInClauses.add(aux.getKey());
                                                }
                                                if (aux.getValue().getTotal() == 0){
                                                    it.remove();
                                                    indiceVariablePureInClauses.remove(aux.getKey());
                                                }

                                            }
                                            cloneClauses.remove(k);
                                            indiceClauseChoixVariable.remove(k);
                                        }
                                        variablesClone.get(Math.abs(key)).getNegative().getListOccurence().clear();
                                        for (Integer k : variablesClone.get(Math.abs(key)).getPositive().getListOccurence()) {
                                            cloneClauses.get(k).remove(((Integer) Math.negateExact(key)));
                                            if(cloneClauses.get(k).size() == 1){
                                                indiceClauseUnitaireAux.add(k);
                                            }
                                            if(cloneClauses.get(k).size() == 2 ){ indiceClauseChoixVariable.add(k);}
                                            if(cloneClauses.get(k).size() != 2 ){ indiceClauseChoixVariable.remove(k);}
                                        }
                                    } catch (Exception ignored) {
                                        Main.LOGGER.info(ignored.getMessage());
                                    }
                                } else {
                                    try {
                                        for (Integer k : variablesClone.get(Math.abs(key)).getPositive().getListOccurence()) {
                                            final Iterator<Entry<Integer, Variable>> it = variablesClone.entrySet().iterator();
                                            while (it.hasNext()) {
                                                Entry<Integer, Variable> aux = it.next();
                                                if (aux.getKey().equals(Math.abs(key))) continue;
                                                aux.getValue().getPositive().getListOccurence().remove(k);
                                                if(aux.getValue().getPositive().getListOccurence().size() == 0){
                                                    indiceVariablePureInClauses.add(aux.getKey());
                                                }

                                                aux.getValue().getNegative().getListOccurence().remove(k);
                                                if(aux.getValue().getNegative().getListOccurence().size() == 0){
                                                    indiceVariablePureInClauses.add(aux.getKey());
                                                }
                                                if (aux.getValue().getTotal() == 0){
                                                    it.remove();
                                                    indiceVariablePureInClauses.remove(aux.getKey());
                                                }
                                            }
                                            cloneClauses.remove(k);
                                            indiceClauseChoixVariable.remove(k);
                                        }
                                        variablesClone.get(Math.abs(key)).getPositive().getListOccurence().clear();

                                        for (Integer k : variablesClone.get(Math.abs(key)).getNegative().getListOccurence()) {
                                            cloneClauses.get(k).remove(((Integer) Math.negateExact(key)));
                                            if(cloneClauses.get(k).size() == 1){
                                                indiceClauseUnitaireAux.add(k);

                                            }
                                            if(cloneClauses.get(k).size() == 2 ){ indiceClauseChoixVariable.add(k);}
                                            if(cloneClauses.get(k).size() != 2 ){ indiceClauseChoixVariable.remove(k);}
                                        }
                                    } catch (Exception ignored) {
                                        Main.LOGGER.info(ignored.getMessage());
                                    }
                                }
                            }
                        }
                    }
                    indiceClauseUnitaireInClauses = indiceClauseUnitaireAux;
                    indiceClauseUnitaireAux = new ArrayList<>();

                }

            } while (sizeAffectation != affectation.size());

            sizeAffectation = affectation.size();
            for (Integer varPure : indiceVariablePureInClauses) {
                if ( !affectation.containsKey(Math.abs(varPure))) {
                    if(variablesClone.get(varPure).getPositive().getNbOccurence()==0){
                        affectation.put(varPure,-1);
                    }
                    if(variablesClone.get(varPure).getNegative().getNbOccurence()==0){
                        affectation.put(varPure,1);
                    }
                }
            }

        } while (sizeAffectation != affectation.size());//pas de nouvelle affectation

        final Map<Integer,Integer> varInClausesAndOccurence= new LinkedHashMap<>();
        int varDecisionForMOMM = -3;

        if( ! indiceClauseChoixVariable.isEmpty()){
            for (Integer i : indiceClauseChoixVariable) {

                for (Integer j: cloneClauses.get(i)) {
                    varInClausesAndOccurence.put(j,(varInClausesAndOccurence.containsKey(j)?varInClausesAndOccurence.get(j)+1:1));
                }
            }
            final List<Entry<Integer,Integer>> varInClauseList = new LinkedList<>(varInClausesAndOccurence.entrySet());
            final Entry<Integer, Integer> varMaxOccurence = Collections.max(varInClauseList, (u, v) -> u.getValue().compareTo(v.getValue()));
            varDecisionForMOMM = varMaxOccurence.getKey();
        }
        clauses = (firstTry)?cloneClauses:clauses;
        variable = (firstTry)?variablesClone:variable;
        //depiler la dernier valeur et reterster ou enpiler une autre si on ne peu pas decider
        return varDecisionForMOMM ;
    }


}