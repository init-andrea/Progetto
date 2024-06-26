
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class Utilities {

    public static String[] getInputFileNames(Scanner userInput) {
        String[] nomiFile = new String[2];
        System.out.println("Scrivi il nome del file con i nodi: ");
        nomiFile[0] = userInput.next();
        System.out.println("Scrivi il nome del file con gli archi: ");
        nomiFile[1] = userInput.next();
        return nomiFile;
    }

    public static int getInputNodesNumber(Scanner userInput) {
        System.out.println("Inserisci il numero di nodi del grafo e premi invio: ");
        while (!userInput.hasNextInt()) {
            userInput.next();
            System.out.println("Riprova, devi inserire un numero intero");
        }
        return userInput.nextInt();
    }

    public static double getInputProbability(Scanner userInput) {
        double probabilità;
        System.out.println("Inserisci la probabilità (compresa tra 0 e 1) di generare un arco tra due vertici e premi invio: ");
        while (true) {
            if (userInput.hasNextDouble()) {
                probabilità = userInput.nextDouble();
                if (probabilità >= 0 && probabilità <= 1) {
                    return probabilità;
                } else {
                    System.out.println("Riprova, devi inserire un numero compreso tra 0 e 1");
                }
            } else {
                System.out.println("Riprova, devi inserire un numero");
                userInput.next();       // togliamo l'input che non è valido dallo scanner
            }
        }
    }

    public static int getInputExperimentsNumber(Scanner userInput) {
        System.out.println("Inserisci il numero di esperimenti da eseguire e premi invio: ");
        while (!userInput.hasNextInt()) {
            userInput.next();
            System.out.println("Riprova, devi inserire un numero intero");
        }
        return userInput.nextInt();
    }

    

    // distanza euclidea come euristica nel caso generale, può essere Manhattan se il grafo è una griglia
    // calcola la distanza euclidea tra due nodi che hanno coordinate x e y
    public static double euclideanDistance(Node a, Node b) {
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        return Math.hypot(dx, dy);
    }

    
    public static void printGraph(Node[] nodes) {
        for (Node node : nodes) {
            System.out.print(node.getLabel() + " (" + node.getX() + ", " + node.getY() + "): ");
            for (Edge edge : node.getEdges()) {
                System.out.print("->" + edge.getDest().getLabel() + " (cost: " + edge.getCost() + ")");
            }
            System.out.println();
        }
    }

    public static void writeResultToFile(String nomeFile, String testo) {
        try {
            File file = new File(nomeFile);
            if (!file.exists()) {
                file.createNewFile();
                System.out.println("Il file " + nomeFile + " è stato creato");
            }
            FileWriter writer = new FileWriter(nomeFile, true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);

            bufferedWriter.write(testo);
            bufferedWriter.newLine();
            bufferedWriter.close();
            System.out.println("Il file " + nomeFile + " dei risultati è stato aggiornato");
        } catch (IOException e) {
            System.out.println("Errore nella scrittura del file: " + e.getMessage());
        }
    }

    public static void writeErdosRenyiGraphToFile(Node[] nodes, double probabilità) {
        int numeroNodi = nodes.length;
        int numeroArchi = 0;

        for (Node node : nodes) {
            numeroArchi += node.getEdges().size();
        }

        String nomeFileNodi = "Nodes_Erdos_Renyi_" + numeroNodi + "V_" + probabilità + "P.txt";
        String nomeFileArchi = "Edges_Erdos_Renyi_" + numeroArchi + "E_" + probabilità + "P.txt";

        try {
            File fileNodi = new File(nomeFileNodi);
            File fileArchi = new File(nomeFileArchi);
            if (!fileNodi.exists()) {
                fileNodi.createNewFile();
                System.out.println("Il file " + nomeFileNodi + " è stato creato");
            }
            if (!fileArchi.exists()) {
                fileArchi.createNewFile();
                System.out.println("Il file " + nomeFileArchi + " è stato creato");
            }
            FileWriter nodesWriter = new FileWriter(nomeFileNodi, true);
            BufferedWriter nodesBufferedWriter = new BufferedWriter(nodesWriter);
            FileWriter edgesWriter = new FileWriter(nomeFileArchi, true);
            BufferedWriter edgesBufferedWriter = new BufferedWriter(edgesWriter);

            int counter = 0;
            for (Node node : nodes) {
                nodesBufferedWriter.write(node.getLabel(1) + " " + node.getX() + " " + node.getY());
                nodesBufferedWriter.newLine();
                for (Edge edge : node.getEdges()) {
                    edgesBufferedWriter.write("" + (counter + 1) + " " + edge.getSrc().getLabel() + " " + edge.getDest().getLabel() + " " + edge.getCost());
                    edgesBufferedWriter.newLine();
                    counter++;
                }
            }
            nodesBufferedWriter.close();
            edgesBufferedWriter.close();
            //System.out.println("Scrittura completata");
        } catch (IOException e) {
            System.out.println("Errore nella scrittura del file: " + e.getMessage());
        }
    }

    public static Node[] loadGraphFromFiles(String nodesFilePath, String edgesFilePath, double probability) throws IOException {
        Map<String, Node> nodeMap = new HashMap<>();
        Random random = new Random();

        // leggi il file Nodes.txt
        try (BufferedReader br = new BufferedReader(new FileReader(nodesFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                String nodeId = parts[0];
                double x = Double.parseDouble(parts[1]);
                double y = Double.parseDouble(parts[2]);
                Node node = new Node(nodeId, x, y);
                nodeMap.put(nodeId, node);
            }
        } catch (FileNotFoundException e) {
            throw e;
        }

        // leggi il file Edges.txt
        try (BufferedReader br = new BufferedReader(new FileReader(edgesFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                //String edgeId = parts[0];
                String startNodeId = parts[1];
                String endNodeId = parts[2];
                double distance = Double.parseDouble(parts[3]);

                Node startNode = nodeMap.get(startNodeId);
                Node endNode = nodeMap.get(endNodeId);

                if (startNode != null && endNode != null && !startNode.hasEdge(endNode) && !endNode.hasEdge(startNode)) {
                    startNode.addEdge(endNode, distance);

                    if (random.nextDouble() <= probability) {        // la probabilità di creare un secondo arco per grafo non orientato
                        endNode.addEdge(startNode, distance);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            throw e;
        }
        
        Node[] nodesArray = nodeMap.values().toArray(new Node[0]);
        return nodesArray;

    }

}
