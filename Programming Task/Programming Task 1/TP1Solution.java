import java.io.*;
import java.util.*;

public class TP1Solution {
  private static InputReader in;
  private static PrintWriter out;
  private static Wahana[] daftarWahana;
  private static Pengunjung[] daftarPengunjung;
  private static Deque<Pengunjung> daftarKeluar = new ArrayDeque<>();
  private static int[][][] dp;
  private static int[][][] bk;

  public static int A(Pengunjung pengunjung, Wahana wahana) {
    if (pengunjung.uang < wahana.harga) return -1;

    Pengunjung copyPengunjung = new Pengunjung(pengunjung.id, pengunjung.totalBermain);
    if (pengunjung.tipe.equals("R")) {
      wahana.antreanReguler.add(copyPengunjung);
    } else {
      wahana.antreanFastTrack.add(copyPengunjung);
    }

    return wahana.antreanReguler.size() + wahana.antreanFastTrack.size();
  }

  public static String E(Wahana wahana) {
    int kapasitasFastTrack = (wahana.persentasePengunjungFT * wahana.kapasitasPengunjung + 99) / 100;
    String selectedPengunjung = "";
    int fastTrackCount = 0;
    int regulerCount = 0;
    Pengunjung pengunjung;
    boolean pengunjungIsReguler = true;

    while (fastTrackCount + regulerCount < wahana.kapasitasPengunjung
        && (!wahana.antreanFastTrack.isEmpty() || !wahana.antreanReguler.isEmpty())) {
      if (fastTrackCount < kapasitasFastTrack && !wahana.antreanFastTrack.isEmpty()) {
        pengunjung = wahana.antreanFastTrack.poll();
        pengunjungIsReguler = false;
      } else if (!wahana.antreanReguler.isEmpty()) {
        pengunjung = wahana.antreanReguler.poll();
        pengunjungIsReguler = true;
      } else {
        pengunjung = wahana.antreanFastTrack.poll();
        pengunjungIsReguler = false;
      }

      Pengunjung realPengunjung = daftarPengunjung[pengunjung.id - 1];
      if (realPengunjung.uang >= wahana.harga && !realPengunjung.isExit) {
        realPengunjung.uang -= wahana.harga;
        realPengunjung.totalBermain++;
        realPengunjung.point += wahana.point;

        selectedPengunjung += (pengunjung.id + " ");

        if (realPengunjung.uang == 0) {
          daftarKeluar.add(realPengunjung);
        }

        if (pengunjungIsReguler) regulerCount++;
        else fastTrackCount++;
      }
    }

    return selectedPengunjung;
  }

  public static int S(Pengunjung pengunjung, Wahana wahana) {
    Pengunjung realPengunjung = daftarPengunjung[pengunjung.id - 1];
    if (realPengunjung.isExit || realPengunjung.uang < wahana.harga) return -1;

    int urutanPengunjung = -1;
    int kapasitasFastTrack = (wahana.persentasePengunjungFT * wahana.kapasitasPengunjung + 99) / 100;

    PriorityQueue<Pengunjung> antreanFastTrackTemp = new PriorityQueue<>(wahana.antreanFastTrack);
    PriorityQueue<Pengunjung> antreanRegulerTemp = new PriorityQueue<>(wahana.antreanReguler);

    int urutan = 0;
    Pengunjung tempPengunjung;
    while (urutanPengunjung == -1 && (!antreanFastTrackTemp.isEmpty() || !antreanRegulerTemp.isEmpty())) {
      if (!antreanFastTrackTemp.isEmpty() && (urutan % wahana.kapasitasPengunjung) < kapasitasFastTrack) {
        tempPengunjung = antreanFastTrackTemp.remove();
      } else if (!antreanRegulerTemp.isEmpty()) {
        tempPengunjung = antreanRegulerTemp.remove();
      } else {
        tempPengunjung = antreanFastTrackTemp.remove();
      }

      if (daftarPengunjung[tempPengunjung.id - 1].uang >= wahana.harga) {
        urutan++;

        if (tempPengunjung.id == pengunjung.id) {
          urutanPengunjung = urutan;
        }
      }
    }

    return urutanPengunjung;
  }

  public static int F(int p) {
    if (!daftarKeluar.isEmpty()) {
      Pengunjung pengunjungKeluar;

      if (p == 0) {
        pengunjungKeluar = daftarKeluar.pollFirst();
      } else {
        pengunjungKeluar = daftarKeluar.pollLast();
      }

      pengunjungKeluar.isExit = true;
      return pengunjungKeluar.point;
    }
    return -1;
  }

  public static List<Integer> O(int id_pengunjung) {
    int N = daftarWahana.length;
    int W = daftarPengunjung[id_pengunjung - 1].uang;

    List<List<Integer>> results = new ArrayList<>();
    List<Integer> lowestPrice = new ArrayList<>();

    for (int j = 0; j < 2; j++) {
      List<Integer> result = new ArrayList<>();

      int i = 1;
      int w = W;
      int k = j;
      while (w > 0 && dp[k][i][w - 1] == dp[k][i][w]) {
        w--;
      }
      lowestPrice.add(w);

      result.add(dp[k][i][w]);
      while (i <= N && w > 0) {
        if (bk[k][i][w] == 1) {
          result.add(i);
          w -= daftarWahana[i - 1].harga;
          k = 1 - k;
        }
        i++;
      }
      results.add(result);
    }

    List<Integer> wahanaGenap = results.get(0);
    List<Integer> wahanaGanjil = results.get(1);
    int resultGenap = wahanaGenap.get(0);
    int resultGanjil = wahanaGanjil.get(0);

    if (resultGanjil > resultGenap) {
      return wahanaGanjil;
    }
    if (resultGanjil < resultGenap) {
      return wahanaGenap;
    }
    if (lowestPrice.get(0) < lowestPrice.get(1)) {
      return wahanaGenap;
    }
    if (lowestPrice.get(0) > lowestPrice.get(1)) {
      return wahanaGanjil;
    }

    for (int i = 1; i < wahanaGenap.size() && i < wahanaGanjil.size(); i++) {
      if (wahanaGenap.get(i) < wahanaGanjil.get(i)) {
        return wahanaGenap;
      } else if (wahanaGenap.get(i) > wahanaGanjil.get(i)) {
        return wahanaGanjil;
      }
    }

    if (wahanaGenap.size() < wahanaGanjil.size()) {
      return wahanaGenap;
    } else {
      return wahanaGanjil;
    }
  }

  public static void main(String[] args) {
    InputStream inputStream = System.in;
    in = new InputReader(inputStream);
    OutputStream outputStream = System.out;
    out = new PrintWriter(outputStream);

    // Initialize Wahana
    int M = in.nextInt();
    daftarWahana = new Wahana[M];
    int harga, point, kapasitasPengunjung, persentasePengunjungFT;
    for (int i = 0; i < M; i++) {
      harga = in.nextInt();
      point = in.nextInt();
      kapasitasPengunjung = in.nextInt();
      persentasePengunjungFT = in.nextInt();

      daftarWahana[i] = new Wahana(harga, point, kapasitasPengunjung, persentasePengunjungFT);
    }

    // Initialize Pengunjung
    int N = in.nextInt();
    daftarPengunjung = new Pengunjung[N];
    String tipe;
    int uang, maxUang = 0;
    for (int i = 0; i < N; i++) {
      tipe = in.next();
      uang = in.nextInt();

      maxUang = Math.max(maxUang, uang);
      daftarPengunjung[i] = new Pengunjung(tipe, uang);
    }

    // Precompute DP
    dp = new int[2][M + 5][maxUang + 5];
    bk = new int[2][M + 5][maxUang + 5];
    for (int i = M; i >= 1; i--) {
      for (int w = 0; w <= maxUang; w++) {
        for (int k = 0; k < 2; k++) {
          int excludeItem = dp[k][i + 1][w];
          if (i % 2 == k) {
            if (daftarWahana[i - 1].harga <= w) {
              int includeItem = daftarWahana[i - 1].point + dp[1 - k][i + 1][w - daftarWahana[i - 1].harga];
              if (includeItem >= excludeItem) {
                dp[k][i][w] = includeItem;
                bk[k][i][w] = 1;
              } else {
                dp[k][i][w] = excludeItem;
              }
            } else {
              dp[k][i][w] = excludeItem;
            }
          } else {
            dp[k][i][w] = excludeItem;
          }
        }
      }
    }

    // Query
    int T = in.nextInt();
    String query;
    for (int tc = 0; tc < T; tc++) {
      query = in.next();

      if (query.equals("A")) {
        int id_pengunjung = in.nextInt();
        int id_wahana = in.nextInt();
        out.println(A(daftarPengunjung[id_pengunjung - 1], daftarWahana[id_wahana - 1]));
      }

      if (query.equals("E")) {
        int id_wahana = in.nextInt();
        String result = E(daftarWahana[id_wahana - 1]);
        if (result.equals("")) {
          out.println(-1);
        } else {
          out.println(result.trim()); // menghilangkan spasi ekstra di akhir
        }
      }

      if (query.equals("S")) {
        int id_pengunjung = in.nextInt();
        int id_wahana = in.nextInt();
        out.println(S(daftarPengunjung[id_pengunjung - 1], daftarWahana[id_wahana - 1]));
      }

      if (query.equals("F")) {
        int p = in.nextInt();
        out.println(F(p));
      }

      if (query.equals("O")) {
        int id_pengunjung = in.nextInt();
        List<Integer> result = O(id_pengunjung);
        for (int i = 0; i < result.size(); i++) {
          out.print(result.get(i));
          out.print(i + 1 == result.size() ? '\n' : ' ');
        }
      }
    }

    out.close();
  }

  static class Wahana {
    private static int idInc = 1;
    int id, harga, point, kapasitasPengunjung, persentasePengunjungFT;
    PriorityQueue<Pengunjung> antreanReguler = new PriorityQueue<>();
    PriorityQueue<Pengunjung> antreanFastTrack = new PriorityQueue<>();

    Wahana(int harga, int point, int kapasitasPengunjung, int persentasePengunjungFT) {
      this.id = idInc++;
      this.harga = harga;
      this.point = point;
      this.kapasitasPengunjung = kapasitasPengunjung;
      this.persentasePengunjungFT = persentasePengunjungFT;
    }
  }

  static class Pengunjung implements Comparable<Pengunjung> {
    private static int idInc = 1;
    int id, uang;
    String tipe;
    int point, totalBermain = 0;
    boolean isExit = false;

    Pengunjung(String tipe, int uang) {
      this.id = idInc++;
      this.tipe = tipe;
      this.uang = uang;
    }

    Pengunjung(int id, int totalBermain) {
      this.id = id;
      this.totalBermain = totalBermain;
    }

    @Override
    public int compareTo(Pengunjung other) {
      if (this.totalBermain < other.totalBermain) {
        return -1;
      } else if (this.totalBermain == other.totalBermain) {
        if (this.id < other.id) {
          return -1;
        } else {
          return 1;
        }
      } else {
        return 1;
      }
    }
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