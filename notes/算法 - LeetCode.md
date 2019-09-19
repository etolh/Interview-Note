### 一.DP
#### L5: 最长回文子串
dp[i][j] = true, if s[i...j]为回文串; 否则为false <br>
递归：
dp[i][j] == (s[i] == s[j]) && dp [i+1][j-1]

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

#### L87 字符串解析成二叉树 **
将一个字符串解析成二叉树，交换二叉树节点，使其成为乱序字符串
如great->rgeat，判断两个字符串是否为乱序

1. 递归
对于字符串s1切分为s1[0...i-1]和s1[i...]两部分，同样s2也切分成s2[0...i-1]和s2[i...]两部分，递归判断s1左边是否与s2左边，s1右边是否与s2右边匹配；s1左边是否与s2右边（i)，s1右边是否与s2左边匹配(len-i)；
判断两个字符串是否相等，可以用hash计算字母个数，若一致则相等。

切分长度从1～len-1

2. dp
设dp[i][j][k]表示s1起始i和s2起始j长度为k是否为匹配。
为维护递推式，寻找子序列；
将s1[i...i+len s2[i...i+len]]划分为两半，
根据dp比较s1两边是否和s2两边为匹配。
 res[i][j][len] = (res[i][j][k] && res[i+k][j+k][len-k] || res[i][j+len-k][k]&&res[i+k][j][len-k]) k=1...len-1

https://blog.csdn.net/Jin_Kwok/article/details/51136509
https://www.cnblogs.com/grandyang/p/4318500.html

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

2. 使用两个dp数组，g[i]表示当在第i天卖出的局部最优[0,i]，s[i]表示在i天后完成交易的全局最优[i, end]。
最大的g[i]+s[i]即为最终教育

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

#### L139 word break
dp[i]表示s前i个可以由dict组成
dp[i] = dp[j] && s[j...i)在worddict中

#### L140 word break 2
DFS： 找到所有可能的组合
对于worddict中所有的word，若s以word为开始，剩余s[word.length()...)以递归方式获取剩下的组合，即当前word+剩余组合即为结果。
同时，为进行剪枝，使用map保存当前s与可能组合的映射；当存在直接获取。
注意：最后一个单词后面不用空格

#### L152 最大连续积
p[i]: 以i为底的最大乘积
n[i]: 以i为底的最小乘积
注意负数问题
当遍历到i, 最大数在a[i] a[i]*p[i-1] a[i]*n[i-1]产生
p[i] = max{a[i],a[i]*p[i-1],a[i]*n[i-1]}
同理,最小数也在其中，因此n[i] = min{a[i],a[i]*p[i-1],a[i]*n[i-1]}
同时保存全局最大值
p[i]和n[i]都可以以变量表示

#### L174 最少出发血量
逆序，设dp[i][j]为从(i,j)出发的最少血量。则其最少血量由下边或右边格子决定，他们的最少血量-当前格子消耗血量
dp[i][j] = Math.min(dp[i][j + 1], dp[i + 1][j]) - dungeon[i][j];
由于血量一定为正,至少为1 dp[i][j]=max(1, dp[i][j])

注意，最下边和最右边的边界，只有一个来源。

https://www.cnblogs.com/grandyang/p/4233035.html

#### L188 买卖股票4 ***
1. 买卖股票3的一般形式
使用局部最优和全局最优思想
local[i][j]: 在第i天且最后一次卖出在i最多进行j次交易的最大利润
global[i][j]: 在第i天最多进行j次交易的最大利润

diff = p[i]-p[i-1]
局部优化递推：
在第i天卖出且完成少于j次交易，分两种情况:
1. 在第i-1天买入，则前i-1天完成j-1次交易，g[i-1][j-1]+diff
2. 在第i-1天前买入，则可以当作k天买入(k < i-1),在i-1卖出，则收益为l[i-1][j],同时再在i-1买入，再在i卖出；即相当于在i-1不卖出，在i卖出；l[i-1][j]+diff

l[i][j]= max{g[i-1][j-1],l[i-1][j]} + diff

全局优化递推
分两种
1. 在i天卖出 l[i][j]
2. 不在i天卖出 g[i-1][j]
g[i][j] = max{l[i][j], g[i-1][j]}

注意，若存在

http://zhuixin8.com/2016/10/02/leetcode-188/


#### L198 house robber
1. dp[i]:截止到a[i]的最大金额
dp[i] = max{dp[i-1], dp[i-2]+a[i]}

dp[0] = a[0], dp[1] = max{a[0], a[1]}

#### L213 house robber2
1. 数组首位相连
划分为两个数组 [0, len-2] [1, len-1] 取最大

#### L213 矩阵最大方阵
1. dp[i][j]表示(i,j)处方阵的边长
dp[i,j] = min(dp[i-1,j] , dp[i, j-1] , dp[i-1, j-1]) + 1, Aij = 1

#### L264 丑数2
1. 设置2,3,5乘积的三个下标，每次选择最小


#### L270 n的平方数个数
dp[n] = min{1 + dp[n-i*i]}, i=1,...

#### L300 最长递增子串长度(不一定连续)
dp[n]:a[0...n]的递增子串长度
dp[n] = max(dp[j]) + 1; a[n] > a[j]

二分
使用一个长度len表示统计当前最长算到的长度，数组d[i]表示最长长度为i时该数组的最小数
从头开始遍历，遍历到i时，若此时长度为len,长度为len的最长数组最后一个数为d[len];
若a[i]>d[len],则表示此时最长数组再可以添加一个数，len++，d[len]=a[i]；
若a[i]< d[len], 表明当前数在最长数组中，则可以寻找最长数组中第一个大于等于a[i]的数j，说明长度为j的最长数组的最小数为a[i], 进行替换，dp[j]=a[i]; 

如d数组[1 2 5] len=3 此时a[i]=3 < 5
用3替换5，即长度为3的最长数组的最小值d[2]=5

由于d数组有序，可以使用二分查找找到a[i]在d中的插入位置。

扩展：d数组中保存的不是最长升序子序列，dp[i]表示i长度的最小下标。
有几位同学在评论中问到如何给出一个LIS而不仅是计算长度。具体的代码我没有写过，不过大概可以这么实现：更新B[i]的时候，把记下来数字在原来数组中的下标也记下来（被替换的数据保留在一个后备数组中）。等到得出 B[n] 了以后，用贪心算法往前回溯，每次找出B[i-1]对应后备数组中值小于B[i]、下标小于B[i]下标、且在该后备数组中下标最大的那个。


https://blog.csdn.net/u013178472/article/details/54926531
https://www.kancloud.cn/digest/pieces-algorithm/163625
https://www.felix021.com/blog/read.php?1587%E5%8F%AF%E6%98%AF%E8%BF%9E%E6%95%B0%E7%BB%84%E9%83%BD%E6%B2%A1%E7%BB%99%E5%87%BA%E6%9D%A5

#### L303 数组部分和
sum[i]: sum{a[0...i]}

#### L303 矩阵部分和
dp[i][j]: 矩阵(0,0)到(i.j)的和
dp[i+1][j+1] = dp[i][j+1] + dp[i+1][j] - dp[i][j] + matrix[i][j]

#### L309 买卖股票(带cooldown)
sell[i]第i天卖出时最大收益
buy[i]第i天买入时最大收益

sell:
第i天卖出 buy[i-1]+p[i]
第i天前卖出 sell[i-1]
sell[i] = max(buy[i-1]+p[i], sell[i-1])

buy:
第i天买入 sell[i-1]-p[i]
第i天前买入 buy[i-1]
buy[i] = max(sell[i-1]-p[i], buy[i-1])

初始
sell[0] = 0 buy[0] = -p[0]

优化:使用变量代替数组


#### L312 打气球
设dp[i][j]为打破[i,j]间气球的最大分数
假设最后打得气球是k，则前面[i,k-1]，后面[k+1,j]
dp[i][j]=max{dp[i][j], dp[i][k-1]+dp[k+1][j]+a[i-1]*a[k]*a[j+1]}
选择最大k

初始:dp[i][i]=a[i-1]*a[i]*a[i+1]
若直接采用遍历，注意从len=1开始，长度逐渐增加

实现方式二: 递归
max(nums, start, end)表示[s,e]间的最大分数，并使用dp作为cache.

https://www.cnblogs.com/grandyang/p/5006441.html


#### L321. Create Maximum Number **
从两个数组一共取k个数组成最大数

注意以下三种情况
1. k=0，一个都不取
2. k不大于其中一个数组长度时，可能是完全只在一个数组中取，也可能两个数组都取。
3. k均大于数组长度，两个肯定都要取。

假设从nums1取i个，从nums2取k-i个
i取值范围
若k>n2表示一定至少在num1取k-n2个，若k<= n2，则表明nums1可以不取
i最小值 max(0, k-n2)

若k< n1表明最多可以取k个，否则nums1最多可以取n1个
i最大值 min(k, n1)

一个重要问题是
从nums数组中取i个，数字最大。
dropNum=n-i表示需要丢弃的个数，用队列保存选择的数字，先将从nums最开始遍历，当drop>0且队列不空，队列中最后一个数字小于当前数字，则抛弃队列最后数字
，drop-1；直到队列中数字均大于当前数字或drop=0，此时再将当前数字加入到队列。
最终队列保存的数字即为结果。

对于nums1和nums2获取的最大数字，再进行组合。（归并排序）

总体，分别从num1取i，nums2取k-i个数字，对两者获取的最大数组进行组合。
i不断遍历，选择最大的数字.

https://blog.csdn.net/Xd_Yu/article/details/52348444
https://www.cnblogs.com/grandyang/p/5136749.html

#### L322. Coin Change
dp[i] = min{dp[i-j]}+1, j in coins

#### L338. Counting Bits
1~n中二进制1的个数
dp[i]:i二进制个数
初始dp[0]=0
使用全局变量near记录当前数cur最近的平方数，从1开始，更新near(cur&(cur-1)==0);
dp[i]=dp[i-near]+1

#### L343. Integer Break 整数拆分
将整数拆分成多个数之和，使得因子的乘积最大。

1. DP解法
设dp[i]将i拆分后的最大乘积，对于[1~i-1]中的任意数j，拆分后j和i-j
由于j和i-j是对称的，因此选择一个再拆分即可，如i-j再拆分 dp[i-1]
加上不拆分的选择j*(i-j)，最大值
dp[i] = max{dp[i], j*(i-j), j*dp[i-j]}
dp[2]=2 dp[3]=2

2. 规律分析
    + 数字2只能拆成 1+1，所以乘积也为1。
    + 数字3可以拆分成 2+1 或 1+1+1，显然第一种拆分方法乘积大为2。
    + 数字4拆成 2+2，乘积最大，为4。
    + 数字5拆成 3+2，乘积最大，为6。
    + 数字6拆成 3+3，乘积最大，为9。
    + 数字7拆为 3+4，乘积最大，为 12。
    + 数字8拆为 3+3+2，乘积最大，为 18。
    + 数字9拆为 3+3+3，乘积最大，为 27。
    + 数字10拆为 3+3+4，乘积最大，为 36。

可以看出从5开始，每个数字必须先分出3，直到最后剩下2或4（4拆分2+2没意义，无需继续拆分）
因此，对于n>5，先不断分出3，直到小于4

https://www.cnblogs.com/grandyang/p/5411919.html


#### L354. Russian Doll Envelopes
300最长升序子序列的二维形式
只不过是后一个元素的w和h都要大于前一个元素的w和h

方法1: DP
先对数组进行排序，根据宽度w和高度h递增排序
dp[i]表示第i个元素可套的娃娃
dp[i] = max{dp[i], dp[j]+1}
其中j满足j< i-1，且w[j]< w[i], h[j] < h[i]


方法2: 二分
使用len表示当前可套封的娃娃数, dp[len]表示len个套封娃娃的最小高度
同样对数组进行排序，先根据宽度w递增排序，再按高度h递减。
保证遍历时后面宽度一定大于等于前面宽度，不再考虑宽度问题；
只要考虑高度问题；

当遍历到i时，若a[i].h > dp[len] 表明当前高度大于第len个娃娃的高度，（宽度一定大），因此，可以再添加个娃娃；
若高度小于，则在dp数组中寻找第一个大于a[i].h的高度，替换。

https://blog.csdn.net/qq508618087/article/details/51619435


#### L357. Count Numbers with Unique Digits
统计: 1位数～n位数中独特数的个数
+ 一位数0～9 f(1)=10
+ 两位数10～99 f(2)=9*9
+ 三位数100~999 f(3)=9*9*8
+ k位数  f(k)=9 * [9 * 8 * ... * (11-k)]

即可进行统计，从二位数开始，每次利用上一次结果计算当前个数,累加

https://www.cnblogs.com/grandyang/p/5582633.html

#### L363. Max Sum of Rectangle No Larger Than K
1. 暴力
    + 基于304思想，设计sum[i][j]表示(0,0)到(i,j)的整数和
    因此 (r,c)->(i,j)部分和

2. 一维数组优化
    按照列遍历，使用L,R表示当前遍历列的起始和终止位置,计算当前所列的累加和。求出累加和数组中小于k的最大值（两次遍历）。
https://www.youtube.com/watch?v=yCQN096CwWM


3. 一维数组优化+low_bound
    求累加和的小于k的最大值，使用TreeSet直接得到最大值（一次遍历）

https://www.cnblogs.com/grandyang/p/5617660.html
http://bookshadow.com/weblog/2016/06/22/leetcode-max-sum-of-sub-matrix-no-larger-than-k/


#### L368. Largest Divisible Subset
类似300LIS，不过数据前后关系是整除，而不是大于
先对数组从小到大排序，为了方便获取数组，
dp逆序，dp[i]表示以a[i]为起点的数组的最大长度
同时使用parent[i]表示i的下一个数

i从len-1到0遍历，对于每一个i，j从i为起点到len-1
if nums[j]%nums[i]==0且dp[i]< dp[j]+1;
则更新dp[i]=dp[j]+1,设置parent[i]=j
同时使用全局遍历maxLen和maxId记录最大长度和最大长度起始Id,即可找到最长数组


### 数组

#### L

#### L34. Find First and Last Position of Element in Sorted Array

#### L39. Combination Sum

DFS遍历，先排序，以一个数为起点，遍历所有子节点；数的子节点就是数组排序后该数后面的数。

https://blog.csdn.net/happyaaaaaaaaaaa/article/details/50897809



#### L

#### L

#### L

#### L





### DFS

深度优先遍历：

从图中某一个节点v开始，访问与其相邻的节点w，然后继续访问与w相邻的节点，每访问后，将该节点设置为已访问；若某一节点无可访问的下一节点时，返回上一个节点，寻找其下一个未访问节点。

```java
  /**
  * 核心伪代码
  * d:深度
  */
	private void dfs(Node node, int d){
        if (isEnd(node)){   //  当前节点为空
            return;
        }
        // 对node进行一些操作
        visited[node] = true;   // 设置为已访问
        System.out.println(node);
    		// .... 操作
        for(Node nextNode : n.next()){  // n相邻的所有节点
            if(!visited[nextNode]){     // 未访问
                dfs(nextNode, d+1);
            }
        }
    }
```



####L98. Validate Binary Search Tree

dfs遍历过程中需要保存当前节点的前一个节点和后一个节点



#### L101.Symmetric Tree

递归判断左右子树是否对错



#### L114. Flatten Binary Tree to Linked List

将二叉树根据前序遍历顺序转换成链表。

方法1:

使用前序遍历保存所有节点，再构造成链表



方法2：

递归, 先递归将左子树转换成链表，转换根节点；再递归将右子树转换成链表，从根节点出发，找到叶子节点（以防节点为空），连接右子树。

```java
root.left=null
root.right=leftNode;
```



https://blog.csdn.net/fuxuemingzhu/article/details/70241424



#### L124. Binary Tree Maximum Path Sum

设置一个递归函数helper表示该节点单边（左、右、无）的最大路径和；一个全局变量ans保存最大路径。

若为空节点，返回0

递归获取左右子树的单边最大路径l,r；若l,r均小于0，则单边路径一个都不选，ans也一个都不选。

否则，单边路径选择l,r较大值，ans选择l,r中大于0的。



借鉴树的深度，定义好递归函数的含义。



#### L200. Number of Islands

1. dfs遍历

   每访问一个节点，设置访问标示为true，使用dfs上下左右遍历数组。

   全局遍历矩阵，每次启动dfs次数即为聚落个数。

   

2. Union-Find



#### L207. Course Schedule

构建图邻接矩阵或邻接数组，以BFS或DFS形式判断是否有环。（拓扑排序）

BFS

构建所有节点的入度树组，将所有入度为0的节点加入队列，当队列不空时，弹出对头元素，若不为空



### BFS

广度优先遍历

从一个节点v开始，访问与其相邻的节点w，然后继续访问v相邻的下一个节点，直到v相邻节点访问完毕；再访问w相邻的节点。即一层一层地访问节点。

```java
    
	private void bfs(Node node, int length){
        ArrayDeque<Node> queue = new ArrayDeque<Node>();
        boolean[] visited = new boolean[length];  // 顶点访问标记

        queue.push(node);
        visited[node] = true;

        while(queue.isEmpty()){
            Node cur = queue.pop();
            // 访问当前顶点,进行操作
            System.out.println(node);
						// .... 操作
            for (Node nextNode: cur.next()){    //node所有相邻节点
                if (!visited[nextNode]){
                    visited[nextNode] = true;
                    queue.push(nextNode);
                }
            }
        }
        
    }
```





### 回溯





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