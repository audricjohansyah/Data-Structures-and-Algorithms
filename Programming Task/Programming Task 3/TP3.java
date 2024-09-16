import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;

public class TP3 {
    public static InputReader in;
    public static PrintWriter out;
    public static Graph graph;
    public static ArrayList<Vertex> vertexList = new ArrayList<>();;
    public static ArrayList<Vertex> treasureRoomList = new ArrayList<>();;
    public static int roomID = 1;

    public static void main(String[] args) {
        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);

        int vertex = in.nextInt();
        graph = new Graph(vertex); // buat graph

        int edges = in.nextInt();
        graph.setEdges(edges); // set jumlah edge untuk graph


        // masukkan jenis (Treasure / Normal) vertex ke list vertex
        for (int i = 0; i < vertex; i++) {
            char type = in.nextChar();
            Vertex v = new Vertex(0, 0);
            v.setID(roomID);
            if (type == 'N') {
                v.setType("N");
                vertexList.add(v);
            } else if (type == 'S') {
                v.setType("S");
                vertexList.add(v);
                treasureRoomList.add(v);
            }
            roomID++;
        }

        // Set list semua vertex dan semua vertex treasure
        graph.setVerticesList(vertexList);
        graph.setTreasureRooms(treasureRoomList);

        // Add edge ke adjacency list
        for (int j = 0; j < edges; j++) {
            int to = in.nextInt();
            int from = in.nextInt();
            long weight = in.nextLong();
            graph.addEdge(to, from, weight);
            // out.println(graph.adjacencyList);
            // out.println("");
        }

        // Precompute untuk query S
        for (int k = 0; k < graph.treasureRooms.size(); k++){
            ArrayList<Long> temp = graph.dijkstra(graph.treasureRooms.get(k).id, Long.MAX_VALUE);
            graph.cacheQueryS.add(temp);
        }

        // Precompute untuk query M
        graph.cacheQueryM = graph.dijkstra(1, Long.MAX_VALUE);
        
        // System.out.println(graph.cacheQueryS);

        // DEBUG GRAPH
        // graph.outputGraph();
        // System.out.println(" ");
        // graph.stringGraph();
        // System.out.println(graph.adjacencyList);

        // DEBUG BFS (M)
        // ArrayList<Integer> vertexTraversed = graph.BFS(1, 17);
        // System.out.println(vertexTraversed);
        // graph.countTreasureRoomVisited(vertexTraversed);

        // DEBUG DIJKSTRA (S)
        // ArrayList<Integer> shortestDistances = graph.dijkstra(1, Integer.MAX_VALUE);
        // for (int i = 0; i < shortestDistances.size(); i++) {
        // System.out.println("Shortest distance from source to vertex " + (i+1) + ": "
        // + shortestDistances.get(i));
        // }

        int commandAmount = in.nextInt();
        for (int k = 0; k < commandAmount; k++) {
            char command = in.nextChar();
            switch (command) {
                case 'M':
                    long amountOfPeople = in.nextLong();
                    M(amountOfPeople);
                    break;

                case 'S':
                    int start = in.nextInt();
                    S(start);
                    break;

                case 'T':
                    int source = in.nextInt();
                    int middle = in.nextInt();
                    int destination = in.nextInt();
                    long groupSize = in.nextLong();
                    T(source, middle, destination, groupSize);
                    break;

            }
        }
        out.close();
    }

    public static void M(long amountOfPeople) {
        int res = graph.treasureRoomVisited(amountOfPeople);
        out.println(res);
    }

    public static void S(int start) {
        long res = Long.MAX_VALUE;
        for(int i = 0; i < graph.cacheQueryS.size(); i++){
            ArrayList<Long> tmp = graph.cacheQueryS.get(i);
            long minPeople = tmp.get(start-1);
            if(res > minPeople){
                res = minPeople;
            }
        }
        out.println(res);
    }

    public static void T(int source, int middle, int destination, long groupSize) {
        ArrayList<Integer> vertexTraversed = graph.BFS(source, groupSize);
        // System.out.println("Source " + source + " " + vertexTraversed);
        if (!vertexTraversed.contains(middle)) { // CEK DARI SOURCE BS KE KE MIDDLE ATAU GA
            out.println("N");
        } else {
            ArrayList<Integer> middleVertexTraversed = graph.BFS(middle, groupSize);
            // System.out.println("Middle " + middle + " " + middleVertexTraversed);
            if (!middleVertexTraversed.contains(destination)) { // CEK DARI MIDDLE BS KE END ATAU GA
                out.println("H");
            } else if (vertexTraversed.contains(middle) && middleVertexTraversed.contains(destination)) {
                out.println("Y");
            }
        }
    }

    static class InputReader {
        public BufferedReader reader;
        public StringTokenizer tokenizer;

        public InputReader(InputStream stream) {
            reader = new BufferedReader(new InputStreamReader(stream), 32768);
            tokenizer = null;
        }

        public String next() {
            while (tokenizer == null || !tokenizer.hasMoreTokens()) {
                try {
                    tokenizer = new StringTokenizer(reader.readLine());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return tokenizer.nextToken();
        }

        public int nextInt() {
            return Integer.parseInt(next());
        }

        public long nextLong() {
            return Long.parseLong(next());
        }

        public char nextChar() {
            return next().charAt(0);
        }
    }
}

class Graph {
    public int vertex;
    public ArrayList<ArrayList<Edge>> adjacencyList = new ArrayList<>();
    public int edges;
    public ArrayList<Vertex> vertices = new ArrayList<>();
    public ArrayList<Vertex> treasureRooms = new ArrayList<>();
    public ArrayList<ArrayList<Integer>> dijkstraResults = new ArrayList<>();
    public ArrayList<ArrayList<Long>> cacheQueryS = new ArrayList<>();
    public ArrayList<Long> cacheQueryM = new ArrayList<>();


    public Graph(int vertex) {
        this.vertex = vertex;
        for (int i = 0; i < vertex; i++) { // N ARRAY DI DALAM 1 ARRAY, N = JLH VERTEX
            this.adjacencyList.add(new ArrayList<>());
            this.dijkstraResults.add(new ArrayList<>());
        }
    }

    public void setEdges(int e) {
        this.edges = e;
    }

    public void setVerticesList(ArrayList<Vertex> vertexList) {
        this.vertices = vertexList;
    }

    public void setTreasureRooms(ArrayList<Vertex> treasureRooms) {
        this.treasureRooms = treasureRooms;
    }

    public void addEdge(int from, int to, long weight) {
        // ACCESS INDEX HARUS DIKURANGIN -1 JANGAN LUPA
        adjacencyList.get(from - 1).add(new Edge(to, weight));
        adjacencyList.get(to - 1).add(new Edge(from, weight));
    }

    // https://www.programiz.com/dsa/graph-bfs
    public ArrayList<Integer> BFS(int source, long amountOfPeople) {
        ArrayList<Integer> result = new ArrayList<>(); // RESULT TRAVERSAL GRAPH SECARA BFS
        boolean[] visited = new boolean[vertex]; // VISITED ARRAY, INSIALISASI FALSE SMUA DI AWAL
        LinkedList<Integer> queue = new LinkedList<>(); // QUEUE UNTUK TETANGGA ARRAY YANG UDAH DIVISIT

        visited[source - 1] = true; // INISIALISASI STARTING POINT JADI TRUE
        queue.add(source); // MASUKIN VERTEX START KE QUEUE

        while (!queue.isEmpty()) {
            int currentVertex = queue.poll(); // DEQUEUE
            result.add(currentVertex); // MASUKIN KE RESULT

            for (Edge edge : adjacencyList.get(currentVertex - 1)) { // LOOP ADJACENCY LIST
                int neighbor = edge.to; // TETANGGANYA
                long requiredPeople = edge.weight; // WEIGHT OF EDGENY

                if (!visited[neighbor - 1] && amountOfPeople >= requiredPeople) { // JIKA BELUM DIVISIT DAN AMOUNT OF
                                                                                  // PEOPLE CUKUP UNTUK MELINTAS
                    visited[neighbor - 1] = true; // VISIT JADI TRUE
                    queue.add(neighbor); // MASUKIN NEIGHBOUR KE QUEUE
                }
            }
        }

        return result;
    }

    public int treasureRoomVisited(long amountOfPeople) {
       int res = 0;
        for(int i = 0; i < treasureRooms.size(); i++){
            if(amountOfPeople >= cacheQueryM.get((treasureRooms.get(i).id) - 1) ){
                res++;
            }
        }
        return res;
    }


    public int countTreasureRoomVisited(ArrayList<Integer> verticesList) {
        // ArrayList<Integer> treasureRoomVisited = new ArrayList<>();
        int counter = 0;
        for (int i = 0; i < verticesList.size(); i++) {
            if (vertices.get(verticesList.get(i) - 1).type == "S") {
                // treasureRoomVisited.add(vertices.get(verticesList.get(i) - 1).id);
                counter++;
            }
        }
        // System.out.println("ROOMS: " + treasureRoomVisited);
        return counter;
    }

    public void djikstraOutput(ArrayList<Long> shortestDistances) {
        for (int i = 0; i < shortestDistances.size(); i++) {
            System.out.println("Shortest distance from source to vertex " + (i + 1) + ": " + shortestDistances.get(i));
        }
    }

    private int minDistance(long dist[], boolean sptSet[]) {
        long min = Long.MAX_VALUE;
        int min_index = -1;

        for (int v = 0; v < vertex; v++)
            if (!sptSet[v] && dist[v] <= min) {
                min = dist[v];
                min_index = v;
            }

        return min_index;
    }

    // https://www.geeksforgeeks.org/dijkstras-shortest-path-algorithm-greedy-algo-7/
    public ArrayList<Long> dijkstra(int source, long amountOfPeople) {
        ArrayList<Long> result = new ArrayList<>(); // ARRAY RESULT
        long[] dist = new long[vertex]; // ARRAY UNTUK DISTANCE (0-INDEXED)
        boolean[] sptSet = new boolean[vertex]; // ARRAY UNTUK KEEP TRACK VERTEX UNTUK SHORTEST PATH, (0-INDEXED)

        for (int i = 0; i < vertex; i++) {
            dist[i] = Long.MAX_VALUE; // INISIALISASI JADI INFINTE / MAX VAL
            sptSet[i] = false; // INISIALISASI SMUA FALSE
        }

        dist[source - 1] = 0; // STARTING POINT

        for (int i = 0; i < vertex - 1; i++) { // ITERASI VERTEX SISANYA
            int u = minDistance(dist, sptSet); // CARI VERTEX DENGAN DISTANCE MINIMUM DARI START YANG BELOM ADA DI
                                               // SPTSET
            sptSet[u] = true;

            for (Edge edge : adjacencyList.get(u)) { // ITERASI NEIGHBOURNY
                int neighbor = edge.to;
                long requiredPeople = edge.weight;

                if (!sptSet[neighbor - 1] && amountOfPeople >= requiredPeople &&
                        dist[u] != Long.MAX_VALUE && requiredPeople < dist[neighbor - 1]) {
                            if(dist[u] > requiredPeople){
                                dist[neighbor - 1] = dist[u];
                            }
                            else{
                                dist[neighbor - 1] = requiredPeople;
                            }
                    // dist[neighbor - 1] = (int) (dist[u] + edge.weight); // UPDATE DISTANCE
                }
            }
        }

        for (int i = 0; i < vertex; i++) {
            result.add(dist[i]); // MASUKIN KE RESULT
        }
        return result;
    }

    public int minPersonRequiredDijkstra(ArrayList<Integer> shortestDistances) {
        ArrayList<Integer> distances = new ArrayList<>();
        for (int i = 0; i < treasureRooms.size(); i++) {
            int roomNumber = treasureRooms.get(i).id;
            // System.out.println("Shortest distance from source to vertex " +
            // treasureRooms.get(i).id + ": "
            // + shortestDistances.get(roomNumber - 1));
            distances.add(shortestDistances.get(roomNumber - 1));
        }

        // System.out.println("ARRAY OF SHORTEST DISTANCES TO TREASURE ROOM: " +
        // distances);
        return Collections.min(distances);
    }

    public void outputGraph() {
        for (int i = 0; i < vertex; i++) {
            for (int j = 0; j < adjacencyList.get(i).size(); j++) {
                System.out.println("Vertex-" + (i + 1) + " is connected to " +
                        adjacencyList.get(i).get(j).to + " with weight " + adjacencyList.get(i).get(j).weight);
            }
        }
    }

    public void stringGraph() {
        for (int t = 0; t < adjacencyList.size(); t++) {
            String res = adjacencyList.get(t).toString();
            String result = res.replace("[", "").replace("]", "");
            System.out.println("Vertex-" + (t + 1) + " = " + result);
        }
    }
}

class Vertex implements Comparable<Vertex> {
    public int source, id;
    public long people;
    public String type;

    public Vertex(int source, long people) {
        this.source = source;
        this.people = people;
    }

    public void setID(int id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String toString() {
        return "| ROOM ID: " + String.valueOf(id) + " , TYPE: " + (type == "N" ? "NORMAL" : "TREASURE") + " |";
    }

    @Override
    public int compareTo(Vertex h) {
        return (int) (this.people - h.people);
    }
}

class Edge {
    public int to;
    public long weight;

    public Edge(int to, long weight) {
        this.to = to;
        this.weight = weight;
    }

    public String toString() {
        return "| CONNECTED TO: " + String.valueOf(to) + ", WEIGHT: " + String.valueOf(weight) + " |";
    }
}