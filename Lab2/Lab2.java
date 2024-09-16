package Lab2;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

public class Lab2 {
    private static InputReader in;
    private static PrintWriter out;

    static int maxOddEvenSubSum(long[] a) {
        // TODO: Implement this method
        List<Long> numberList = Arrays.asList(Arrays.stream(a).boxed().toArray(Long[]::new));
        int maxSum = 0;
        if (a.length % 2 == 0){
            int thisSumEven = 0;
            long num = 0;
            for(int i = 0; i < a.length; i++){
                if (a[i] % 2 == 0){
                    thisSumEven += a[i];
                    num = a[i];
                    if(thisSumEven <= num){
                        thisSumEven = (int) num;
                    }
                    if (thisSumEven > maxSum || maxSum == 0) {
                        if(thisSumEven > maxSum){
                            maxSum = thisSumEven;
                        }
                        else{
                            if(numberList.contains(0L)){
                                if(maxSum <= 0){
                                    maxSum = 0;
                                }
                            }
                            else{
                                maxSum = thisSumEven;
                            }
                        }
                    }
                }
                else if(a[i] % 2 != 0){
                    thisSumEven = 0;
                }
            }
            
        }
        else if(a.length % 2 != 0){
            int thisSumOdd = 0;
            long num = 0;
            for(int i = 0; i < a.length; i++){
                if (a[i] % 2 != 0){
                    thisSumOdd += a[i];
                    num = a[i];
                    if(thisSumOdd <= num){
                        thisSumOdd = (int) num;
                    }
                    if (thisSumOdd > maxSum || maxSum == 0) {
                        if(thisSumOdd > maxSum){
                            maxSum = thisSumOdd;
                        }
                        else{
                            if(numberList.contains(0L)){
                                if(maxSum <= 0){
                                    maxSum = 0;
                                }
                            }
                            else{
                                maxSum = thisSumOdd;
                            }
                        }
                    }
                }
                else if(a[i] % 2 == 0){
                    thisSumOdd = 0;
                }
            }
        }
        return maxSum;
    }

    public static void main(String[] args) throws IOException {
        InputStream inputStream = System.in;
        in = new InputReader(inputStream);
        OutputStream outputStream = System.out;
        out = new PrintWriter(outputStream);

        // Read value of N
        int N = in.nextInt();

        // Read value of x
        long[] x = new long[N];
        for (int i = 0; i < N; ++i) {
            x[i] = Long.parseLong(in.next());
        }

        int ans = maxOddEvenSubSum(x);
        out.println(ans);

        // don't forget to close/flush the output
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

    }
}