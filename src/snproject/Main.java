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
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

import org.jgrapht.Graphs;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.BellmanFordShortestPath;
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
    public static int neighborhoods[];
    public static int maxT = 6; // maximum threshold for neighborhoodFunction()
    
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
    
    /**
     * //Uses Floyd-Warshall to compute all pairs shortest paths in O(n^3)
     * Uses BellmanFord on each node in O(n^2m)
     * Results on FB dataset: 
     *  i=2, count=16133014
		i=3, count=13416880
		i=4, count=9435028
		i=5, count=3573468
		i=6, count=1008298
		i=7, count=331084
		i=8, count=15620
		i=9, count=0
		i=10, count=0
     * 
     */
    public static int[] neighborhoodFunction(){

//    	FloydWarshallShortestPaths<String, DefaultEdge> fw = new FloydWarshallShortestPaths<String, DefaultEdge>(graph);
//    	Collection<GraphPath<String, DefaultEdge>> paths = fw.getShortestPaths();
//    	for(GraphPath<String, DefaultEdge> path : paths){
//    		if(path.getEdgeList().size() >= t)
//    			count++;
//    	}
        int values[] = new int[maxT];
    	for(String start : graph.vertexSet()){
    		BellmanFordShortestPath<String, DefaultEdge> bf = new BellmanFordShortestPath<>(graph, start);
                for(String end : graph.vertexSet()){	
    			if(!start.equals(end)){
                            for (int i = 0; i < maxT; i++) {
                                if(bf.getCost(end) > i) // why >?
                                    values[i]++;
                            }
    			}
    		}
    	}
    	//System.out.println("i=" + t + ", count=" + count);
    	return values;
    }

    /*//There is already a function in JGraphT
    public static Vector<String> getNeighbors(UndirectedGraph<String, DefaultEdge> g, String u) {
        Vector<String> neighbors = new Vector<String>();

        for (DefaultEdge e : g.edgesOf(u)) {
            String v = g.getEdgeTarget(e).equals(u) ? g.getEdgeSource(e) : g.getEdgeTarget(e);
            neighbors.add(v);
        }
        return neighbors;
    }*/

    public static void main(String[] args) {
        graph = new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);
        //buildGraph("/Users/sakhar/Dropbox/columbia/Social Networks/SN14-Challenge2ExampleData/G1.txt", graph);
        //buildGraph("/Users/edwardliu/Downloads/facebook_combined.txt", graph);
        buildGraph("/Users/sakhar/Downloads/facebook_combined.txt", graph);
        HashSet<String> iNodes = new HashSet<>();
        HashSet<String> rNodes = new HashSet<>();
        System.out.println("graph:" + graph.vertexSet().size());
        Vector<String> vertexSet = new Vector<String>();
        vertexSet.addAll(graph.vertexSet());
        Random r = new Random();
        
        
        neighborhoods = neighborhoodFunction();
        for (int i = 0; i < maxT; i++) {
            System.out.println("n="+neighborhoods[i]);
        }
        for (String s : vertexSet) {
            if (r.nextDouble() < intialI) {
                iNodes.add(s);
            }
        }

        for (int i = 0; i < t; i++) {
            HashSet<String> tempNew = new HashSet<>();
            HashSet<String> tempRem = new HashSet<>();
            for (String v : iNodes) {
                List<String> neighbors = Graphs.neighborListOf(graph, v);
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
            iNodes.addAll(tempNew);
            rNodes.addAll(tempRem);
            System.out.println("i nodes:"+iNodes.size());
            System.out.println("r nodes:"+rNodes.size());
        }
    }

}
