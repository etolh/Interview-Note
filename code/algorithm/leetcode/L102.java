
import java.util.*;

public class L102 {
    public List<List<Integer>> levelOrder(TreeNode root) {

        List<List<Integer>> ans = new ArrayList<List<Integer>>();
        if(root == null)
            return ans;
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(null);
        queue.offer(root);

        while(queue.size() != 1){
            TreeNode node = queue.poll();
            if(node == null){
                // 层次标志,队列中只有当前层数据
                ArrayList<Integer> t = new ArrayList<>();
                Iterator<TreeNode> it = queue.iterator();
                while(it.hasNext()){
                    TreeNode n = it.next();
                    t.add(n.val);
                }

                ans.add(t);
                // 添加下一层标识
                queue.offer(null);
            }else{

                if(node.left != null){
                    queue.offer(node.left);
                }

                if(node.right != null){
                    queue.offer(node.right);
                }

            }
        }
        return ans;
    }
}
