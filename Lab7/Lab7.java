package Lab7;
import java.util.*;
import java.io.*;

public class Lab7 {

    static class Box {
        int id;
        long value;
        String state;

        Box(int id, long value, String state) {
            this.id = id;
            this.value = value;
            this.state = state;
        }
    }

    static class BoxContainer {
        public ArrayList<Box> heap;
        public HashMap<Integer, Integer> idToIndexMap;
        public int size;

        public BoxContainer() {
            this.heap = new ArrayList<>();
            this.idToIndexMap = new HashMap<>();
        }

        public static int getParentIndex(int i) {
            return (i - 1) / 2;
        }

        public static int leftIndex(int i) {
            return 2 * i + 1;
        }

        public static int rightIndex(int i) {
            return 2 * i + 2;
        }

        public void percolateUp(int i) {
            while (i > 0 && compare(heap.get(i), heap.get(getParentIndex(i)))) {
                swap(i, getParentIndex(i));
                i = getParentIndex(i);
            }
        }
        
        public void percolateDown(int i) {
            int left = leftIndex(i);
            int right = rightIndex(i);
            int max = i;
        
            if (left < size && compare(heap.get(left), heap.get(max))) {
                max = left;
            }
        
            if (right < size && compare(heap.get(right), heap.get(max))) {
                max = right;
            }
        
            if (max != i) {
                swap(i, max);
                percolateDown(max);
            }
        }

        private boolean compare(Box a, Box b) {
            if (a.value == b.value) {
                return a.id < b.id;
            } 
            return a.value > b.value; 
        }

        public void insert(Box box) {
            heap.add(box);
            int index = size;
            idToIndexMap.put(box.id, index);
            size++;
            percolateUp(index);
        }

        public Box peek() {
            return heap.get(0);
        }

        public void swap(int firstIndex, int secondIndex) {
            Box temp = heap.get(firstIndex);
            heap.set(firstIndex, heap.get(secondIndex));
            heap.set(secondIndex, temp);
            
            idToIndexMap.put(heap.get(firstIndex).id, firstIndex);
            idToIndexMap.put(heap.get(secondIndex).id, secondIndex);
        }

        public void updateBox(Box box, long newValue) {
            int index = idToIndexMap.get(box.id);
            box.value = newValue;
        
            percolateUp(index);
            percolateDown(index);
        }

        public void D(Box box1, Box box2){
            if (!box1.state.equals(box2.state)) {
                if ((box1.state.equals("R") && box2.state.equals("S")) ||
                    (box1.state.equals("P") && box2.state.equals("R")) ||
                    (box1.state.equals("S") && box2.state.equals("P"))) {
                    box1.value += box2.value;
                    box2.value /= 2;
                } else {
                    box2.value += box1.value;
                    box1.value /= 2;
                }
                updateBox(box1, box1.value);
                updateBox(box2, box2.value);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter pw = new PrintWriter(System.out);
    
        int N = Integer.parseInt(br.readLine());
    
        ArrayList<Box> boxes = new ArrayList<>();
        BoxContainer boxContainer = new BoxContainer();
    
        for (int i = 0; i < N; i++) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            long value = Long.parseLong(st.nextToken());
            String state = st.nextToken();
    
            Box box = new Box(boxes.size(), value, state);
            boxes.add(box);
            boxContainer.insert(box);
        }
    
        int T = Integer.parseInt(br.readLine());
    
        for (int i = 0; i < T; i++) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            String command = st.nextToken();
    
            if ("A".equals(command)) {
                long value = Long.parseLong(st.nextToken());
                String state = st.nextToken();
                Box boxBaru = new Box(boxes.size(), value, state);
                boxes.add(boxBaru);
                boxContainer.insert(boxBaru);
            } else if ("D".equals(command)) {
                int id1 = Integer.parseInt(st.nextToken());
                int id2 = Integer.parseInt(st.nextToken());
            
                Box box1 = boxes.get(id1);
                Box box2 = boxes.get(id2);

                boxContainer.D(box1, box2);
            
            } else if ("N".equals(command)) {
                int id = Integer.parseInt(st.nextToken());
                Box box = boxes.get(id);
                
                for (int neighborId : new int[] {id - 1, id + 1}) {
                    if (neighborId >= 0 && neighborId < boxes.size()) {
                        Box neighborBox = boxes.get(neighborId);
            
                        if (!box.state.equals(neighborBox.state)) {
                            if ((box.state.equals("R") && neighborBox.state.equals("S")) ||
                                (box.state.equals("P") && neighborBox.state.equals("R")) ||
                                (box.state.equals("S") && neighborBox.state.equals("P"))) {
                                box.value += neighborBox.value;
                                boxContainer.updateBox(box, box.value);
                            }
                        }
                    }
                }
               
            }
            Box topBox = boxContainer.peek();
            pw.println(topBox.value + " " + topBox.state);
        }
    
        pw.flush();
        pw.close();
    }    
}