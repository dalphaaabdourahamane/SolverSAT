package sandIOmega;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Logger;

/**
 * Classe principale qui gere la construction de l arbre des affecation
 * Created by SigmaPi & Sandii on 14/04/2016. yes
 * @version 4.3
 * @author Alpha Sandratra
 */
public class Main {

    private static Map<Integer,List<Integer>> formuleClauses = new LinkedHashMap<>();
    private static Map<Integer,Variable> variables = new LinkedHashMap<>();
    private static Map<Integer,Integer> affectations = new LinkedHashMap<>(); //Valeur d'une affectations : VRAI = 1, FAUX = (-1)
    /*
        Gestion de la pile
     */
    private static Deque<Integer> pileAffectations = new ConcurrentLinkedDeque<>();
    private static HashSet<Integer> tabGestionAffectations = new HashSet<>();
    static long tdebut = System.currentTimeMillis();
    protected static boolean ALEA =   false;
    protected static Logger LOGGER = Logger.getLogger("SAT SOLVER ");

    public static void main(String arg[]){

        final String fileName;

        int TIMEOUT = 5000000;
        if(arg.length >=1){
            fileName = arg[0];
        }else {
            throw  new IllegalArgumentException("ERREUR : Pas de fichier donné en parametre");
        }

        if(arg.length >=2){
            ALEA = (arg[1].equalsIgnoreCase("r"));
        }

        if(arg.length >=3){
            TIMEOUT = Integer.parseInt(arg[2]);
        }

        try {
            int nVar=0,mClause=0;
            int clauseIndice = 1;
            final BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;

            while ((line = reader.readLine()) != null){
                if ( line.startsWith("c")) continue;
                if(line.trim().equals("")) continue;
                if (line.startsWith("p")) {
                    final String[] split = line.trim().split("\\s+");
                    nVar = Integer.parseInt(split[2]);
                    mClause = Integer.parseInt(split[3]);
                    continue;
                };
                final String[] splitLine = line.trim().split("\\s+"); // Liste des variables lu sur la ligne du fichier
                final ArrayList<Integer> listeVariables = new ArrayList<>(splitLine.length-1); // Liste d'objets Variables (cf.Classe Variable)


                if(splitLine.length == 2){
                    // Recherche des formuleClauses unitaires existantes dans le fichier
                    int key = Integer.parseInt(splitLine[0]);
                    int val = (key<0)? 1:-1;
                    if(affectations.get(Math.abs(key)) != null && affectations.get(Math.abs(key)) == val){
                        System.out.println("UNSAT");
                        System.err.println(System.currentTimeMillis()-Main.tdebut);
                        System.exit(1);
                    }
                    val = (key<0)? -1:1;
                    affectations.putIfAbsent(Math.abs(key),val);
                    if(val<0){ // CAS 1 : Variable affectée positivement
                        try {
                            List<Integer> listOccurence = variables.get(Math.abs(key)).getNegative().getListOccurence();
                            for (Integer k: listOccurence){
                                final Iterator<Map.Entry<Integer, Variable>> it = variables.entrySet().iterator();
                                while (it.hasNext()) {
                                    Map.Entry<Integer, Variable> aux = it.next();
                                    if (aux.getKey().equals(Math.abs(key))) continue;
                                    aux.getValue().getPositive().getListOccurence().remove(k);
                                    aux.getValue().getNegative().getListOccurence().remove(k);
                                    if (aux.getValue().getTotal() == 0) it.remove();
                                }
                                formuleClauses.remove(k);
                            }
                            listOccurence.clear();
                            for (Integer k: variables.get(Math.abs(key)).getPositive().getListOccurence()){
                                formuleClauses.get(k).remove(((Integer)Math.negateExact(key)));
                            }
                        } catch (Exception ignored) {
                            LOGGER.info(ignored.getMessage());

                        }
                    }else { // CAS 2 : Variable affectée négativement
                        try {
                            List<Integer> listOccurence = variables.get(Math.abs(key)).getPositive().getListOccurence();
                            for (Integer k: listOccurence){ // k : indice de la formuleClauses courante
                                final Iterator<Map.Entry<Integer, Variable>> it = variables.entrySet().iterator();
                                while (it.hasNext()) {
                                    Map.Entry<Integer, Variable> aux = it.next();
                                    if (aux.getKey().equals(Math.abs(key))) continue;
                                    aux.getValue().getPositive().getListOccurence().remove(k);
                                    aux.getValue().getNegative().getListOccurence().remove(k);
                                    if (aux.getValue().getTotal() == 0) it.remove();
                                }
                                formuleClauses.remove(k);
                            }

                            listOccurence.clear();

                            for (Integer k: variables.get(Math.abs(key)).getNegative().getListOccurence()){
                                formuleClauses.get(k).remove(((Integer)Math.negateExact(key)));
                            }
                        } catch (Exception ignored) {
                            LOGGER.info(ignored.getMessage());

                        }
                    }
                    variables.remove(Math.abs(key));
                    listeVariables.clear();
                    continue;

                }
                int varInLine; // Variable lue
                for (String s: splitLine){
                    varInLine = Integer.parseInt(s);
                    int keyVar = varInLine;
                    int valVar = (keyVar<0)? -1:1;
                    keyVar = Math.abs(varInLine);
                    if(affectations.get(keyVar) != null && affectations.get(keyVar) == valVar){
                        // Suppression de la formuleClauses Contenant une variable déjà affectée
                        for(Integer k:listeVariables){ // k : Variable affectée
                            if(k<0){
                                variables.get(Math.abs(k)).getNegative().getListOccurence().remove((Integer)clauseIndice);
                            }else {
                                variables.get(Math.abs(k)).getPositive().getListOccurence().remove((Integer)clauseIndice);
                            }
                        }
                        listeVariables.clear();
                        break;
                    }
                    if(affectations.get(keyVar) != null && affectations.get(keyVar) != valVar){
                        continue;
                    }

                    if(varInLine == 0) break; // Arrivée à la fin de la ligne lue
                    if(listeVariables.contains(-(varInLine))){
                        for(Integer k:listeVariables){
                            if(k<0){
                                variables.get(Math.abs(k)).getNegative().getListOccurence().remove((Integer)clauseIndice);
                            }else {
                                variables.get(Math.abs(k)).getPositive().getListOccurence().remove((Integer)clauseIndice);
                            }
                        }
                        listeVariables.clear();
                        break;
                    }
                    listeVariables.add(varInLine);
                    variables.putIfAbsent(Math.abs(varInLine), new Variable());
                    final Variable variable = variables.get(Math.abs(varInLine));
                    if(varInLine > 0){
                        variable.getPositive().addNbOccurence();
                        variable.getPositive().getListOccurence().add(clauseIndice);
                    }else{
                        variable.getNegative().addNbOccurence();
                        variable.getNegative().getListOccurence().add(clauseIndice);
                    }
                }
                if(listeVariables.size()!= 0) formuleClauses.put(clauseIndice++,listeVariables);
            }

            variables = sortByValues(variables, affectations);
            Formule formule = new Formule(formuleClauses,variables);
            int varForMOMM = formule.eval(affectations, true);// Première évaluation de la formule


            do {
                if(System.currentTimeMillis()-Main.tdebut>TIMEOUT){
                    System.out.println("TIME OUT");
                    System.exit(-1);
                }
                //empile une variable
                int variableDecision = -1; // Variable à affecter
                if (!ALEA) {
                    if (varForMOMM <0  ) {
                        for (Map.Entry<Integer,Variable> var: variables.entrySet()){
                            //empile les nouvelles variables si il y en a
                            if( (!affectations.containsKey(Math.abs(var.getKey()))) &&
                                    (!pileAffectations.contains(var.getKey()) && !pileAffectations.contains(-var.getKey()))){
                                variableDecision = var.getKey()*var.getValue().getPowerfulState();
                                pileAffectations.addFirst(variableDecision);
                                tabGestionAffectations.add(variableDecision);
                                break;
                            }
                        }
                    }else {
                        if( (!affectations.containsKey(Math.abs(varForMOMM))) &&
                                (!pileAffectations.contains(varForMOMM) && !pileAffectations.contains(-varForMOMM))) {
                            variableDecision = varForMOMM;
                            pileAffectations.addFirst(variableDecision);
                            tabGestionAffectations.add(variableDecision);
                        }

                    }
                } else {

                    ArrayList<Integer> list = new ArrayList<>(variables.keySet());
                    list.removeAll(affectations.keySet());
                    Collections.shuffle(list);
                    variableDecision = list.get(new Random().nextInt(list.size()));
                    pileAffectations.addFirst(variableDecision);
                    tabGestionAffectations.add(variableDecision);
                }

                if(variableDecision > 0) tabGestionAffectations.add(variableDecision);

                affectations.put(Math.abs(variableDecision),(variableDecision<0)?-1:1);

                do {

                    boolean isRollBackByException = false;
                    try {
                        varForMOMM = formule.eval(affectations,false);
                    } catch (Exception ignored) {
                        LOGGER.info(ignored.getMessage());
                        isRollBackByException =  true;
                    }
                    if(!isRollBackByException && (variables.size() != pileAffectations.size())) {
                        // il reste un nouvelle variable a affacter
                        varForMOMM = -1;
                        break;
                    }
                    do {
                        variableDecision = pileAffectations.removeFirst();


                        boolean findItem = false;
                        Iterator<Map.Entry<Integer,Integer>> it = affectations.entrySet().iterator();
                        while (it.hasNext()){
                            Map.Entry<Integer,Integer> entry = it.next();
                            if(findItem || (entry.getKey()*entry.getValue() == variableDecision)){
                                findItem = true;
                                it.remove();
                            }
                        }

                        if(tabGestionAffectations.contains(variableDecision)){
                            affectations.put(Math.abs(variableDecision),(variableDecision<0)?1:-1);
                            pileAffectations.addFirst(-variableDecision);
                            break;
                        }
                    } while (!pileAffectations.isEmpty());
                    varForMOMM = -1;

                } while (!pileAffectations.isEmpty());



            } while (!pileAffectations.isEmpty());


            System.out.println("UNSAT");
            System.err.println(System.currentTimeMillis()-Main.tdebut);
            System.exit(0);


            reader.close();
        } catch (IOException ignored) {
            LOGGER.info(ignored.getMessage());
        }


    }

    private static Map<Integer, Variable> sortByValues(Map<Integer,Variable> map,Map<Integer,Integer> aff) {

        LinkedList<Map.Entry<Integer,Variable>> list = new LinkedList<>(map.entrySet());
        // Defined Custom Comparator here
        Collections.sort(list, (u, v) -> (v.getValue()).compareTo(u.getValue()));
        // Here I am copying the sorted list in HashMap
        // using LinkedHashMap to preserve the insertion order
        Map<Integer,Variable> sortedHashMap = new LinkedHashMap<>();
        for (Map.Entry<Integer, Variable> entry : list) {
            sortedHashMap.put(entry.getKey(), entry.getValue());
            if (!aff.containsKey(Math.abs(entry.getKey()))) {
                if(entry.getValue().getPositive().getNbOccurence()==0){
                    aff.put(entry.getKey(),-1);
                }
                if(entry.getValue().getNegative().getNbOccurence()==0){
                    aff.put(entry.getKey(),1);
                }
            }
        }
        return sortedHashMap;
    }

    private static Map<Integer, Variable> cloneMapVariable(Map<Integer,Variable> map){
        Map<Integer,Variable> clone = new LinkedHashMap<>();
        for (Map.Entry<Integer,Variable> entry:map.entrySet()){
            clone.put(entry.getKey(),entry.getValue().clone());
        }
        return clone;
    }



}