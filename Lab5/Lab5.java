package Lab5;
import java.io.*;
import java.util.StringTokenizer;

public class Lab5 {

    private static InputReader in;
    private static PrintWriter out;
    private static DoublyLinkedList rooms = new DoublyLinkedList();

    public static void main(String[] args) {
        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);

        int N = in.nextInt();

        for (int i = 0; i < N; i++) {
            char command = in.nextChar();
            char direction;

            switch (command) {
                case 'A':
                    direction = in.nextChar();
                    char type = in.nextChar();
                    add(type, direction);
                    break;
                case 'D':
                    direction = in.nextChar();
                    out.println(delete(direction));
                    break;
                case 'M':
                    direction = in.nextChar();
                    out.println(move(direction));
                    break;
                case 'J':
                    direction = in.nextChar();
                    out.println(jump(direction));
                    break;
            }
        }

        out.close();
    }

    public static void add(char type, char direction) {
        rooms.add(type, direction);
    }

    public static int delete(char direction) {
        ListNode deletedNode = rooms.delete(direction);
        if (deletedNode != null) {
            return deletedNode.id;
        }
        else{
            return -1;
        }
    }

    public static int move(char direction) {
        ListNode movedNode = rooms.move(direction);
        if (movedNode != null) {
            return movedNode.id;
        }
        else{
            return -1;
        }
    }

    public static int jump(char direction) {
        if ((char) rooms.current.element == 'C') {
            return -1;
        }
        else{
            ListNode jumpedNode = rooms.jump(direction);
            if (jumpedNode != null) {
                return jumpedNode.id;
            }
            return -1;
        }
    }

    private static class InputReader {
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

        public char nextChar() {
            return next().charAt(0);
        }

        public int nextInt() {
            return Integer.parseInt(next());
        }
    }
}

class DoublyLinkedList {
    private int nodeIdCounter = 0;
    ListNode first;
    ListNode current;
    ListNode last;
    int size = 0;

    public ListNode add(Object element, char direction) {
        nodeIdCounter++;
        ListNode tmp = new ListNode(element, nodeIdCounter);

        if (size == 0) {
            first = tmp;
            last = tmp;
            current = tmp;
            tmp.next = tmp;
            tmp.prev = tmp;

        } 
        else {
            if (direction == 'R') {
                tmp.prev = current;
                tmp.next = current.next;

                current.next.prev = tmp;
                current.next = tmp;
            } 
            
            else if(direction == 'L'){
                tmp.next = current;
                tmp.prev = current.prev;
                
                current.prev.next = tmp;
                current.prev = tmp;
            }
        }
        size++;
        return tmp;
    }

    public ListNode delete(char direction) {
        if (size == 0 || current == null || size < 2) {
            return null;
        }
    
        ListNode tmp;
        if (direction == 'R') {
            tmp = current.next;
            if (tmp != current) {
                current.next = tmp.next;
                tmp.next.prev = current;
                if (tmp == last) {
                    last = current;
                }
            } 
            
            else {
                current = null;
                first = null;
                last = null;
            }
        } 
        else {
            tmp = current.prev;

            if (tmp != current) {
                current.prev = tmp.prev;
                tmp.prev.next = current;
                if (tmp == first) {
                    first = current;
                }
            } 
            
            else {
                current = null;
                first = null;
                last = null;
            }
        }
        size--;
        return tmp;
    }

    public ListNode move(char direction) {
        if (size == 0 || current == null) {
            return null;
        }
        else{
            if (direction == 'R') {
                current = current.next;
            } 
            else {
                current = current.prev;
            }
        }
        return current;
    }

    public ListNode jump(char direction) {
       ListNode tmp = current;
        if (direction == 'R') {
            do {
                tmp = tmp.next;
            } while (tmp != current && (char)tmp.element != 'S');
        } 
        else {
            do {
                tmp = tmp.prev;
            } while (tmp != current && (char)tmp.element != 'S');
        }
        if ((char)tmp.element == 'S') {
            current = tmp;
            return tmp;
        }
        return null;
    }
}

class ListNode {
    Object element;
    ListNode next;
    ListNode prev;
    int id;

    ListNode(Object element, int id) {
        this.element = element;
        this.id = id;
    }

    public String toString() {
        return String.format("(ID:%d Elem:%s)", id, element);
    }
}