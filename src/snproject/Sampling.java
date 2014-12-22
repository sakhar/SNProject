/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package snproject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.alg.ConnectivityInspector;
/**
 *
 * @author sakhar
 */
public class Sampling {
    public static SimpleDirectedGraph<String, DefaultEdge> graph;
    public static int threshold = 5000;
    
    public static void main(String args[]){
        
        graph = new SimpleDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
        buildGraph("/Users/sakhar/Downloads/twitter_combined.txt", graph);
        System.out.println("nodes:"+graph.vertexSet().size());
        System.out.println("edges:"+graph.edgeSet().size());
        Random r = new Random();
        HashSet<String> set = new HashSet<>();
        
        //HashSet<DefaultEdge> edges = new HashSet<>();
                
        for (String v:graph.vertexSet()) {
           if(r.nextDouble()<=0.90)
                set.add(v);
        }
        graph.removeAllVertices(set);
        
        
        ConnectivityInspector inspector = new ConnectivityInspector(graph);
        List<Set<String>> connectedSets = inspector.connectedSets(); // get all connected components
        int maxIndex = 0;
        int max = 0;
        for (int i = 0; i < connectedSets.size(); i++) {
            if(connectedSets.get(i).size()>max&&connectedSets.get(i).size()<threshold){
                maxIndex = i;
                max = connectedSets.get(i).size();
            }
        }
        //System.out.println(connectedSets.get(maxIndex).size());
        //System.out.println(maxIndex);
        for (int i = 0; i < connectedSets.size(); i++) {
            if(i==maxIndex)
                continue;
            graph.removeAllVertices(connectedSets.get(i));
        }
        
        System.out.println("nodes:"+graph.vertexSet().size());
        System.out.println("edges:"+graph.edgeSet().size());
        for (DefaultEdge e:graph.edgeSet()) {
            System.out.println(graph.getEdgeSource(e)+" "+graph.getEdgeTarget(e));
        }
        
    }
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
}
