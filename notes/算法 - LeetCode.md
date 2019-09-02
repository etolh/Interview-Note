### DP
#### L5: 最长回文子串
dp[i][j] = true, if s[i...j]为回文串; 否则为false <br>
递归：
dp[i][j] == (s[i] == s[j]) && dp[i+1][j-1]

初始:
dp[i][i] = true
dp[i][i+1] == (s[i] == s[i+1])

归纳：
dp[i][j] == (s[i] == s[j]) && (j - i < 3 || dp[i+1][j-1])

#### L10 正则匹配
1. dp[i][j]:分别表示s[0...i-1]与p[0...j-1]是否匹配；最终判断dp[m][n]

2. 递归：sc=s[i-1] pc=p[j-1] <br>
若sc==pc || pc == ".", 则dp[i][j] = dp[i-1][j-1] <br>
否则，若pc="*", pc2=p[j-2]  <br>
若sc==pc2 || pc2 == "."，则有三种匹配方式:匹配0/多个字符 <br>
dp[i][j] = dp[i][j-2] || dp[i-1][j]
否则，只匹配0个字符 dp[i][j] = dp[i][j-2]

#### L33 有效括号个数 **
1. dp解法
    1. dp[i]表示以s[i]为底的最长有效括号长度
    dp[0]= 0
    s[i]==),
    若s[i-1]==(，则dp[i]=(i>1?dp[i-2]:0)+2
    若s[i-1]==),则dp[i-1]表示s[i-1]为底的括号长度，因此i-d[i-1]-1表示该长度的起始点前一个位置；若s[i-dp[i-1]-1]=(,即正好与s[i]构成一个括号
    dp[i] = dp[i-1] + ((i - dp[i-1]) >= 2 ? dp[i - dp[i-1] - 2] : 0) + 2;

2. 栈模拟 <br>
    初始插入-1,表示起始位置
    当遇到(,将当前下标入栈
    当遇到),先弹出栈顶；若栈为空表明前一个有效序列已经扫描，将当前下标入栈；否则，计算i-s.peek()表示当前有效长度。peek为弹出栈顶后的元素。

#### L44 正则匹配2
1. 参照1，dp[i][j] denotes whether s[0....i-1] matches p[0.....j-1]
dp[i][0] = false
dp[0][j] = true, if s[0...j-1] = "****"

递归
if p[j-1] == *； 匹配0个或多个
dp[i][j] = dp[i][j-1] || dp[i-1][j]
否则，
dp[i][j] = dp[i-1][j-1] && (s.charAt(i-1)==p.charAt(j-1) || p.charAt(j-1)=='?');


#### L53 最长连续子数组
dp[i] = a[i], dp[i-1]  < 0
dp[i] = a[i] + dp[i-1], dp[i-1] >= 0

#### L62 矩阵移动路径数
dp[i][j]表示移动到(i,j)的路径数
dp[i][j] = dp[i-1][j] + dp[i][j-1]

初始
dp[i][0] = 1 
dp[0][j] = 1


#### L62 矩阵移动路径数2
1. 添加障碍，原理一致；障碍处dp[i][j] = 0

#### L64 矩阵路径最小和
dp[i][j]:从(0,0)到(i.j)最小路径和
dp[i][j] = Math.min(dp[i-1][j], dp[i][j-1]) + a[i][j]


#### L72 编辑距离
word1转换到word2的步骤数
dp[i][j]:将word1前i个字符[0...i-1]变成word2前j个字符[0...j-1]的最少步数.
初始
dp[0][j]=j dp[i][0]=i

若word1[i-1]==word2[j-1],则dp[i][j]=dp[i-1][j-1]
否则，有三种方式
1. 插入dp[i][j-1], word[i]插入一个word2[j-1]字符，w1[i]=w2[j-1],比较w1[0...i-1]与w2[0...j-2]
2. 删除dp[i-1][j], word[i-1]删除，比较w1前面的
3. 替换dp[i-1][j-1]
选择最小的步骤+1

#### L84 直方图最大矩阵

#### L85 0/1矩阵
###### DP解法

matrix
0 0 0 1 0 0 0
0 0 1 1 1 0 0
0 1 1 1 1 1 0

height
0 0 0 1 0 0 0
0 0 1 2 1 0 0
0 1 2 3 2 1 0

left
0 0 0 3 0 0 0
0 0 2 3 2 0 0
0 1 2 3 2 1 0

right
7 7 7 4 7 7 7
7 7 5 4 5 7 7
7 6 5 4 5 4 7

result
0 0 0 1 0 0 0
0 0 3 2 3 0 0
0 5 6 3 6 5 0

1. 从第一行开始，一行一行处理矩阵；针对每个点(i,j)
求出以(i,j)为底的高度height[j]
并针对此高度，求出该坐标点以此高度向左可延伸的最左边位置left[i]的位置和向右可延伸的最右边位置right[i];
因此，此坐标的最大面积为(right[i]-left[i] + 1)*height[i];
遍历过程中最大面积即为最终结果。

left/right说明：
如(1, 3)坐标处值为1，高度为2：(0,3)->(1,3)
则(1,3)处高度为2，向左扩展，只有自己高度才可以为2。
向右扩展，也只有自己高度才能为2

2. 设置3个dp，分别表示每行j位置处height\left\right
height
height(i,j) = height(i-1,j) + 1, if matrix[i][j]=='1';
height(i,j) = 0, if matrix[i][j]=='0'

left: 从左到右 cur_left = 0
left(i,j) = max(left(i-1,j), cur_left) if matrix[i][j]=='1';
left(i,j) = 0, cur_left = j+1 if matrix[i][j]=='0'

上一行j位置处left坐标和本行当前left可延伸的坐标cur_left中取最大；最大表示只取最满足

rigth: 从右到左 cur_right = n - 1
rigth(i,j) = max(rigth(i-1,j), cur_right) if matrix[i][j]=='1';
rigth(i,j) = n - 1, cur_right = j - 1 if matrix[i][j]=='0'

上一行j位置处right坐标和本行当前right可延伸的坐标cur_right中取最小；最大表示只取最满足

初始化
height=0 left=0 right=n

https://leetcode.com/problems/maximal-rectangle/discuss/29054/Share-my-DP-solution



###### 栈解法
1. 将0/1矩阵变成高度数组，再使用L84解法

#### L87 字符串解析成二叉树


#### L90 字符串解析成数字个数
dp[i]: 字符串前i个数字s[0...i-1]可解析成数字的个数
当s[i-1]数字在1～9之间，dp[i]+=dp[i-1]
当s[i-2...i-1]数字在10～26之间，dp[i]+=dp[i-2]

初始
dp[0]=1 dp[1]=1

#### L96 1～n二叉查找树个数
1. dp[i]表示节点个数为i的BST个数；
对于n个元素；当根节点为i时，左子树一定为[1...i-1]共i-1个元素
右子树节点一定为i+1~n给你个n-i个元素
因此dp[n,i]=dp[i-1]*dp[n-i]
根节点从1～n，即可得到dp[n]

#### L95 1～n二叉查找树个数2
在96上返回所有BST的根节点
设置一个函数gen(s,e)生成[s,e]中所有BST的根节点集合
因此，将[s,e]中每一个当作根节点，递归生成左右子树根节点集合；
每次从左右子树根节点集合取一个构建新的根节点加入到集合中返回。

https://www.cnblogs.com/grandyang/p/4301096.html


#### L97 s1和s2是否能组成s3
dp[i][j]表示s3前i+j位s3[0...i+j-1]是否能由s1前i位s1[0...i-1]和s2前j位s2[0...j-1]组成

初始
+ dp[0][0] = true
+ dp[i][0] = s1[i-1] == s3[i-1] && dp[i-1][0]
+ dp[0][j] = s2[j-1] == s3[j-1] && dp[j-1][0]

递归
1. 若s1[i-1]=s3[i+j-1]，则s3最后一位使用s1
dp[i][j] = d[i-1][j]
2. 若s2[j-1]=s3[i+j-1]，则s3最后一位使用s2
dp[i][j] = d[i][j-1]
https://www.jianshu.com/p/0f31410d0613

优化 DFS & BFS ***
https://www.cnblogs.com/grandyang/p/4298664.html


#### L115 s字符串表示T的个数
dp[i][j]表示s[0...i-1]组成t[0...j-1]的个数
初始
dp[0][0] = 1
dp[i][0] = 1, i > 0
dp[0][j] = 0, j > 0

递归
首先，舍dp[i][j] = dp[i-1][j]一定成立, 是由于s前i位组成t前j位的个数一定包含s前i-1位包含t前j位的个数；
且若s[i-1]=t[j-1]，则还包含s前i-1位包含t前j-1位的个数dp[i-1][j-1]

https://www.cnblogs.com/higerzhang/p/4133793.html

#### L120 数字三角形路径和
dp[i][j]:第i层第j个节点的最小路径和，首层设为0
从底向上寻找，由于从(i,j)只能到(i+1, j)和(i+1, j+1)
因此 dp[i][j]=min{dp[i+1][j], dp[i+1][j+1]}+A[i][j]

初始
最底层 dp[n][j] = A[n][j]

省去层数即dp[j]=min{dp[j], dp[j+1]}+A[i][j]
{}内部还是下一层的值，dp[j]表示某层j位置的最小路径
最终结果一定为dp[0]，第0层只有一个[0][0]

#### L121 买卖股票
1. 遍历时更新当前最小值min；
若当前值-min>最大利润maxPro,则更新最大利润

2. dp[i] = max{dp[i-1], A[i]-min}

#### L122 买卖股票2 - 多次买卖
1. 只要prices[i+1]>prices[i],就收获利润

#### L121 买卖股票3 - 2次买卖
1. 设dp[i][j]表示第j天进行i次买卖(一定卖出)的最大收益
假设第j天没卖出，一定是j-1天前卖出，dp[i][j-1]
假设第j天卖出，则买入时间设置为k(1~j-1), 收益为p[j-1]-p[k-1] + dp[i-1][k-1]

dp[i][j]=max{dp[i][j-1], p[j-1]-p[k-1] + dp[i-1][k-1]}
k:[1~j-1]

初始dp[0][j] = 0 dp[i][0] = 0 无需初始化

https://leetcode.com/problems/best-time-to-buy-and-sell-stock-iii/discuss/135704/Detail-explanation-of-DP-solution

2. 使用两个dp数组，一个表示当在第i天卖出的局部最优，一个表示在i天前完成交易的全局最优。

https://www.cnblogs.com/grandyang/p/4281975.html


2. 解法 基于L121分两段求解最大利润
f[i]:(0~i)之间一次最大利润
g[i]:(i~n-1)间的一次最大利润
f[i]求法和l121一致，g[i]从尾向前遍历，设置后面的最大峰值peek，减去当前值为利润；
因此,profit=max{f[i]+g[i]}
https://soulmachine.gitbooks.io/algorithm-essentials/java/dp/best-time-to-buy-and-sell-stock-iii.html

#### L131 回文串分割

#### L132 回文串分割2
1. 基于判断回文串l5，设dp[i]表示前i个字符的最小切割次数
对于i，分割成两端[0...j-1] [j...i-1]
若[j...i+1]可回文，则dp[i]=dp[j]+1 ifs[j..,i-1]回文 j=0~i-1


### 栈
#### L84 直方图中的最大面积
1. 解法1:
寻找直方图中局部峰值（当前数字大于下一个数字），以局部峰值为终点向前遍历，计算所有的矩阵面积，每次保留最大值；从局部峰值处理是由于局部峰值高于前面非峰值的数字，因此非局部峰值的情况局部峰值都包括。
（两个局部峰值之间都是递增数列，但均小于两个峰值，如[7 2 4 5 6 1] 7和6为局部峰值，2 4 5为中间。
如下，以6为终点，向前遍历到[2 4 5]；肯定包括5/4/2为终点的矩阵。

2. 解法2:单调栈
使用单调递增栈保存一个局部峰值区域；
遍历数组（数组末尾添加0，使得最后一个数字一定为局部峰值）；当前数字大于栈顶元素，则将当前下标入栈；
若小于，说明当前栈顶元素即为一个局部峰值，栈中保存的都是小于该局部峰值的区域；弹出栈顶下标t；以t高度为矩阵高度；
i-1表示局部峰值坐标，为终点下标，栈顶下标为小于h[t]的下标即为起点的前面；因此长度为i-1-stack.peek(),此时面积为h[t]*(i-1-stack.peek()).
若弹出后，栈为空；表明前面所有高度都大于h[t]，则起始为0，长度为i。
https://www.cnblogs.com/grandyang/p/4322653.html


### L146 LRU实现

http://www.noteanddata.com/leetcode-146-LRU-Cache-java-solution-note-2.html