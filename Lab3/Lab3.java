package Lab3;
import java.io.*;
import java.util.StringTokenizer;

public class Lab3{
    private static InputReader in;
    private static PrintWriter out;
    private static int arahCount = 0;
    private static long[][] CiArray;
    private static int buildingfinal;
    private static int building;
    private static int floors;
    private static String arah = "KANAN";
    private static long target;
    private static int position = 0;
    private static long[] gedunghealth;

    // Metode GA
    static String GA() {
        //TODO: Implement this method
        arahCount ++;
        if(arahCount % 2 != 0){
            arah = "KIRI";
        }
        else if(arahCount % 2 == 0){
            arah = "KANAN";
        }
        return arah;
    }

    // Metode S
    static String S(int Si){
        //TODO: Implement this method
        String res = "";
        long points = 0;
        long totalpointsgedung = 0;
        
        if(Si > floors){ //mencegah index out of bounds
            Si = floors;
        }
        
        for (int j = 0; j < floors; j++){ //cek next floor kalo udh ad floor yang hancur
            if(CiArray[position][j] == -1){
                Si += 1;
                if(Si > floors){
                    Si = floors;
                }
            }
        }

        for(int i = 0; i < Si; i++){ // loop hancurin floor sesuai Si
            if(CiArray[position][i] != -1){
                points = CiArray[position][i]; // damage
                target -= points; // kurangin target dengan damage
                totalpointsgedung += points; // jumlahkan damage
                long currentgedunghealth = gedunghealth[position]; 
                currentgedunghealth -= points;
                gedunghealth[position] = currentgedunghealth; // update isi array gedung health berdasarkan posisi
                CiArray[position][i] = -1; // mark gedung kalo udah ancur
            }
        }

        if(gedunghealth[position] <= 0){ // kurangin gedung kalo smua floors udh hancur
            building--;
            if (building <= 0){
                building = 0;
            }
        }

        //atur arah gerak 
        if(arah.equalsIgnoreCase("KANAN")){ // kanan
            position ++;
            if(position > buildingfinal-1){ // reset ke index awal kalo udh out of bounds di akhir
                position = 0;
            }

            //skip gedung yang udah hancur
            long loopcount = 0; 
            while(gedunghealth[position] <= 0){ // kalo geser kanan dan gedung yang kanan udah hancur, geser kanan lagi
                loopcount++;
                position++;
                if(position > buildingfinal-1){
                    position = 0;
                }
                if(loopcount >= buildingfinal || gedunghealth[position] > 0){ // break kalo semua gedung udh di cek / health gedung di posisi baru ga nol
                    break;
                }
            }
        }
        
        else if(arah.equalsIgnoreCase("KIRI")){ //kiri
            position--;
            if(position < 0){
                position = buildingfinal-1; //reset ke index akhir kalo out of bounds di awal
            }

            //skip gedung hancur
            long loopcount = 0;
            while(gedunghealth[position] <= 0){  // kalo geser kiri dan gedung yang kiri udah hancur, geser kiri lagi
                loopcount ++;
                position--;
                if(position < 0){
                    position = buildingfinal-1;
                }
                if(loopcount >= buildingfinal || gedunghealth[position] > 0){ // break kalo semua gedung udh di cek / health gedung di posisi baru ga nol
                    break;
                }
            }
        }

        if(target <= 0 || building == 0){ // kalo target tercapai & building hancur smua menang
            return "MENANG";
        }

        else{
            res = String.valueOf(totalpointsgedung); // print output damage
            return res;
        }
    }

    // Template
    public static void main(String[] args) {
        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);
        
        // Read input
        long T = in.nextLong();
        int X = in.nextInt();
        int C = in.nextInt();
        int Q = in.nextInt();
        
        target = T;
        building = X;
        buildingfinal = X;
        floors = C;
        CiArray = new long[X][C];
        gedunghealth = new long[X];

        for (int i = 0; i < X; i++) {
            long health = 0;
            for (int j = 0; j < C; j++) {
                long Ci = in.nextLong();
                health += Ci;
                CiArray[i][j] = Ci;
            }
            gedunghealth[i] = health;
        }

        // out.println("AWAL " + "ARRAY GEDUNG" + Arrays.deepToString(CiArray));
        // out.println("ARRAY HEALTH GEDUNG " + Arrays.toString(gedunghealth));
        // out.println("");
        // Process the query
        for (int i = 0; i < Q; i++) {
            String perintah = in.next();
            if (perintah.equals("GA")) {
                out.println(GA());
                // GA();
                // out.println("");
                // out.println(i+1 + "." + "GANTI ARAH: " + arah);
                // out.println("");

            } else if (perintah.equals("S")) {
                int Si = in.nextInt();
                out.println(S(Si));
                // out.println("");
                // out.println("CURRENT ARAH: " + arah);
                // out.println("CURRENT POSITION: " + (position));
                // out.println("CURRENT TARGET: " + target);
                // out.println("JUMLAH BUILDINGS ATTACKED: " + Si);
                // S(Si);
                // out.println(i+1 + ". " + "ARRAY GEDUNG" + Arrays.deepToString(CiArray));
                // out.println("ARRAY HEALTH GEDUNG " + Arrays.toString(gedunghealth));
                // out.println("NEXT POSITION: " + (position));
                // out.println("CURRENT TARGET: " + target);
                // out.println("SISA BUILDING: " + building);
                // out.println("");
            }      
        }

        // don't forget to close the output
        out.close();
    }
    
    // taken from https://codeforces.com/submissions/Petr
    // together with PrintWriter, these input-output (IO) is much faster than the usual Scanner(System.in) and System.out
    // please use these classes to avoid your fast algorithm gets Time Limit Exceeded caused by slow input-output (IO)
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

        public long nextLong(){
            return Long.parseLong(next());
        }

    }
}