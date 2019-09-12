
import java.util.Stack;

public class L394 {

    public String decodeString(String s) {
        StringBuilder sb = new StringBuilder();
        decode(s, 0, sb);
        return sb.toString();
    }

    // 解析s[l...n-1]的编码字符,初始为数字, sb保存当前结果
    // 返回当前层最后闭括号]的下标
    private int decode(String s, int start, StringBuilder sb){

        String res = "";
        int idx = start; // 起始字符
        int cnt = 0;
        while(idx < s.length()){

            char c = s.charAt(idx);
            if (c >= '0' && c <= '9'){
                cnt = cnt * 10 + (c - '0');
            }else if (c == '['){
                // 下一层编码
                StringBuilder temp = new StringBuilder();
                int end = decode(s, idx + 1, temp);
                while (cnt-- > 0){
                    sb.append(temp);
                }
                idx = end;
                cnt = 0;
            }else if (c == ']'){
                // 当前层结束
                return idx;
            }else { // 字母
                sb.append(c);
            }
            idx++;
        }
        return idx;
    }

    public String decodeString1(String s) {
        if(s == null || s.length() == 0)
            return s;

        int n = s.length();
        int idx = 0;
        int cnt = 0;
        String res = "";

        Stack<Integer> numStack = new Stack<>();
        Stack<String> strStack = new Stack<>();

        while(idx < n){
            char c = s.charAt(idx++);
            if(c >= '0' && c <= '9'){
                cnt = cnt * 10 + (c - '0');
            }else if(c == '['){
                numStack.push(cnt);
                strStack.push(res);
                cnt = 0;
                res = "";
            }else if(c == ']'){  // ]
                String top = strStack.pop();
                int count = numStack.pop();
                for(int i = 0; i < count; i++){
                    top += res;
                }
                res = top;
            }else{
                res += c;
            }
        }
        return res;
    }

    public static void main(String[] args){
        new L394().decodeString("3[a]4[f]");
    }
}
