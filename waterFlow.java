import java.io.*;
import java.util.*;

class Input {
    private String task;
    private String source;
    private String destination;
    private String middleNodes;
    private int noOfPipes;
    private String[] graph;
    private int startTime;

    Input(){
        task = "";
        source = "";
        destination = "";
        middleNodes = "";
        noOfPipes = 0;
        graph = new String[0];
        startTime = 0;
    }

    public void setTask(String temp){
        task = temp;
    }
    public void setSource(String temp){
        source = temp;
    }
    public void setDestination(String temp){
        destination = temp;
    }
    public void setMiddleNodes(String temp){
        middleNodes = temp;
    }
    public void setNoOfPipes(int temp){
        noOfPipes = temp;
    }
    public void setGraph(String[] temp){
        graph = new String[noOfPipes];
        graph = temp;
    }
    public void setStartTime(int temp){
        startTime = temp;
    }

    public String getTask(){
        return task;
    }
    public String getSource(){
        return source;
    }
    public String getDestination(){
        return destination;
    }
    public String getMiddleNodes(){
        return middleNodes;
    }
    public int getNoOfPipes(){
        return noOfPipes;
    }
    public String[] getGraph(){
        return graph;
    }
    public int getStartTime(){
        return startTime;
    }
}

class Node{
    private String state;
    private ArrayList<String> parent;
    private int cost;

    Node(){
        state = "";
        parent = new ArrayList<String>();
        cost = 0;
    }

    public void setState(String temp){
        state = temp;
    }
    public String getState(){
        return state;
    }
    public void setParent(String temp){
        parent.add(temp);
    }
    public ArrayList<String> getParent(){
        return parent;
    }
    public void setCost(int temp){
        cost = temp;
    }
    public int getCost(){
        return cost;
    }

    public static void swap(Node node1, Node node2){
        String state;
        ArrayList<String> parent;
        int cost;

        state = node1.getState();
        node1.setState(node2.getState());
        node2.setState(state);

        cost = node1.getCost();
        node1.setCost(node2.getCost());
        node2.setCost(cost);

        parent = node1.getParent();
        for(int i=0; i<node1.getParent().size(); ++i){
            node1.getParent().remove(i);
        }
        for(int i=0; i<node2.getParent().size(); ++i){
            node1.setParent(node2.getParent().get(i));
            node2.getParent().remove(i);
        }
        for(int i=0; i<parent.size(); ++i){
            node2.setParent(parent.get(i));
        }
    }
}

class StateComparator implements Comparator<Node> {
    @Override
    public int compare(Node node1, Node node2) {
        return node1.getState().compareTo(node2.getState());
    }
}

class CostComparator implements Comparator<Node>{
    @Override
    public int compare(Node node1, Node node2){
        return ((Integer)node1.getCost()).compareTo(node2.getCost());
    }
}

public class waterFlow {
    private static int noOfTestCases;

    public static void main(String args[]) throws Exception{
        //get the no. of test cases
        File inputFile = new File(args[1]);
        BufferedReader br = null;
        br = new BufferedReader(new FileReader(inputFile));
        noOfTestCases = Integer.parseInt(br.readLine());

        File outputFile = new File("output.txt");
        BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));

        //get input
        Input[] input = new Input[noOfTestCases];
        for(int i=0; i<noOfTestCases; ++i){
            input[i] = new Input();
            input[i].setTask(br.readLine());
            input[i].setSource(br.readLine());
            input[i].setDestination(br.readLine());
            input[i].setMiddleNodes(br.readLine());
            int tempNoOfPipes = Integer.parseInt(br.readLine());
            input[i].setNoOfPipes(tempNoOfPipes);
            String[] tempGraph = new String[tempNoOfPipes];
            for (int j=0; j<tempNoOfPipes; ++j) {
                tempGraph[j] = br.readLine();
            }
            input[i].setGraph(tempGraph);
            input[i].setStartTime(Integer.parseInt(br.readLine()));
            br.readLine();
        }

       //process input
        for(int i=0; i<noOfTestCases; ++i){
            if(input[i].getTask().equals("BFS")){
                ArrayList<Node> nodes = initializeNodes(input[i]);
                BFS(input[i], nodes, bw);
            }
            else if(input[i].getTask().equals("DFS")){
                ArrayList<Node> nodes = initializeNodes(input[i]);
                DFS(input[i], nodes, bw);
            }
            else if(input[i].getTask().equals("UCS")){
                ArrayList<Node> nodes = initializeNodes(input[i]);
                UCS(input[i], nodes, bw);
            }
        }
        br.close();
        bw.close();
    }

    private static ArrayList<Node> initializeNodes(Input input){
        ArrayList<Node> result = new ArrayList<Node>();

        Node tempNode = new Node();
        tempNode.setState(input.getSource());
        tempNode.setParent(" ");            
        tempNode.setCost(input.getStartTime());
        result.add(tempNode);

        for(String iterator : input.getDestination().split(" ")){
            Node tempNode1 = new Node();
            tempNode1 = checkGraphForParent(iterator, input.getGraph());
            result.add(tempNode1);
        }
        for(String iterator : input.getMiddleNodes().split(" ")){
            Node tempNode2 = new Node();
            tempNode2 = checkGraphForParent(iterator, input.getGraph());
            result.add(tempNode2);
        }

        return result;
    }

    private static Node checkGraphForParent(String iterator, String[] graph){
        Node result = new Node();
        result.setState(iterator);
        for(int i=0; i<graph.length; ++i){
            int count = 0;
            String from = "";
            String to = "";
            String cost = "";
            for(String x : graph[i].split(" ")){
                if(count == 0){
                    from = x;
                    ++count;
                }
                else if(count == 1){
                    to = x;
                    ++count;
                }
                else if(count == 2){
                    cost = x;
                    ++count;
                }
                else{
                    break;
                }
            }
            if(to.equals(iterator)){
                result.setParent(from);
            }
        }
        return result;
    }

    private static void BFS(Input input, ArrayList<Node> nodes, BufferedWriter bw) throws Exception{        
        if(goalCheck(nodes.get(0).getState(), input)){
            bw.write(nodes.get(0).getState() + " " + (nodes.get(0).getCost()%24));
            bw.write("\n");
            return;
        }
        ArrayDeque<Node> frontier = new ArrayDeque<Node>();
        frontier.addLast(nodes.get(0));
        Set<Node> exploredSet = new HashSet<>();
        while(true){
            if(frontier.isEmpty()){
                bw.write("None");
                bw.write("\n");
                return;
            }
            Node tempNode = new Node();
            tempNode = frontier.removeFirst();
            if(goalCheck(tempNode.getState(), input)){
                bw.write(tempNode.getState() + " " + (tempNode.getCost()%24));
                bw.write("\n");
                return;
            }
            exploredSet.add(tempNode);
            ArrayList<Node> childNodes = new ArrayList<Node>();
            for(Node node : nodes){
                if(node.getParent().contains(tempNode.getState())){
                    childNodes.add(node);
                }
            }
            //sort childnodes
            Collections.sort(childNodes, new StateComparator());
            for(Node node : childNodes){
                if(!(frontier.contains(node)) && !(exploredSet.contains(node))){
                    node.setCost(tempNode.getCost() + 1);
                    frontier.addLast(node);
                }
            }
        }
    }

    private static boolean goalCheck(String temp, Input input){
        String destination = input.getDestination();
        for(String iterator : destination.split(" ")){
            if(iterator.equals(temp)){
                return true;
            }
        }
        return false;
    }

    private static void DFS(Input input, ArrayList<Node> nodes, BufferedWriter bw) throws Exception{
        if(goalCheck(nodes.get(0).getState(), input)){
            bw.write(nodes.get(0).getState() + " " + (nodes.get(0).getCost()%24));
            bw.write("\n");
            return;
        }
        ArrayDeque<Node> frontier = new ArrayDeque<Node>();
        frontier.addFirst(nodes.get(0));
        Set<Node> exploredSet = new HashSet<>();
        while(true){
            if(frontier.isEmpty()){
                bw.write("None");
                bw.write("\n");
                return;
            }
            Node tempNode = new Node();
            tempNode = frontier.removeFirst();
            if(goalCheck(tempNode.getState(), input)){
                bw.write(tempNode.getState() + " " + (tempNode.getCost()%24));
                bw.write("\n");
                return;
            }
            exploredSet.add(tempNode);
            ArrayList<Node> childNodes = new ArrayList<Node>();
            for(Node node : nodes){
                if(node.getParent().contains(tempNode.getState())){
                    childNodes.add(node);
                }
            }
            //sort childnodes
            Collections.sort(childNodes, new StateComparator());
            for(int i=childNodes.size()-1; i>=0; --i){
                Node node = childNodes.get(i);
                if(!(exploredSet.contains(node))){
                    node.setCost(tempNode.getCost() + 1);
                    frontier.addFirst(node);
                }
            }
        }
    }

    private static void UCS(Input input, ArrayList<Node> nodes, BufferedWriter bw) throws Exception{
        if(goalCheck(nodes.get(0).getState(), input)){
            bw.write(nodes.get(0).getState() + " " + (nodes.get(0).getCost()%24));
            bw.write("\n");
            return;
        }
        ArrayList<Node> frontier = new ArrayList<Node>();
        frontier.add(nodes.get(0));
        ArrayList<Node> exploredSet = new ArrayList<Node>();
        
        while(true){
            if(frontier.isEmpty()){
                bw.write("None");
                bw.write("\n");
                return;
            }
            Node tempNode = new Node();
            Collections.sort(frontier, new CostComparator());
            //if cost equal, sort alphabetically
            for(int i=0; i<frontier.size(); ++i){
                for(int j=i+1; j<frontier.size(); ++j){
                    if(frontier.get(i).getCost() == frontier.get(j).getCost()){
                        if(((frontier.get(i).getState()).compareTo(frontier.get(j).getState())) > 0){
                            Node.swap(frontier.get(i), frontier.get(j));
                        }
                    }
                }
            }

            tempNode = frontier.remove(0);
            if(goalCheck(tempNode.getState(), input)){
                bw.write(tempNode.getState() + " " + (tempNode.getCost()%24));
                bw.write("\n");
                return;
            }
            exploredSet.add(tempNode);
            
            ArrayList<Node> childNodes = new ArrayList<Node>();

            // make a copy of the nodes arraylist as it is affected when changes are made in childNodes (same address)
            ArrayList<Node> copyOfNodes = new ArrayList<Node>();
            for(int i=0; i<nodes.size(); ++i){
                Node copyNode = new Node();
                copyNode.setState(nodes.get(i).getState());  
                copyNode.setCost(nodes.get(i).getCost());  
                for(int j=0; j<nodes.get(i).getParent().size(); ++j){  
                    copyNode.setParent(nodes.get(i).getParent().get(j));   
                }
                copyOfNodes.add(copyNode); 
            }

            for(Node node : copyOfNodes){
                if(node.getParent().contains(tempNode.getState())){
                    //set the cost for the node as node cost + parent cost
                    setCostForUCSNode(node, tempNode, input);
                    if(checkForOffTimes(input, tempNode, node)){
                        childNodes.add(node);
                    }
                }
            }

         for(Node node : childNodes){
            boolean present = false;
            for(int i=0; i<frontier.size(); ++i){
                if(frontier.get(i).getState().equals(node.getState())){
                    present = true;
                    if(node.getCost() < frontier.get(i).getCost()){
                        frontier.remove(i);
                        frontier.add(node);
                    }
                }
            }
            if(!present){
                for(int i=0; i<exploredSet.size(); ++i){
                    if(exploredSet.get(i).getState().equals(node.getState())){
                        present = true;                        
                    }
                }
            }
            if(!present){
                frontier.add(node);
            }
        }
    }  
}   

private static void setCostForUCSNode(Node node, Node tempNode, Input input){
    for(String line : input.getGraph()){
        String[] pipe = line.split(" ");
        if(pipe[0].equals(tempNode.getState()) && pipe[1].equals(node.getState())){
            node.setCost(Integer.parseInt(pipe[2]) + tempNode.getCost());
        }
    }   
}

private static boolean checkForOffTimes(Input input, Node tempNode, Node childNode){
    String[] graph = input.getGraph();
    for(int i=0; i<graph.length; ++i){
        String[] pipe = graph[i].split(" ");
        if(pipe[0].equals(tempNode.getState()) && pipe[1].equals(childNode.getState())){
            if(!(pipe[3].equals("0"))){
                ArrayList<String> intervals = new ArrayList<String>();
                for(int j=4; j<pipe.length; ++j){
                    intervals.add(pipe[j]);
                }
                for(int j=0; j<intervals.size(); ++j){
                    String[] offsets = intervals.get(j).split("-");
                    if((tempNode.getCost()%24) >= Integer.parseInt(offsets[0]) && (tempNode.getCost()%24) <= Integer.parseInt(offsets[1])){
                        return false;
                    }
                }
            }
        }
    }
    return true;
}
}