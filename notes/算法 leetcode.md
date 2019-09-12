## LeetCode



### L394. Decode String ***

将每一个[]看出一层encoded string，有递归和栈两种方式

编码string格式   数字[字母[编码string]]

栈解法

使用numStack保存该层编码string的数字，strStack保存遍历到该层时上一层的结果。cnt统计每层数字，res保存当前结果。



​	当遇到数字时，cnt统计上一层数字.

​	当遇到[，表示到底下一层，将cnt和res分别保存到栈中，并将cnt和res清空。

​	当遇到字母时，res统计当前层字母。

​	当遇到]时，表示当前层结束，从栈中弹出上一层数字cnt和前面结果  res=top() + cnt * res;



https://blog.csdn.net/mine_song/article/details/71036245





递归

递归函数int decode(String s, int start, StringBuilder sb)

表示递归解析s[idx...]字符串，并将解析后的结果存储到sb中，并返回当前层闭括号的下标]。

初始遍历s，存储数字和字符，当遇到[时，表明遇到下一层编码字符串，递归解析，将解析后的字符串*cnt串联上当前层sb.



https://leetcode.com/problems/decode-string/discuss/87719/Java-recursive-solution.





### L494. Target Sum

1. DFS

   遍历每个位置，对于每个数有两种选择+/-，直到遍历到末尾，若目标减到0，此时ans++;

   可以用string记录当前坐标和tar表示当前状态，用于缓存。

2. dp

   利用规律可知，所有正数之和=(tar+sum)/2

   dp[i] += dp[i - n], n是数组中一员。

   

https://blog.csdn.net/mine_song/article/details/70216562





#### L301. Remove Invalid Parentheses ***


  

BFS

从最开始字符串s，每次减去其中一个括号，加入到队列中。

从队列中弹出修减的字符串，若合法，添加到结果集，并不再向下遍历。

https://leetcode.com/problems/remove-invalid-parentheses/discuss/75032/Share-my-Java-BFS-solution



### L102. Binary Tree Level Order Traversal

按层打印二叉树，每次输出一层

1. 添加行标识
2. 先保存每行数量，每次都弹出一行所有节点，并将下一行节点加入到队列中。

https://www.cnblogs.com/lightwindy/p/8520387.html

  