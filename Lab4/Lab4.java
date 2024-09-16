package Lab4;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import java.lang.Math;

public class Lab4 {
    private static InputReader in;
    private static PrintWriter out;
    private static char[] dnadino1;       
    private static char[] dnadino2;


    // TODO: implement this method
    static int deteksiDNA(int m, int n, int[][] memo) {
        if(m == 0 || n==0){
            return 0;
        }
        if (memo[m][n] != -1) {
            return memo[m][n];
        }

        if (dnadino1[m - 1] == dnadino2[n-1]) {
            memo[m][n] = 1 + deteksiDNA(m - 1, n - 1, memo);
        } else {
            int option1 = deteksiDNA(m - 1, n, memo);
            int option2 = deteksiDNA(m, n - 1, memo);
            memo[m][n] = Math.max(option1, option2);
        }

        return memo[m][n];
    }

    public static void main(String[] args) {
        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);

        // Read value of T
        int T = in.nextInt();

        // Run T test case
        while (T-- > 0) {
            int M = in.nextInt();
            int N = in.nextInt();
            String S1 = in.next();
            String S2 = in.next();

            dnadino1 = new char[M];
            dnadino2 = new char[N];

            for(int i = 0; i < M; i++){
                dnadino1[i] = S1.charAt(i);
            }

            for(int i = 0; i < N; i++){
                dnadino2[i] = S2.charAt(i);
            }
            
            int[][] memo = new int[M + 1][N + 1];
            for (int i = 0; i <= M; i++) {
                for (int j = 0; j <= N; j++) {
                    memo[i][j] = -1;
                }
            }
            // TODO: implement method deteksiDNA(M, N, S1, S2) to get answer
            int ans =  deteksiDNA(M, N, memo);;
            out.println(ans);
        }

        // don't forget to close/flush the output
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
    }
}