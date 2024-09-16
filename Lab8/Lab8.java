package Lab8;

import java.io.*;
import java.util.*;

class Graph {
    public int Node;
    public ArrayList<ArrayDeque<Edge>> al = new ArrayList<>();

    public Graph(int node) {
        this.Node = node;
        for (int i = 0; i <= node; i++) {
            this.al.add(new ArrayDeque<>());
        }
    }

    public void addEdge(int from, int to, long weight) {
        this.al.get(from).add(new Edge(to, weight));
        this.al.get(to).add(new Edge(from, weight));
    }

    public ArrayList<Long> shortest(int source) {
        if (source == 0) {
            return null;
        }
        ArrayList<Long> distance = new ArrayList<>();
        for (int i = 0; i <= Node; i++) {
            distance.add(Long.MAX_VALUE);
        }

        distance.set(source, (long) 0);
        PriorityQueue<Compare> queue = new PriorityQueue<>();
        queue.add(new Compare(source, 0));

        while (!queue.isEmpty()) {
            Compare current = queue.poll();
            int vertex = current.source;
            long w = current.o2;

            if (w > distance.get(vertex))
                continue;

            for (Edge edge : this.al.get(vertex)) {
                int u = edge.to;
                long weight = edge.weight;

                if (distance.get(vertex) + weight < distance.get(u)) {
                    distance.set(u, distance.get(vertex) + weight);
                    queue.add(new Compare(u, distance.get(vertex) + weight));
                }
            }
        }
        return distance;
    }
}

class Edge {
    int to;
    long weight;

    public Edge(int to, long weight) {
        this.to = to;
        this.weight = weight;
    }
}

class Node {
    int id;
    long weight;

    public Node(int id, long weight) {
        this.id = id;
        this.weight = weight;
    }
}

class Compare implements Comparable<Compare> {
    int source;
    long o2;

    public Compare(int source, long o2) {
        this.source = source;
        this.o2 = o2;
    }

    @Override
    public int compareTo(Compare o) {
        return (int) (this.o2 - o.o2);
    }
}

public class Lab8 {
    private static InputReader in;
    private static PrintWriter out;

    public static void main(String[] args) {
        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);

        int N = in.nextInt();
        int E = in.nextInt();
        Graph graph = new Graph(N);

        for (int i = 0; i < E; i++) {
            int A = in.nextInt();
            int B = in.nextInt();
            long W = in.nextLong();
            graph.addEdge(A, B, W);

        }

        int H = in.nextInt();
        ArrayList<Integer> treasureNodes = new ArrayList<Integer>();
        ArrayList<ArrayList<Long>> oxygen = new ArrayList<>();

        for (int i = 0; i < H; i++) {
            int K = in.nextInt();
            treasureNodes.add(K);
        }

        for (int v : treasureNodes) {
            oxygen.add(graph.shortest(v));
        }

        treasureNodes.add(1);
        oxygen.add(graph.shortest(1));
        int Q = in.nextInt();
        int O = in.nextInt();

        while (Q-- > 0) {
            long totalOxygenNeeded = 0;
            int T = in.nextInt();
            int davePosition = 1;
          
            while (T-- > 0) {
                int D = in.nextInt();
                totalOxygenNeeded += oxygen.get(treasureNodes.indexOf(davePosition)).get(D);
                davePosition = D;
            }

            totalOxygenNeeded += oxygen.get(treasureNodes.indexOf(davePosition)).get(1);
            out.println(totalOxygenNeeded < O ? 1 : 0);
        }

        out.close();
    }

    // taken from https://codeforces.com/submissions/Petr
    // together with PrintWriter, these input-output (IO) is much faster than the
    // usual Scanner(System.in) and System.out
    // please use these classes to avoid your fast algorithm gets Time Limit
    // Exceeded caused by slow input-output (IO)
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
    }
}
