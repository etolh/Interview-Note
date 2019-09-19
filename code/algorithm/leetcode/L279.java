
public class L279 {
    public int numSquares(int n) {
        if(n == 0)
            return 0;
        if(n == 1)
            return 1;

        int ans = Integer.MAX_VALUE;
        for(int i = 1; i * i <= n; i++){
            ans = Math.min(ans, numSquares(n - i * i)) + 1;
        }
        return ans;
    }

    public static void main(String[] args){
        new L279().numSquares(12);
    }

}
