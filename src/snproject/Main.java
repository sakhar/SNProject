/*
 Edward Liu - UNI: eml2170
 Sakhar Alkhereyf - UNI: sa3147
 COMS 6998: Social Networks
 Fall 2014
 Computer Science, Columbia University
 Final Project
 */

package snproject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

/**
 *
 * @author sakhar
 */
public class Main {

    public static final double beta = 0.09; //Infection Rate
    public static final double gamma = 0.05; // Recovery Rate
    public static final double intialI = 1.0 / 1000.0; // Initial infected
    public static final int t = 2; // time
    public static UndirectedGraph<String, DefaultEdge> graph;

    public static void buildGraph(String path, UndirectedGraph<String, DefaultEdge> g) {
        Scanner input; // declare scanner input to read the input

        try {
            // the file structure should be as described on readme file
            input = new Scanner(new File(path));
        } catch (FileNotFoundException ex) {
            System.out.println("File" + " \"" + path + "\" is not found! please make sure that it is in the same directory of this program. Program terminated");
            ex.printStackTrace();
            return;
        }
        System.out.println("Building graph .. please wait");

        int i = 0; // line number
        while (input.hasNext()) {
            String line = input.nextLine();
            i++; // increment line number

            if (line.startsWith("#")) // to skip commented lines
            {
                continue;
            }
            try {

                String firstNode = line.split(" ")[0]; // first node id
                String secondNode = line.split(" ")[1]; // second node id
                if (!g.containsVertex(firstNode)) {
                    g.addVertex(firstNode); // if it already exists, nothing will be changed
                }
                if (!g.containsVertex(secondNode)) {
                    g.addVertex(secondNode); // if it already exists, nothing will be changed
                }
                if (firstNode.equals(secondNode)) // self-loops, won't be added
                {
                    continue;
                }
                if (!g.containsEdge(firstNode, secondNode)) {
                    g.addEdge(firstNode, secondNode); // if it already exists, nothing will be changed
                }
            } catch (Exception ex) {
                System.out.println("There is a problem with the file structure at line [" + i + "]. Program terminated");
                System.out.println(line);
                ex.printStackTrace(); // print error
                return;
            }
        }
    }

    public static Vector<String> getNeighbors(UndirectedGraph<String, DefaultEdge> g, String u) {
        Vector<String> neighbors = new Vector<String>();

        for (DefaultEdge e : g.edgesOf(u)) {
            String v = g.getEdgeTarget(e).equals(u) ? g.getEdgeSource(e) : g.getEdgeTarget(e);
            neighbors.add(v);
        }
        return neighbors;
    }

    public static void main(String[] args) {
        graph = new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);
        buildGraph("/Users/sakhar/Dropbox/columbia/Social Networks/SN14-Challenge2ExampleData/G1.txt", graph);
        HashSet<String> iNodes = new HashSet<>();
        HashSet<String> rNodes = new HashSet<>();
        System.out.println("graph:" + graph.vertexSet().size());
        Vector<String> vertexSet = new Vector<String>();
        vertexSet.addAll(graph.vertexSet());
        Random r = new Random();

        for (String s : vertexSet) {
            if (r.nextDouble() < intialI) {
                iNodes.add(s);
            }
        }

        for (int i = 0; i < t; i++) {
            HashSet<String> tempNew = new HashSet<>();
            HashSet<String> tempRem = new HashSet<>();
            for (String v : iNodes) {
                Vector<String> neighbors = getNeighbors(graph, v);
                for (String u : neighbors) {
                    if (!rNodes.contains(u)) {
                        if (r.nextDouble() < beta) {
                            tempNew.add(u);
                            System.out.println("add " + u + " to infected");
                        }
                    }
                }
                if (r.nextDouble() < gamma) {
                    tempRem.add(v);
                    System.out.println("remove " + v + " from infected");
                }
            }
            iNodes.removeAll(tempRem);
        }

        //System.out.println(iNodes.size());
    }

}
