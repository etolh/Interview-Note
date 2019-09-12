
import java.util.*;

public class L301 {
    public List<String> removeInvalidParentheses(String s) {

        List<String> ans = new ArrayList<String>();

        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        queue.add(s);
        visited.add(s);
        boolean found = false;

        while(!queue.isEmpty()){
            String cur = queue.poll();

            if(isValid(cur)){
                ans.add(cur);
                found = true;
            }

            // 此字符串合法，则表示在该层即可获取合法字符串，无需再遍历到下一层
            if(found)
                continue;

            for(int i = 0; i < cur.length(); i++){
                char c = cur.charAt(i);
                if(c != '(' && c != ')')
                    continue;
                // 只去掉括号,去掉第i位
                String t = cur.substring(0,i) + cur.substring(i+1);
                if(!visited.contains(t)){ // 去掉重复
                    visited.add(t);
                    queue.add(t);
                }
            }
        }

        return ans;
    }

    private boolean isValid(String s){

        int count = 0;
        for(int i = 0; i < s.length(); i++){
            char c = s.charAt(i);
            if(c == '('){
                count++;
            }else if(c == ')' && count-- == 0){
                return false;
            }
        }
        return count == 0;
    }
}
