package com.anjalipancholi.assignment1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;

public class WordNet {
    private final Map<String, List<Integer>> hypernymMap;
    private final Map<Integer, String> synsetMap;
    private SAP sap;
    private int count;


    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) {
            throw new IllegalArgumentException("File is empty");
        }
        synsetMap = new HashMap<>();
        hypernymMap = new HashMap<>();
        count = 0;
        readSynsets(synsets);
        readHypernyms(hypernyms);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return hypernymMap.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) {
            throw new IllegalArgumentException("String is null");
        }
        return hypernymMap.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException("Invalid");
        }
        return sap.length(hypernymMap.get(nounA), hypernymMap.get(nounB));

    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException("Invalid");
        }
        return synsetMap.get(sap.ancestor(hypernymMap.get(nounA), hypernymMap.get(nounB)));
    }


    private void readSynsets(String synsets) {
        In string = new In(synsets);
        while (string.hasNextLine()) {
            count++;
            String line = string.readLine();
            String[] words = line.split(",");
            if (words.length < 2) {
                continue;
            }
            int synsetId = Integer.parseInt(words[0]);
            synsetMap.put(synsetId, words[1]);
            String[] synsetNouns = words[1].split(" ");
            for (String i : synsetNouns) {
                List<Integer> list = hypernymMap.get(i);
                if (list != null) {
                    list.add(synsetId);
                } else {
                    List<Integer> list2 = new ArrayList<>();
                    list2.add(synsetId);
                    hypernymMap.put(i, list2);
                }
            }
        }
    }

    private void readHypernyms(String hypernyms) {
        In string = new In(hypernyms);
        Digraph digraph = new Digraph(count);
        while ((string.hasNextLine())) {
            String line = string.readLine();
            String[] element = line.split(",");
            if (element.length < 2) continue;
            int id = Integer.parseInt(element[0]);
            for (int i = 1; i < element.length; i++) {
                int edges = Integer.parseInt(element[i]);
                digraph.addEdge(id, edges);
            }
        }
        DirectedCycle directedCycle = new DirectedCycle(digraph);
        if (directedCycle.hasCycle()) {
            throw new IllegalArgumentException("Input has a cycle");
        }
        int root = 0;
        for (int i = 0; i < count; i++) {
            if (digraph.outdegree(i) == 0) {
                root++;
            }
            if (root > 1) {
                throw new IllegalArgumentException("It has more than one root");
            }
        }
        sap = new SAP(digraph);
    }


    public static void main(String[] args) {
    }
}
