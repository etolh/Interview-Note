
import java.util.HashMap;
import java.util.Map;

public class L494 {

    // 优化
    private Map<String, Integer> maps = new HashMap<>();
    public int findTargetSumWays(int[] nums, int S) {
        return dfs(nums, 0, S);
    }

    private int dfs(int[] nums, int cur, int tar){

        String key = cur + ":" + tar;
        if(maps.containsKey(key)){
            return maps.get(key);
        }

        if(cur == nums.length && tar == 0){
            return 1;
        }

        if(cur == nums.length){
            return 0 ;
        }

        int add = dfs(nums, cur + 1, tar - nums[cur]);
        int sub = dfs(nums, cur + 1, tar + nums[cur]);
        int count = add + sub;

        maps.put(key, count);
        return count;

    }

    /*
    private int ans = 0;
    public int findTargetSumWays(int[] nums, int S) {
        dfs(nums, 0, S);
        return ans;
    }

    private void dfs(int[] nums, int cur, int tar){
        if(cur == nums.length && tar == 0){
            ans++;
            return;
        }

        if(cur == nums.length){
            return;
        }

        dfs(nums, cur + 1, tar - nums[cur]);
        dfs(nums, cur + 1, tar + nums[cur]);

    }
    */

    // DP
    public int findTargetSumWays2(int[] nums, int S) {
        if (nums == null || nums.length == 0)
            return 0;

        int s = 0;
        for (int n : nums){
            s += n;
        }
        if ((S+s)%2 == 1){
            return 0;
        }
        int tar = (S+s)/2;

        int[] dp = new int[tar];
        dp[0] = 1;

        for (int n : nums){
            for (int i = tar; i >= n; i--)
                dp[i] += dp[i - n];
        }
        return dp[tar];
    }
}
