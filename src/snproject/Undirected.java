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
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.BellmanFordShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.SimpleGraph;

/**
 *
 * @author sakhar
 */
public class Undirected {

    public static final double beta = 0.05; //Infection Rate
    public static final double gamma = 0.05; // Recovery Rate
    public static final double intialI = 1.0 / 1000.0; // Initial infected
    public static final int t = 100; // time
    public static UndirectedGraph<String, DefaultEdge> graph;
    public static int neighborhoods[];
    public static int maxT = 6; // maximum threshold for neighborhoodFunction()

    public static void buildGraph(String path, Graph<String, DefaultEdge> g) {
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
     * //Uses Floyd-Warshall to compute all pairs shortest paths in O(n^3) Uses
     * BellmanFord on each node in O(n^2m) Results on FB dataset: n=0 n=176468
     * n=2892602 n=6874454 n=12736014 n=15301184
     *
     */
    public static int[] neighborhoodFunction() {

		//    	FloydWarshallShortestPaths<String, DefaultEdge> fw = new FloydWarshallShortestPaths<String, DefaultEdge>(graph);
        //    	Collection<GraphPath<String, DefaultEdge>> paths = fw.getShortestPaths();
        //    	for(GraphPath<String, DefaultEdge> path : paths){
        //    		if(path.getEdgeList().size() >= t)
        //    			count++;
        //    	}
        int values[] = new int[maxT];
        for (String start : graph.vertexSet()) {
            BellmanFordShortestPath<String, DefaultEdge> bf = new BellmanFordShortestPath<>(graph, start);
            for (String end : graph.vertexSet()) {
                if (!start.equals(end)) {
                    double cost = bf.getCost(end);
                    for (int t = 0; t < maxT; t++) {
                        if (cost <= t) {
                            values[t]++;
                        }
                    }
                }
            }
        }
        return values;
    }

    public static void main(String[] args) throws IOException {
        graph = new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);
        
        //buildGraph("/Users/edwardliu/Downloads/facebook_combined.txt", graph);
        buildGraph("/Users/sakhar/Downloads/facebook_combined.txt", graph);
        //buildGraph("/Users/sakhar/Dropbox/FinalProject/sampled-datasets/twitter-directed.txt", graph);
        HashSet<String> iNodes = new HashSet<>();
        HashSet<String> rNodes = new HashSet<>();
        System.out.println("graph:" + graph.vertexSet().size());
        Vector<String> vertexSet = new Vector<String>();
        vertexSet.addAll(graph.vertexSet());
        Random r = new Random();
        
        neighborhoods = neighborhoodFunction();
        for (int i = 0; i < maxT; i++) {
            System.out.println("n=" + neighborhoods[i]);
        }
        for (String s : vertexSet) {
            if (r.nextDouble() < intialI) {
                iNodes.add(s);
            }
        }
        String output = "";
        PrintWriter writer = new PrintWriter("/Users/sakhar/Dropbox/FinalProject/facebook/2/sir.csv");
        writer.print("id");
        for (int i = 0; i < t; i++) {
            if (i%10==0)
                writer.print(",t"+i);
        }
        writer.println();
        HashMap<Integer, List<String>> map = new HashMap< Integer, List<String>>();
        for (int i = 0; i < t; i++) {
            
            System.out.println("==========t=" + i + "==========");
            HashSet<String> tempNew = new HashSet<>();
            HashSet<String> tempRem = new HashSet<>();
            for (String v : iNodes) {
                List<String> neighbors = Graphs.neighborListOf(graph, v);
                for (String u : neighbors) {
                    if (!rNodes.contains(u)&&!tempRem.contains(u)&&!iNodes.contains(u)) {
                        if (r.nextDouble() < beta) {
                            tempNew.add(u);
                            //System.out.println("add " + u + " to infected");
                        }
                    }
                }
                if (r.nextDouble() < gamma) {
                    tempRem.add(v);
                    //System.out.println("remove " + v + " from infected");
                }
            }
            iNodes.addAll(tempNew);
            iNodes.removeAll(tempRem);
            rNodes.addAll(tempRem);
            System.out.println("=========================");
            System.out.println("s nodes:" + (graph.vertexSet().size() - iNodes.size() - rNodes.size()));
            System.out.println("i nodes:" + iNodes.size());
            System.out.println("r nodes:" + rNodes.size());
            System.out.println("=========================");
            output += "t:"+i+"\n";
            output += "s:"+(graph.vertexSet().size() - iNodes.size() - rNodes.size())+"\n";
            output += "r:"+rNodes.size()+"\n";
            output += "i:"+iNodes.size()+"\n";
            output += "================\n";
            
            if(i%10!=0)
                continue;
            
            for (String v:vertexSet) {
                if(rNodes.contains(v)||iNodes.contains(v))
                    continue;
                add(Integer.parseInt(v), "1", map);
                //writer.println(v+","+1);
            }
            
            for (String v:iNodes) {
                add(Integer.parseInt(v), "2", map);
                
                //writer.println(v+","+2);
            }
            for (String v:rNodes) {
                add(Integer.parseInt(v), "3", map);
                //writer.println(v+","+3);
            }
            
            
        }
        
        for (Entry<Integer, List<String>> e:map.entrySet()) {
            writer.print(e.getKey());
            for (int i=0; i<e.getValue().size();i++) {
                writer.print(","+e.getValue().get(i));
            }
            writer.println();
        }
        
        writer.close();
        System.out.println();
        System.out.println(output);
    }
    
    // copied from stackoverflow
    public static void add(Integer key, String newValue,Map<Integer, List<String>> map) {
    List<String> currentValue = map.get(key);
    if (currentValue == null) {
        currentValue = new ArrayList<String>();
        map.put(key, currentValue);
    }
    currentValue.add(newValue);
}

}
