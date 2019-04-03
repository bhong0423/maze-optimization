package student;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import game.GetOutState;
import game.Tile;
import game.FindState;
import game.SewerDiver;
import game.Node;
import game.NodeStatus;
import game.Edge;

public class DiverMax<E> extends SewerDiver {
	HashSet<Long> visited = new HashSet<Long>();
	
    /** Get to the ring in as few steps as possible. Once you get there, 
     * you must return from this function in order to pick
     * it up. If you continue to move after finding the ring rather 
     * than returning, it will not count.
     * If you return from this function while not standing on top of the ring, 
     * it will count as a failure.
     * 
     * There is no limit to how many steps you can take, but you will receive
     * a score bonus multiplier for finding the ring in fewer steps.
     * 
     * At every step, you know only your current tile's ID and the ID of all 
     * open neighbor tiles, as well as the distance to the ring at each of these tiles
     * (ignoring walls and obstacles). 
     * 
     * In order to get information about the current state, use functions
     * currentLocation(), neighbors(), and distanceToRing() in FindState.
     * You know you are standing on the ring when distanceToRing() is 0.
     * 
     * Use function moveTo(long id) in FindState to move to a neighboring 
     * tile by its ID. Doing this will change state to reflect your new position.
     * 
     * A suggested first implementation that will always find the ring, but likely won't
     * receive a large bonus multiplier, is a depth-first walk. Some
     * modification is necessary to make the search better, in general.*/
    @Override public void findRing(FindState state) {
        //TODO : Find the ring and return.
        // DO NOT WRITE ALL THE CODE HERE. DO NOT MAKE THIS METHOD RECURSIVE.
        // Instead, write your method elsewhere, with a good specification,
        // and call it from this one.
    	
        //dFSWalk(state);
    	greedyTraversal(state);
    }
    
    public void dFSWalk(FindState state) {
    	long id = state.currentLocation();
    	visited.add(id);
    	Collection<NodeStatus> neighbors = state.neighbors();
    	int numNeighbors = neighbors.size();
    	NodeStatus[] neighborArray = neighbors.toArray(new NodeStatus[numNeighbors]); 
    	for (NodeStatus neighbor : neighborArray){
    		long neighborId = neighbor.getId();
    		if (!visited.contains(neighborId)) {
    			state.moveTo(neighborId);
    			if (state.distanceToRing() == 0)
    				return;
    			dFSWalk(state);
    			if (state.distanceToRing() == 0)
    	    		return;
    			state.moveTo(id);
    		}
    	}
    }
    
    public void greedyTraversal(FindState state) {
    	long id = state.currentLocation();
    	visited.add(id);
    	Collection<NodeStatus> neighbors = state.neighbors();
    	int numNeighbors = neighbors.size();
    	NodeStatus[] neighborArray = neighbors.toArray(new NodeStatus[numNeighbors]); 
    	Heap<NodeStatus> neighborHeap = heapify(neighborArray);
    	NodeStatus temp;
    	long tempId = id;
    	while (neighborHeap.size() > 0) {
    		temp = neighborHeap.poll();	
    		tempId = temp.getId();
    		if (!visited.contains(tempId)) {
    			state.moveTo(tempId);
    			if (state.distanceToRing() == 0)
    				return;
    			greedyTraversal(state);
    			if (state.distanceToRing() == 0)
    				return;
    			state.moveTo(id);
    		}
    	}
    }
    
    //Precondition: b is filled with elements
    public Heap<NodeStatus> heapify(NodeStatus[] b)
    {
    	if (b.equals(null))
    		return null;
    	Heap<NodeStatus> neighbors = new Heap<NodeStatus>();
    	int ringDistance;
    	for (NodeStatus neighbor: b) {
    		ringDistance = neighbor.getDistanceToTarget();
    		neighbors.add(neighbor, ringDistance);
    	}
    	return neighbors;
    	
    }

    /** Get out of the sewer system before the steps are all used, trying to collect
     * as many coins as possible along the way. Your solution must ALWAYS get out
     * before the steps are all used, and this should be prioritized above
     * collecting coins.
     * 
     * You now have access to the entire underlying graph, which can be accessed
     * through GetOutState. currentNode() and getExit() will return Node objects
     * of interest, and getNodes() will return a collection of all nodes on the graph. 
     * 
     * You have to get out of the sewer system in the number of steps given by
     * getStepsRemaining(); for each move along an edge, this number is decremented
     * by the weight of the edge taken.
     * 
     * Use moveTo(n) to move to a node n that is adjacent to the current node.
     * When n is moved-to, coins on node n are automatically picked up.
     * 
     * You must return from this function while standing at the exit. Failing to
     * do so before steps run out or returning from the wrong node will be
     * considered a failed run.
     * 
     * Initially, there are enough steps to get from the starting point to the
     * exit using the shortest path, although this will not collect many coins.
     * For this reason, a good starting solution is to use the shortest path to
     * the exit. */
    @Override public void getOut(GetOutState state) {
        //TODO: Get out of the sewer system before the steps are used up.
        // DO NOT WRITE ALL THE CODE HERE. Instead, write your method elsewhere,
        //with a good specification, and call it from this one.
    	
    	//shortestPathGetOut(state);
    	updatingPriorityPathGetOut(state);
    }
    
    public void shortestPathGetOut(GetOutState state) {
    	//follow the path given by the shortest path list
    	Node current = state.currentNode();
    	Node endNode = state.getExit();
    	List<Node> shortestPath = Paths.shortestPath(current, endNode);
    	int pathSize = shortestPath.size();
    	Node[] shortestPathArray = shortestPath.toArray(new Node[pathSize]); 
    	for (int i = 1; i < pathSize; i++) {
    		state.moveTo(shortestPathArray[i]); //moves to itself as well
    	}
    }
    
    public void updatingPriorityPathGetOut(GetOutState state) {
    	Node endNode = state.getExit();
	    //scans all the nodes and saves nodes with coins in a heap
	    Node current = state.currentNode();
	    Heap<List<Node>> coinPaths = new Heap<List<Node>>();
	    double pathLength;
	    for (Node element: state.allNodes()) {
	    	//sets priority based on coin value and distance
	    	List<Node> path = Paths.shortestPath(current, element);
	    	double coinPriority = 0;
	    	for (Node n : path) {
	    		coinPriority += n.getTile().coins();
	    	}
	    	pathLength = Paths.pathDistance(Paths.shortestPath(current, element));
	    	if (pathLength != 0) {	
	    		double priority = -(coinPriority / pathLength);
	    		if(coinPriority != 0)
	    			coinPaths.add(path, priority);
	    	}
	    }
	    
	    //exit by shortest path if there are no coins
	    if (coinPaths.size == 0) {
	    	shortestPathGetOut(state);
	    	return;
	    }
	    		
	    //creates a path to highest priority coin using shortest path
	    List<Node> path = coinPaths.poll();
	    	
	    //traverses path if there are enough steps and uses shortest path to exit
	    Node next = path.get(1);
	    int nextEdge = current.getEdge(next).length;
	    int shortestPath = Paths.pathDistance(Paths.shortestPath(current, endNode));
	    //if there are enough steps remaining
	    if (shortestPath <= state.stepsLeft() - 2 * nextEdge) {
	    	state.moveTo(next);
	    	updatingPriorityPathGetOut(state);
	    //else exit by shortest path
	    } else {
	    	shortestPathGetOut(state);
	    	return;
	    }
    }
    
}
