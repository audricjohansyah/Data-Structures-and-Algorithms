import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.lang.Math;

public class TP2 {
    private static InputReader in;
    private static PrintWriter out;
    public static DoublyLinkedList classrooms = new DoublyLinkedList();
    private static int studentID = 1;
    public static int classID = 1;

    public static void main(String[] args) {
        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);

        int studentAmount = 0;
        int classAmount = in.nextInt(); // set up kelas

        for(int i = 0; i < classAmount; i++){
            int cap = in.nextInt();
            studentAmount += cap;
            classrooms.add(cap, classID, new AVLTree()); // masukin kelas ke doublylinkedlist
            classID++;
        }

        ListNode node = classrooms.current;
        for(int j = 0; j < studentAmount; j++){ // set up students tiap kelas (sesuai dgn kapasitas)
            int scores = in.nextInt();
            if(node.capacityCheck != 0){
                classrooms.insertStudent(node, scores, studentID, 0);
                node.capacityCheck--;
                studentID++;
                // classrooms.countClassAverageScore(node);
            }
           else{
                node = node.next;  // kalo kapasitas kelas (pointer current) udah abis, pindahin pointer current ke kelas selanjutnya
                if(node == classrooms.first){ // kalo pointer balik ke awal berarti student gaad yg bs masuk kelas lg
                    break;
                }
                else{
                    classrooms.insertStudent(node, scores, studentID, 0);
                    node.capacityCheck--;
                    studentID++;
                    // classrooms.countClassAverageScore(node);
                }
            }
        }
        classrooms.allClassAverageScore();

        // classrooms.display();
        // classrooms.resetCurrent();

        int commandAmount = in.nextInt();
        for (int k = 0; k < commandAmount; k++){
            String command = in.next();
            switch (command) {
                case "T":
                    T();
                    break;
                case "C":
                    C();
                    break;
                case "G":
                    G();
                    break;
                case "S":
                    S();
                    break;
                case "K":
                    K();
                    break;
                case "A":
                    A();
                    break;
                default:
                    break;
            }
        }

        out.close();
    }
    
    public static void T(){
        int score = in.nextInt();
        int studentid = in.nextInt();
        ListNode nodeNow = classrooms.current;
        AVLTree studentTree = classrooms.current.students;


        int studentScore = -1;
        for (Map.Entry<Integer, Integer> entry : nodeNow.studentsMap.entrySet()) {
            int id = entry.getKey();
            if(studentid == id){
                studentScore = entry.getValue();
                break;
            }
        }
        
        if(studentScore == -1){
            out.println(-1);
        }
        else{
            Node student = studentTree.search(studentTree.root, studentScore, studentid); // STUDENT FIX GA NULL, FIX ADA

            int extraScore = 0; //HITUNG EXTRA SCORE YG DIDAPETIN STUDENT
            int myScore = nodeNow.studentsMap.get(studentid); // DAPETIN SCORE STUDENT
            for (Map.Entry<Integer, Integer> entry : nodeNow.studentsMap.entrySet()) { 
                int id = entry.getKey(); // DAPETIN ID TEMEN-TEMENNYA
                if(studentid != id){ // KALO KEY DI HASHMAPNYA TIDAK SAMA KYK STUDENT ID BARU DI CEK (KALO SAMA DI SKIP LAH)
                   if(myScore >= entry.getValue()){ // CEK APAKAH SCORE STUDENT LEBIH GEDE SAMA DGN DARIPADA SCORE SETIAP TMN KELASNY
                        extraScore += 1;
                        if(extraScore == score){
                            break;
                        }
                   }
                }
            }

            if(extraScore < score){ //  EXTRA SCORE TIDAK BOLEH LEBIH GEDE DARI SCORE
                score += extraScore;
            }
            else if (extraScore >= score){
                score += score;
            }

            int newScore = student.key + score;
            Node temp = new Node(newScore, student.id, student.cheated);

            classrooms.deleteStudent(nodeNow, student.key, student.id); // DELETE STUDENT DGN KEY LAMA (SCORE BLM UPDATE)
            classrooms.insertStudent(nodeNow, temp.key, temp.id, temp.cheated); // MASUKIN STUDENT DGN KEY BARU (SCORE UPDATED)

            classrooms.countClassAverageScore(nodeNow);

            // classrooms.display();
            out.println(temp.key);
        }
    }

    public static void C(){
        int studentid = in.nextInt();
        ListNode nodeNow = classrooms.current;
        AVLTree studentTree = nodeNow.students;

        int studentScore = -1;
        for (Map.Entry<Integer, Integer> entry : nodeNow.studentsMap.entrySet()) {
            int key = entry.getKey();
            if(studentid == key){
                studentScore = entry.getValue();
                break;
            }
        }

        if(studentScore == -1){
            // classrooms.display();
            out.println(-1);
        }
        else{
            // System.out.println(studentScore);
            // System.out.println(studentid);
            Node student = studentTree.search(studentTree.root, studentScore, studentid); 
            if(student != null){

                student.addCheated(); // CHEAT

                if(student.cheated == 1){
                    Node temp = new Node(0, student.id, student.cheated); //INITIATE STUDENT TEMP BARU YANG ISINYA SAMA KAYAK STUDENY YANG CHEAT, SCORE = 0
                    
                    classrooms.deleteStudent(nodeNow, student.key, student.id);  
                    classrooms.insertStudent(nodeNow, temp.key, temp.id, temp.cheated);// MASUKIN STUDENT TEMP KE KELAS, AGAR BALANCING KEMBALI KRN KEY GANTI VALUE

                    classrooms.countClassAverageScore(nodeNow);

                    // classrooms.display();
                    out.println(temp.key);
                }
                else if(student.cheated == 2){
                    Node temp = new Node(0, student.id, student.cheated); // BIKIN STUDENT YANG MELAKUKAN KECURANGAN 2X
                    classrooms.deleteStudent(nodeNow, student.key, student.id);  // DELETE STUDENT DARI CLASSROOM SRKG

                    if(nodeNow != classrooms.last){ // KALO DIA BUKAN DI KELAS TERBURUK, PINDAHIN KE KELAS TERBURUK
                        if(nodeNow.capacity < 6){ // SETELAH PINDAHIN STUDENT KE KELAS TERBURUK, 
                            // CEK KAPASITAS APAKAH KURANG DARI 6 ATO TIDAK, JIKA IYA DELETE CLASSROOM DAN PINDAHKAN KE KELAS YANG LEBIH BURUK (POSISI DI KANAN)
                            ListNode nextClass = nodeNow.next;
                            for (Map.Entry<Integer, Integer> entry : nodeNow.studentsMap.entrySet()) { 
                                int id = entry.getKey(); // DAPETIN SCORE STUDENT
                                int score = entry.getValue(); // DAPETIN ID STUDENT
                                int cheatcount = studentTree.search(studentTree.root, score, id).cheated;
                                classrooms.insertStudent(nextClass, score, id, cheatcount);
                            }
                            classrooms.move("R"); //PAKCIL PINDAH KANAN
                            classrooms.delete("L"); //DELETE CLASSROOM DI CURRENT.PREV MAKANYA KIRI
                            classrooms.countClassAverageScore(nextClass);
                        }

                        ListNode lastClassroom = classrooms.last; // RANKING TERENDAH LAST CLASSROOM
                        classrooms.insertStudent(lastClassroom, temp.key, temp.id, temp.cheated); // MASUKIN KE KELAS TERBURUK
                        classrooms.countClassAverageScore(lastClassroom);
                    }
                    else if(nodeNow == classrooms.last){ // KALO DIA UDAH DI KELAS TERBURUK, STAY
                        classrooms.insertStudent(nodeNow, temp.key, temp.id, temp.cheated);
                    }
                    
                    classrooms.countClassAverageScore(nodeNow);
                    // classrooms.display();
                    out.println(classrooms.last.id);
                }
                else if(student.cheated == 3){
                    int dropoutStudentId = student.id;
                    classrooms.deleteStudent(nodeNow, student.key, student.id);  

                    if(nodeNow != classrooms.last){ // KALO DIA BUKAN DI KELAS TERBURUK
                        if(nodeNow.capacity < 6){
                            ListNode nextClass = nodeNow.next;
                            for (Map.Entry<Integer, Integer> entry : nodeNow.studentsMap.entrySet()) { 
                                int id = entry.getKey(); // DAPETIN SCORE STUDENT
                                int score = entry.getValue(); // DAPETIN ID STUDENT
                                int cheatcount = nodeNow.students.search(nodeNow.students.root, score, id).cheated;
                                classrooms.insertStudent(nextClass, score, id, cheatcount);
                            }
                            classrooms.move("R"); //PAKCIL PINDAH KANAN
                            classrooms.delete("L"); //DELETE CLASSROOM DI CURRENT.PREV MAKANYA KIRI
                            classrooms.countClassAverageScore(nextClass);
                        }
                    }
                    else if(nodeNow == classrooms.last){ // KALO DIA UDAH DI KELAS TERBURUK, PINDAHIN KE SECOND WORST CLASSROOM
                        if(nodeNow.capacity < 6){
                            ListNode previousClass = nodeNow.prev;
                            for (Map.Entry<Integer, Integer> entry : nodeNow.studentsMap.entrySet()) { 
                                int id = entry.getKey(); // DAPETIN SCORE STUDENT
                                int score = entry.getValue(); // DAPETIN ID STUDENT
                                int cheatcount = nodeNow.students.search(nodeNow.students.root, score, id).cheated;
                                classrooms.insertStudent(previousClass, score, id, cheatcount);
                            }
                            classrooms.move("L"); //PAKCIL PINDAH KIRI (SECOND WORST CLASS)
                            classrooms.delete("R"); //DELETE CLASSROOM DI CURRENT.NEXT MAKANYA KANAN
                            classrooms.countClassAverageScore(previousClass);
                        }
                    }
                    classrooms.countClassAverageScore(nodeNow);
                    out.println(dropoutStudentId);
                }
            }
        }
    }

    public static void G(){
        String direction = in.next();
        ListNode node = classrooms.move(direction);

        if(node != null){
            // System.out.println("ROOM NOW: " + classrooms.current.id);
            out.println(node.id);
        }
        else{
            out.println(-1);
        }
    }

    public static void S(){
        if(classrooms.size == 1){
            out.println("-1 -1");
        }
        else if(classrooms.size > 1){
            ListNode classroomNow = classrooms.current;
            if(classroomNow == classrooms.first || classroomNow == classrooms.last || classrooms.size == 2){ // JIKA KELAS DI KEDUA UJUNG (BEST/WORST) ATAU JLH KELAS = 2
                if(classroomNow == classrooms.last){ // jika dia kelas terburuk
                    // tuker 3 terbaik di kelas ini dengan 3 terburuk di kelas yg lebih pinter
                    // worst classroom
                    Node s1 = classroomNow.students.maxNode(classroomNow.students.root);
                    Node student1 = new Node(s1.key, s1.id, s1.cheated); // INI STUDENT TERBAIK 1
                    classrooms.deleteStudent(classroomNow, s1.key, s1.id);

                    Node s2 = classroomNow.students.maxNode(classroomNow.students.root);
                    Node student2 = new Node(s2.key, s2.id, s2.cheated); // INI STUDENT TERBAIK 2
                    classrooms.deleteStudent(classroomNow, s2.key, s2.id);


                    Node s3 = classroomNow.students.maxNode(classroomNow.students.root);
                    Node student3 = new Node(s3.key, s3.id, s3.cheated); // INI STUDENT TERBAIK 2
                    classrooms.deleteStudent(classroomNow, s3.key, s3.id);
                    
                    // better classroom
                    ListNode betterClassroom = classroomNow.prev;
                    Node betterS1 = betterClassroom.students.minNode(betterClassroom.students.root);
                    Node betterStudent1 = new Node(betterS1.key, betterS1.id, betterS1.cheated); // INI STUDENT TERBURUK 1
                    classrooms.deleteStudent(betterClassroom, betterS1.key, betterS1.id);

                    Node betterS2 = betterClassroom.students.minNode(betterClassroom.students.root);
                    Node betterStudent2 = new Node(betterS2.key, betterS2.id, betterS2.cheated); // INI STUDENT TERBURUK 2
                    classrooms.deleteStudent(betterClassroom, betterS2.key, betterS2.id);


                    Node betterS3 = betterClassroom.students.minNode(betterClassroom.students.root);
                    Node betterStudent3 = new Node(betterS3.key, betterS3.id, betterS3.cheated); // INI STUDENT TERBURUK 3
                    classrooms.deleteStudent(betterClassroom, betterS3.key, betterS3.id);

                    
                    // masukkan 3 terburuk di kelas better ke kelas worse
                    classrooms.insertStudent(classroomNow, betterStudent1.key, betterStudent1.id, betterStudent1.cheated);
                    classrooms.insertStudent(classroomNow, betterStudent2.key, betterStudent2.id, betterStudent2.cheated);
                    classrooms.insertStudent(classroomNow, betterStudent3.key, betterStudent3.id, betterStudent3.cheated);
                

                    // masukkan 3 terbaik di kelas worse ke kelas better
                    classrooms.insertStudent(betterClassroom, student1.key, student1.id, student1.cheated);
                    classrooms.insertStudent(betterClassroom, student2.key, student2.id, student2.cheated);
                    classrooms.insertStudent(betterClassroom, student3.key, student3.id, student3.cheated);

                    classrooms.countClassAverageScore(classroomNow);
                    classrooms.countClassAverageScore(betterClassroom);
                    
                    int bestStudent = classroomNow.students.maxNode(classroomNow.students.root).id;
                    int worstStudent = classroomNow.students.minNode(classroomNow.students.root).id;

                    out.println(String.valueOf(bestStudent) + " " + String.valueOf(worstStudent));

                }
                else{ // jika KELAS terbaik
                    //tuker 3 terburuk dari kelas ini dengan 3 terbaik dari kelas terburuk
                    //better classroom
                    Node s1 = classroomNow.students.minNode(classroomNow.students.root);
                    Node student1 = new Node(s1.key, s1.id, s1.cheated); // INI STUDENT TERBURUK 1 DARI KELAS TERBAIK
                    classrooms.deleteStudent(classroomNow, s1.key, s1.id);


                    Node s2 = classroomNow.students.minNode(classroomNow.students.root);
                    Node student2 = new Node(s2.key, s2.id, s2.cheated); // INI STUDENT TERBURUK 2 DARI KELAS TERBAIK
                    classrooms.deleteStudent(classroomNow, s2.key, s2.id);

                    Node s3 = classroomNow.students.minNode(classroomNow.students.root);
                    Node student3 = new Node(s3.key, s3.id, s3.cheated); // INI STUDENT TERBURUK 3 DARI KELAS TERBAIK
                    classrooms.deleteStudent(classroomNow, s3.key, s3.id);

                    
                    // worse classroom
                    ListNode worseClassroom = classroomNow.next;
                    Node worseS1 = worseClassroom.students.maxNode(worseClassroom.students.root);
                    Node worseStudent1 = new Node(worseS1.key, worseS1.id, worseS1.cheated); // INI STUDENT TERBAIK 1 DARI KELAS TERBURUK
                    classrooms.deleteStudent(worseClassroom, worseS1.key, worseS1.id);

                    Node worseS2 = worseClassroom.students.maxNode(worseClassroom.students.root);
                    Node worseStudent2 = new Node(worseS2.key, worseS2.id, worseS2.cheated); // INI STUDENT TERBAIK 2 DARI KELAS TERBURUK
                    classrooms.deleteStudent(worseClassroom, worseS2.key, worseS2.id);


                    Node worseS3 = worseClassroom.students.maxNode(worseClassroom.students.root);
                    Node worseStudent3 = new Node(worseS3.key, worseS3.id, worseS3.cheated); // INI STUDENT TERBAIK 3 DARI KELAS TERBURUK
                    classrooms.deleteStudent(worseClassroom, worseS3.key, worseS3.id);

                    // masukkan 3 terbaik di kelas worse ke kelas best
                    classrooms.insertStudent(classroomNow, worseStudent1.key, worseStudent1.id, worseStudent1.cheated);
                    classrooms.insertStudent(classroomNow, worseStudent2.key, worseStudent2.id, worseStudent2.cheated);
                    classrooms.insertStudent(classroomNow, worseStudent3.key, worseStudent3.id, worseStudent3.cheated);
                
                    // masukkan 3 terburuk di kelas best ke kelas worse
                    classrooms.insertStudent(worseClassroom, student1.key, student1.id, student1.cheated);
                    classrooms.insertStudent(worseClassroom, student2.key, student2.id, student2.cheated);
                    classrooms.insertStudent(worseClassroom, student3.key, student3.id, student3.cheated);

                    classrooms.countClassAverageScore(classroomNow);
                    classrooms.countClassAverageScore(worseClassroom);

                    int bestStudent = classroomNow.students.maxNode(classroomNow.students.root).id;
                    int worstStudent = classroomNow.students.minNode(classroomNow.students.root).id;

                    out.println(String.valueOf(bestStudent) + " " + String.valueOf(worstStudent));
                }
            }
            
            else if(classrooms.size > 2 && classroomNow != classrooms.first && classroomNow != classrooms.last){ // jika jumlah kelas lebih dari 2 dan pakcil bukan di antara kedua ujung
                ListNode betterClass = classroomNow.prev;
                ListNode worseClass = classroomNow.next;
                
                //3 terburuk dari better classroom
                Node bc1 = betterClass.students.minNode(betterClass.students.root);
                Node worstBC1 = new Node(bc1.key, bc1.id, bc1.cheated); // INI STUDENT TERBURUK 1 DARI BETTER CLASS
                classrooms.deleteStudent(betterClass, bc1.key, bc1.id);

                Node bc2 = betterClass.students.minNode(betterClass.students.root);
                Node worstBC2 = new Node(bc2.key, bc2.id, bc2.cheated); // INI STUDENT TERBAIK 2 DARI BETTER CLASS
                classrooms.deleteStudent(betterClass, bc2.key, bc2.id);

                Node bc3 = betterClass.students.minNode(betterClass.students.root);
                Node worstBC3 = new Node(bc3.key, bc3.id, bc3.cheated); // INI STUDENT TERBAIK 3 DARI BETTER CLASS
                classrooms.deleteStudent(betterClass, bc3.key, bc3.id);
                
                // 3 terbaik worse classroom
                Node wc1 = worseClass.students.maxNode(worseClass.students.root);
                Node bestwc1 = new Node(wc1.key, wc1.id, wc1.cheated); // INI STUDENT TERBAIK 1 DARI WORSE CLASS
                classrooms.deleteStudent(worseClass, wc1.key, wc1.id);

                Node wc2 = worseClass.students.maxNode(worseClass.students.root);
                Node bestwc2 = new Node(wc2.key, wc2.id, wc2.cheated); // INI STUDENT TERBAIK 2 DARI WORSE CLASS
                classrooms.deleteStudent(worseClass, wc2.key, wc2.id);

                Node wc3 = worseClass.students.maxNode(worseClass.students.root);
                Node bestwc3 = new Node(wc3.key, wc3.id, wc3.cheated); // INI STUDENT TERBAIK 3 DARI WORSE CLASS
                classrooms.deleteStudent(worseClass, wc3.key, wc3.id);

                // 3 terbaik dari current
                Node bestC1 = classroomNow.students.maxNode(classroomNow.students.root);
                Node currentclassBest1 = new Node(bestC1.key, bestC1.id, bestC1.cheated); // INI STUDENT TERBAIK 1 DARI CURRENT CLASS
                classrooms.deleteStudent(classroomNow, bestC1.key, bestC1.id);

                Node bestC2 = classroomNow.students.maxNode(classroomNow.students.root);
                Node currentclassBest2 = new Node(bestC2.key, bestC2.id, bestC2.cheated); // INI STUDENT TERBAIK 2 DARI CURRENT CLASS
                classrooms.deleteStudent(classroomNow, bestC2.key, bestC2.id);

                Node bestC3 = classroomNow.students.maxNode(classroomNow.students.root);
                Node currentclassBest3 = new Node(bestC3.key, bestC3.id, bestC3.cheated); // INI STUDENT TERBAIK 3 DARI CURRENT CLASS
                classrooms.deleteStudent(classroomNow, bestC3.key, bestC3.id);

                // 3 terburuk dari current
                Node worstC1 = classroomNow.students.minNode(classroomNow.students.root);
                Node currentclassWorst1 = new Node(worstC1.key, worstC1.id, worstC1.cheated); // INI STUDENT TERBURUK 1 DARI CURRENT CLASS
                classrooms.deleteStudent(classroomNow, worstC1.key, worstC1.id);

                Node worstC2 = classroomNow.students.minNode(classroomNow.students.root);
                Node currentclassWorst2 = new Node(worstC2.key, worstC2.id, worstC2.cheated); // INI STUDENT TERBAIK 2 DARI CURRENT CLASS
                classrooms.deleteStudent(classroomNow, worstC2.key, worstC2.id);

                Node worstC3 = classroomNow.students.minNode(classroomNow.students.root);
                Node currentclassWorst3 = new Node(worstC3.key, worstC3.id, worstC3.cheated); // INI STUDENT TERBAIK 3 DARI CURRENT CLASS
                classrooms.deleteStudent(classroomNow, worstC3.key, worstC3.id);

                // TUKER 3 TERBAIK DARI WORSE CLASS KE CURRENT CLASS DAN 3 TERBURUK DARI CURRENT CLASS KE WORSE CLASS
                // masukin 3 TERBAIK dari WORSE CLASS ke CURRENT CLASS
                classrooms.insertStudent(classroomNow, bestwc1.key, bestwc1.id, bestwc1.cheated);
                classrooms.insertStudent(classroomNow, bestwc2.key, bestwc2.id, bestwc2.cheated);
                classrooms.insertStudent(classroomNow, bestwc3.key, bestwc3.id, bestwc3.cheated);

                // masukin 3 TERBURUK dari CURRENT CLASS ke WORSE CLASS
                classrooms.insertStudent(worseClass, currentclassWorst1.key, currentclassWorst1.id, currentclassWorst1.cheated);
                classrooms.insertStudent(worseClass, currentclassWorst2.key, currentclassWorst2.id, currentclassWorst2.cheated);
                classrooms.insertStudent(worseClass, currentclassWorst3.key, currentclassWorst3.id, currentclassWorst3.cheated);

                // TUKER 3 TERBAIK DARI CURRENT CLASS KE BEST CLASS DAN 3 TERBURUK DARI BEST CLASS KE CURRENT CLASS
                // masukin 3 TERBAIK dari CURRENT CLASS ke BEST CLASS
                classrooms.insertStudent(betterClass, currentclassBest1.key, currentclassBest1.id, currentclassBest1.cheated);
                classrooms.insertStudent(betterClass, currentclassBest2.key, currentclassBest2.id, currentclassBest2.cheated);
                classrooms.insertStudent(betterClass, currentclassBest3.key, currentclassBest3.id, currentclassBest3.cheated);

                // masukin 3 TERBURUK dari BEST CLASS ke CURRENT CLASS
                classrooms.insertStudent(classroomNow, worstBC1.key, worstBC1.id, worstBC1.cheated);
                classrooms.insertStudent(classroomNow, worstBC2.key, worstBC2.id, worstBC2.cheated);
                classrooms.insertStudent(classroomNow, worstBC3.key, worstBC3.id, worstBC3.cheated);

                classrooms.countClassAverageScore(worseClass);
                classrooms.countClassAverageScore(classroomNow);
                classrooms.countClassAverageScore(betterClass);

                int bestStudent = classroomNow.students.maxNode(classroomNow.students.root).id;
                int worstStudent = classroomNow.students.minNode(classroomNow.students.root).id;

                out.println(String.valueOf(bestStudent) + " " + String.valueOf(worstStudent));
            }
        }
    }

    public static void K(){
        int idClassNow = classrooms.current.id; // SIMPAN ID CLASSROOM CURRENT UTK DICARI NANTI
        int index = 0; // OUTPUT INDEX PAKCIL

        ArrayList<ListNode> classroomList = new ArrayList<>(); // BUAT ARRAYLIST UNTUK MASUKIN LISTNODE (CLASSROOM)
        ListNode temp = classrooms.first;
        do{
            classroomList.add(temp);
            temp = temp.next;
        }while(temp != classrooms.first);

        // classrooms.display();
        classrooms.deleteAll(); // DELETE SMUA KELAS DI CLASSROOM
        ListNode[] arrayClassroom = classroomList.toArray(new ListNode[0]); // KONVERSI ARRAYLIST JADI ARRAY UNTUK MERGE SORT

        MergeSort sorting = new MergeSort(); //BUAT OBJEK MERGE SORT
        sorting.sort(arrayClassroom, 0, arrayClassroom.length - 1); // PANGGIL FUNGSI MERGE SORT

        // MASUKKAN KEMBALI SORTED CLASSROOM DARI ARRAY TERSEBUT KE DOUBLY LINKED LIST
        for(int i = 0; i < arrayClassroom.length; i++){ 
            if(idClassNow == arrayClassroom[i].id){ // JIKA DI ARRAY KETEMU CLASS DENGAN ID YANG SAMA DENGAN ID CLASS PAKCIL SBLM SORTING
                index = i; // INDEX POSISI PAKCIL SETELAH DISORT ULANG
            }
            ListNode node = classrooms.add(arrayClassroom[i].capacity, arrayClassroom[i].id, arrayClassroom[i].students); // MASUKKAN KE DOUBLY LINKED LIST

            //COPY CONTENTS
            node.capacity = arrayClassroom[i].studentsMap.size(); // COPY KAPASITAS
            node.totalScore = arrayClassroom[i].totalScore; //COPY TOTAL SCORE
            node.capacityCheck = 0; //COPY CAPACITY CHECK
            node.averageScore = arrayClassroom[i].averageScore; // COPY AVERAGE SCORE
            node.studentsMap = arrayClassroom[i].studentsMap; //COPY HASHMAP
            classrooms.countClassAverageScore(node); //HITUNG AVERAGE SCORE
        }

        do{
            classrooms.current = classrooms.current.next; //PINDAHKAN POINTER CURRENT SESUAI DENGAN KELAS YANG TADINYA DITEMPATI PAKCIL SEBELUM SORTING
        }while(idClassNow != classrooms.current.id);

        // classrooms.current = nodeNow;
        // System.out.println(Arrays.toString(arrayClassroom));
        // System.out.println("current now " + classrooms.current);
        // System.out.println("current next " + classrooms.current.next);
        // classrooms.display();

        out.println(index+1);
    }

    public static void A(){
        int capacity = in.nextInt();
        ListNode added = classrooms.add(capacity, classID, new AVLTree());
        classID++; // JANGAN LUPA TAMBAHIN CLASS ID
        for(int i = 0; i < capacity; i++){ // set up students tiap kelas (sesuai dgn kapasitas)
            classrooms.insertStudent(added, 0, studentID, 0); // masukin student (idnya dan scoreny) ke kelas sesuai dgn kapasitas kelas, avltree node key = score
            classrooms.countClassAverageScore(added);
            studentID++;
        }
        // classrooms.display();
        out.println(added.id);
    }

    // taken from https://codeforces.com/submissions/Petr
    // together with PrintWriter, these input-output (IO) is much faster than the
    // usual Scanner(System.in) and System.out
    // please use these classes to avoid your fast algorithm gets Time Limit
    // Exceeded caused by slow input-output (IO)
    public static class InputReader {
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

    }
}

class DoublyLinkedList {
    public ListNode first;
    public ListNode current;
    public ListNode last;
    public int size = 0;

    public ListNode add(int capacity, int id, AVLTree students) { // tambahin classroom
        ListNode tmp = new ListNode(capacity, id, students);

        if (size == 0) {
            first = tmp;
            last = tmp;
            current = tmp;
            tmp.next = tmp;
            tmp.prev = tmp;
        } 
        else {
            last.next = tmp; // UPDATE NEXT DARI CURRENT LAST POINTER
           
            tmp.prev = last; // HUBUNGIN NODE BARU SETELAH LAST
            tmp.next = first; // HUBUNGIN NODE BARU KE FIRST KRN CIRCULAR

            last = tmp; // UPDATE POINTER LUST
            first.prev = last; // HUBUNGIN PERTAMA KE LAST KRN CIRCULAR
        }
        size++;
        return tmp;
    }

    public ListNode delete(String direction) {
        if (size == 0 || current == null || size < 2) {
            return null;
        }
    
        ListNode tmp;
        if (direction.equals("R")) {
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

    public ListNode move(String direction) {
        if (size == 0 || current == null) {
            return null;
        }
        else{
            if (direction.equals("R")) {
                current = current.next;
            } 
            else if (direction.equals("L")) {
                current = current.prev;
            }
        }
        return current;
    }

    public void display() {  // print dari FIRST node classroom, print smua contentnya
        ListNode tmp = first;
        if (tmp == null) {
            System.out.println("No Classrooms.");
            return;
        }
        do {
            System.out.println("Classroom:");
            System.out.print(tmp);
            System.out.println("\nStudents:");
            tmp.students.inOrder(tmp.students.root);
            System.out.println("\n");
            System.out.println("Average Score: " + tmp.averageScore);
            System.out.println("\n");
            System.out.println("HASHMAP: " + tmp.studentsMap);
            System.out.println("\n");
            tmp = tmp.next;
        } while (tmp != first);
    } 

    public void insertStudent(ListNode node, int score, int studentid, int cheat){ // tambahin student
        node.totalScore += score; //kurangin total score
        node.studentsMap.put(studentid, score); // tambahin ke hashmap
        node.capacity++; // tambahin jumlah student di classroom
        node.students.root = node.students.insert(node.students.root, score, studentid, cheat); // masukin ke avl tree student
    }

     public void deleteStudent(ListNode node, int score, int studentid){ // delete student
        node.totalScore -= score; // kurangin total score
        node.studentsMap.remove(studentid); // buang dari hashmap
        node.capacity--; // kurangin kapasitas
        node.students.root = node.students.delete(node.students.root, score, studentid); // delete dari avl tree student
    }

    public void allClassAverageScore(){
        ListNode tmp = first;
        if (tmp == null) {
            System.out.println("No Classrooms.");
            return;
        }
        do {
            tmp.averageScore = (double) tmp.totalScore / (double) tmp.capacity;
            tmp = tmp.next;
        } while (tmp != first);
    }

    public void countClassAverageScore(ListNode node){
        node.averageScore = (double) node.totalScore / (double) node.capacity;
    }
    
    public void deleteAll() {
        if (size == 0) {
            System.out.println("No nodes to delete.");
            return;
        }

        ListNode currentToDelete = first;
        do {
            ListNode nextNode = currentToDelete.next;
            deleteNode(currentToDelete);
            currentToDelete = nextNode;
        } while (currentToDelete != first);

        first = null;
        last = null;
        current = null;
        size = 0;
    }

    private void deleteNode(ListNode node) {
        if (node == first) {
            first = node.next;
        }
        if (node == last) {
            last = node.prev;
        }
        if (node == current) {
            current = node.next;
        }

        if (node.prev != null) {
            node.prev.next = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        }

        node.next = null;
        node.prev = null;

        size--;
    }
}

class ListNode { //untuk doublylinkedlist
    public ListNode next;
    public ListNode prev;
    public int capacity; // final capacity
    public int capacityCheck; //untuk cek kapasitas kelas masih ada ato nggak
    public int id; // id classroom
    public AVLTree students; //attribute students avltree, setiap kelas punya students dlm bentuk avltree, KEY = SCORE
    public int totalScore;
    public double averageScore;
    public HashMap<Integer, Integer> studentsMap;

    ListNode(int capacity, int id, AVLTree students) {
        this.capacity = 0;
        this.capacityCheck = capacity;
        this.id = id;
        this.students = students;
        this.totalScore = 0;
        this.averageScore = 0;
        this.studentsMap = new HashMap<>();
    }

    public String toString() {
        return "[ID: " + String.valueOf(id) + ", Capacity: " + String.valueOf(capacity) + "]";
    }
}

class AVLTree {
    public Node root;

    public int height(Node node) {
        if (node == null) {
            return 0;
        }
        return node.height;
    }

    public int balancing(Node node) { // CEK BALANCING PERBEDAAN ANTAR TREE HARUS (-1, 0 ,1)
        if (node == null) {
            return 0;
        }
        return height(node.left) - height(node.right);
    }

    public Node insert(Node node, int key, int id, int cheat) { // key = score
        if (node == null) {
            return (new Node(key, id, cheat));
        }

        else if (key < node.key) {
            node.left = insert(node.left, key, id, cheat); 
        }
        else if (key > node.key) {
            node.right = insert(node.right, key, id, cheat); 
        }
        else {
            if (id < node.id) { //ID LEBIH KECIL KE KANAN KARENA RANKING LEBIH TINGGI
                node.right = insert(node.right, key, id, cheat);
            }
            else if(id > node.id){ // ID LEBIH BESAR KE KIRI KARENA RANKING LEBIH RENDAH
                node.left = insert(node.left, key, id, cheat);
            }
        }
  
       // UPDATE HEIGHT
        node.height = Math.max(height(node.left), height(node.right)) + 1; 
  
        // BALANCING
        int balance = balancing(node); 
  
        // Left Left Case 
        if (balance > 1 && balancing(node.left) >= 0) {
            return singleRightRotate(node); 
        }
 
        // Left Right Case 
        else if (balance > 1 && balancing(node.left) < 0) { 
            node.left = singleLeftRotate(node.left); 
            return singleRightRotate(node); 
        } 
 
        // Right Right Case 
        else if (balance < -1 && balancing(node.right) <= 0) {
            return singleLeftRotate(node); 
        }
 
        // Right Left Case 
        else if (balance < -1 && balancing(node.right) > 0) { 
            node.right = singleRightRotate(node.right); 
            return singleLeftRotate(node); 
        } 
  
        //RETURN ROOT BARU
        return node; 
    }
    
    public Node minNode(Node node) {
        Node current = node;
        while (current.left != null) {
            current = current.left;
        }
        return current;
    }

    public Node maxNode(Node node) {
        Node current = node;
        while (current.right != null){
            current = current.right;
        }
        return current;
    }

    public Node delete(Node root, int key, int id) {
        if (root == null) {
            return root; 
        }
 
        // smaller key di left subtree 
        else if (key < root.key) {
            root.left = delete(root.left, key, id); 
        }
 
        // bigger key di right subtree
        else if (key > root.key) {
            root.right = delete(root.right, key, id); 
        }
 
        // jika key/score sama maka ini yang mau didelete, tapi harus check id sama ato ga dulu
        else{ 
            if(id > root.id){
                root.left = delete(root.left, key, id);
            }
            else if(id < root.id){
                root.right = delete(root.right, key, id); 
            }
            else if(id == root.id){
                // satu child 
                if ((root.left == null) || (root.right == null)) { 
                    Node temp = null; 
                    if (temp == root.left){ 
                        temp = root.right; 
                    }
                    else{
                        temp = root.left; 
                    }
    
                    // No child  
                    if (temp == null) { 
                        temp = root; 
                        root = null; 
                    } 
                    else { 
                        root = temp; 
                    }
                } 
                else{ 
                    //inorder successor
                    Node temp = minNode(root.right); 

                    root.key = temp.key; 
                    root.id = temp.id;
                    root.cheated = temp.cheated;

                    root.right = delete(root.right, temp.key, temp.id);
                } 
            }
        } 
 
        // jika avltree baru satu node
        if (root == null) {
            return root; 
        }
        else{
            // UPDATE HEIGHT
            root.height = Math.max(height(root.left), height(root.right)) + 1; 
    
            // BALANCING 
            int balance = balancing(root); 
    
            // Left Left Case 
            if (balance > 1 && balancing(root.left) >= 0) {
                return singleRightRotate(root); 
            }
    
            // Left Right Case 
            else if (balance > 1 && balancing(root.left) < 0) { 
                root.left = singleLeftRotate(root.left); 
                return singleRightRotate(root); 
            } 
    
            // Right Right Case 
            else if (balance < -1 && balancing(root.right) <= 0) {
                return singleLeftRotate(root); 
            }
    
            // Right Left Case 
            else if (balance < -1 && balancing(root.right) > 0) { 
                root.right = singleRightRotate(root.right); 
                return singleLeftRotate(root); 
            } 
    
            //RETURN ROOT BARU
            return root; 
        }
    }

    public Node singleLeftRotate(Node x) {
        Node y = x.right; 
        Node T2 = y.left; 
  
        // rotasi 
        y.left = x; 
        x.right = T2; 
  
        //  ubah height
        x.height = Math.max(height(x.left), height(x.right)) + 1; 
        y.height = Math.max(height(y.left), height(y.right)) + 1; 
   
        return y; 
    }

    public Node singleRightRotate(Node y) {
        Node x = y.left; 
        Node T2 = x.right; 
  
        // rotasi 
        x.right = y; 
        y.left = T2; 
  
        // ubah height
        y.height = Math.max(height(y.left), height(y.right)) + 1; 
        x.height = Math.max(height(x.left), height(x.right)) + 1; 
  
        return x; 
    }

    public Node search(Node root, int key, int id) {
        while (root != null) {
            if (key < root.key) {
                root = root.left;
            } 
            else if (key > root.key) {
                root = root.right;
            } 
            else {
                if(id > root.id){
                    root = root.left;
                }
                else if(id < root.id){
                    root = root.right;
                }
                else if (id == root.id){
                    return root;
                }
            }
        }
        return null;
    }
 
    public void preOrder(Node node) { //cetak preorder
        if (node != null) { 
            System.out.print("|Score: " + node.key + ", ID: " + node.id + "| "); 
            preOrder(node.left); 
            preOrder(node.right); 
        } 
    } 

    public void inOrder(Node node){
        if (node != null){
            // traverse left child dulu
            inOrder(node.left);
    
            // Then print the data of node
            System.out.print("|ID: " + node.id + ", Score: " + node.key + ", Cheated: " + node.cheated + "| ");
    
            // traverse right child setelah
            inOrder(node.right);
        }
    }
}

class Node { //untuk avltree
    public int key; // id student
    public int height, id; // height avltree, attribute id student
    public Node left, right;
    public int cheated;

    Node(int key, int id, int cheated) {
        this.key = key;
        this.id = id;
        this.cheated = 0;
        this.height = 1;
        this.cheated = cheated;
    }

    public void addCheated(){
        this.cheated++;
    }

    public String toString(){
        return "|ID: " + String.valueOf(id) + ", SCORE: " + String.valueOf(key) + ", CHEATED: "+ String.valueOf(cheated) + "|";
    }
}

class MergeSort {
    void merge(ListNode arr[], int l, int m, int r) {
        int n1 = m - l + 1;
        int n2 = r - m;

        ListNode L[] = new ListNode[n1];
        ListNode R[] = new ListNode[n2];

        for (int i = 0; i < n1; ++i)
            L[i] = arr[l + i];
        for (int j = 0; j < n2; ++j)
            R[j] = arr[m + 1 + j];

        int i = 0, j = 0;
        int k = l;
        while (i < n1 && j < n2) {
            if (L[i].averageScore > R[j].averageScore || (L[i].averageScore == R[j].averageScore && L[i].id < R[j].id)) {
                arr[k] = L[i];
                i++;
            } else {
                arr[k] = R[j];
                j++;
            }
            k++;
        }

        while (i < n1) {
            arr[k] = L[i];
            i++;
            k++;
        }

        while (j < n2) {
            arr[k] = R[j];
            j++;
            k++;
        }
    }
 
    void sort(ListNode arr[], int l, int r) {
        if (l < r) {
            int m = l + (r - l) / 2;

            sort(arr, l, m);
            sort(arr, m + 1, r);

            merge(arr, l, m, r);
        }
    }
}

//REFERENCES:
// MERGE SORT: https://www.geeksforgeeks.org/merge-sort/
// AVL TREE: https://www.geeksforgeeks.org/insertion-in-an-avl-tree/, LAB 6
// DOUBLY LINKED LIST: https://www.geeksforgeeks.org/introduction-to-doubly-linked-lists-in-java/, LAB 5

// NOTE:
// NOT FULL SCORE, BUT ALMOST (94/100)