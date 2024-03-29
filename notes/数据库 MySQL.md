# MySQL

## 一. 锁与事务实现

### MYISAM表锁
偏向MyISAM存储引擎，开销小，加锁快，无死锁；锁定粒度大，发生锁冲突的概率最高，并发度最低。
MYISAM在读表前自动对表加读锁，在写表前自动对表加写锁。

> 加锁: lock table t1 read/write;
1. 用户A给表A加了读锁之后，只能读表A，不能写表A（报错），也不能读写其他表（报错）。
2. 此时用户B(未加锁）可以读表A，可以读写其他表，但是写表A时会出现阻塞（未报错），直至用户A释放表A的锁之后才解除阻塞，执行命令。


### 事务并发带来的问题
1. 脏写(丢失修改）：两个事务都对一个数据进行了修改，一个事务的修改覆盖了另一个事务的修改。
1. 脏读（读-DB结果不一致）：在一个事务中，读取其他事务未提交的数据，其他事务回滚后，导致读到的数据与数据库中的数据不一致； 
2. 不可重复读（读-读结果不一致）：一个事务中多次读取相同记录结果不一致（另一事务对该记录进行增改删）；
3. 幻读（读-写，用写来验证读，结果不一致）：一个事务中读取某个范围内的记录，另一个事务在该范围内插入新的记录。

### 隔离级别
为解决这些出现的问题，Mysql通过设置事务间的隔离性减少并发问题
1. 读未提交(Read Uncommitted)：一个事务可以读取其他事务未提交的结果，出现脏读。
2. 读已提交(Read Committed)：一个事务读取到其他事务已经提交的数据。Oracle等多数数据库默认都是该级别，出现不重复读
3. 可重复读(Repeated Read)：确保同一事务的多个实例在并发读取数据时，会看到相同结果。可重复读。InnoDB默认级别。在SQL标准中，该隔离级别消除了不可重复读，但是还存在幻读。
4. 串行读(Serializable)：完全串行化的读，一个事务执行完毕后另一个事务才能执行，每次读都需要获得表级共享锁，读写相互都会阻塞。

![隔离级别](pics\db_tx_iso.jpg)

https://www.cnblogs.com/zhoujinyi/p/3437475.html

### MVCC
1. MVCC简单来说，就是保存数据在不同时间的版本，通过对比当前事务版本获取某一个版本的数据，从而实现并发控制，实现读已提交、可重复读的问题。

#### 逻辑实现
数据库每条记录都有三个隐藏列，其中两个和MVCC相关：创建记录的事务版本号 DB_TRX_ID 和 删除记录的版本号 DB_ROLL_ID
1. 当一个事务插入数据时，将新插入行的创建版本号设置为当前事务版本号，删除版本号设置为NULL
2. 当一个事务删除数据时，数据行被没有真正被删除，而是将删除版本号设置为当前版本号
3. 当一个事务更新数据时，先复制一条被修改的记录，将旧的记录的删除版本号设置为当前版本号，新的记录创建版本号也设置为当前版本号。

4. 当事务进行查询时，规则如下:
    1. 查找创建版本号早于当前版本号的记录：保证读取到的数据是当前事务开始前已经存在的数据，或是自身插入或修改的。
    2. 记录版本号要么是NULL，要么是大于当前版本号的：保证事务读取的记录在事务开始前未被删除
    只要满足这两条，才作为返回结果。

https://juejin.im/post/5c68a4056fb9a049e063e0ab#heading-2

#### 底层实现    
1. redo log：防止事务在执行过程中丢失问题，在事务执行期间将对数据集的修改也保存到redo log（buffer）中；当执行期间若故障或断电，则可以根据redo log恢复到最近一次提交。
2. undo log: 用于事务的回滚。在事务执行期间事务前面的状态保存到undo log（buffer）；若当前事务回滚，则根据undo log恢复到以前的状态。

3. 当事务提交时，redo log buffer中修改保存到磁盘redo log中。undo log中修改undo日志(修改、删除）以版本链形式保存，用于实现MVCC，插入undo日志可被覆盖。


### redo log
1. redo log是用于实现事务的持久化，redo log会把事务在执行过程中对数据库的所有修改都记录下来，在系统崩溃重启后将事务前面的修改重新恢复出来。
2. redo日志顺序写入磁盘，避免了随机IO。


#### 具体过程
1. redo log记录对数据库的修改，每进行一次修改时，会生成一条redo log，保存修改的表空间、页号和具体内容。

2. 一条sql语句可能会修改多个数据库页面，比如插入一条语句时当插入页空间充足时只会修改当前页，若不足，会页分裂操作，将当前页分裂成两个页同时还要插入一条新的页目录项，页目录项的插入也可能会引起页的分裂，因此修改多个页面，产生多个redo日志，这样的多个redo日志称为一组redo日志。

3. 在崩溃恢复时，一组redo日志必须以原子性的恢复才能保证正确性（Mini-Transaction：对底层页面的一次原子访问过程）

#### redo日志同步过程
1. redo日志先写入到内存中redo日志缓存区中，再同步到磁盘中redo日志块中。缓冲区和磁盘都是划分为一个个512字节块。

2. 同步磁盘时机：
    1. 缓冲区buffer空间不足
    2. 事务提交
    3. 后台线程不停刷新（每秒）
    4. 服务器关闭

#### 写入redo日志到缓冲区
1. redo日志缓冲区维持了两个全局变量log sequence number日志序列号(lsn)和buf_next_to_write。其中lsn表示写入到缓存区中redo日志的字节数（初始值8704)。next_to_write记录缓冲区已经刷新到磁盘的redo日志字节数；当buf_next_to_write=lsn表示所有的redo日志都刷新到磁盘。

2. 在事务修改数据库时，会将被修改的页（脏页）插入到flush链表中，每个脏页有两个变量,用于记录脏页在修改前后的lsn。

#### checkpoint
1. 事务不断修改数据库，导致redo日志不断增加；因此需要删除已经无用的redo日志。当redo日志对应的脏页已经被刷新到磁盘，则表明该redo日志已经无用，即当前redo日志占用空间可被覆盖。
2. 根据flush链表计算缓冲区中已经刷新到磁盘的字节数lsn.
3. 磁盘中的redo日志文件维护了checkpoint_lsn，表示设置为计算得到的lsn。


#### 崩溃恢复
1. 根据磁盘中redo日志文件checkpoint_lsn，找到最近发生的checkpoints（最大的），确定恢复起点，将恢复起点lsn后的所有redo日志顺序扫描，根据记录的修改将对应的页面回复过来。

2. 加快过程：
    1. 使用hashtable对表空间和页号计算哈希值，将对同一个页进行修改的redo日志放在一起执行，避免很多随机操作。
    2. 跳过已经刷新到磁盘的页面。

### undo log
1. undo日志用于事务回滚时将事务对数据库的修改恢复到数据库原来的状态。

2. 事务id：当一个事务对数据库进行修改（增删改），Mysql会为该事务分配一个全局事务id。

3. 记录有三个隐藏列，row_id是未设置主键且没有unique列时分配的主键id，trx_id记录对该记录进行修改的事务id,roll_pointer指向记录的undo日志。

### undo日志
1. 插入，undo日志记录插入的记录id，恢复时删除该记录。
    新插入的记录trx_id记载当前事务id，pointer执行插入时的undo日志。
    
2. 删除，undo日志记录被删除的记录id和相关内容，恢复时重新插入，
    事务删除数据首先将记录的删除标识设为1，提交事务后，再将记录移入到垃圾链表（不是真正删除）。
    因此，在事务执行阶段只是修改删除标识，同时生成一个undo日志，undo日志记载了被删除的内容，同时还和记录一样，保存trx_id和pointer。
    在修改标识前，将记录的事务id和指针保存到undo日志中，

3. 修改，undo日志记录id和修改前的旧值，恢复时恢复旧值。
    修改不更新主键时，会生成一条update undo日志，记录修改属性的原来值，以及事务id和指针。
    当修改主键时，实际时生成一条delete undo日志和一条insert undo日志，delete undo日志删除原来主键记录，insert undo日志表示插入新的主键记录。

#### 磁盘： undo页面 - undo段（页面链表） - 回滚段
1. undo日志分为两种类型，插入一种类型，删除和修改作为一种类型。同一个undo日志类型保存到同一个undo页面中，undo页面以链表形式表示。
2. 每一个undo页面链表对应一个段，即链表中的页面都是从段中申请，段中第一个页面保存着段相关信息，每一个undo页面保存着该页面内undo日志相关信息。
3. 回滚段保存着所有undo段中的第一个页面，通过这个页面可以找到该undo段；也可以从回滚段中申请页面建立新的undo段。
4. 一个回滚段有1024个undo槽，对应1024个undo段，即可建立1024个undo页面链表。
5. mysql中一共有128个回滚段， 0/33~127系统表空间  1~32临时表空间

6. 为什么分普通表和临时表：
    1. 对数据库的修改需要记录redo日志，因此undo页面写入undo日志也是一个写过程，同样要记录redo日志。
    2. 在修改针对普通表的回滚段中的Undo页面时，需要记录对应的redo日志，而修改针对临时表的回滚段中的Undo页面时，不需要记录对应的redo日志。


### MVCC实现原理
1. 设置隔离级别 SET [GLOBAL|SESSION] TRANSACTION ISOLATION LEVEL level;
2. MVC是通过Undo日志和ReadView来实现的，Undo日志保存记录中原来的值，ReadView控制哪个版本对当前事务是可见的。

3. 如上所示，每个用户记录都有trx_id和roll_pointer列，事务id表示对数据修改的事务id，pointer指向undo日志；在修改数据记录时，生成一条新的undo日志时，会将记录中事务id和指针写入到新的undo日志中，因此undo日志会通过指针指向上一个undo日志，形成一个版本链。

4. 版本链的头节点就是当前记录的最新值，后面每个版本都是一个undo日志，保存着该版本对应的事务id和当前版本值。

4. 针对读未提交，每次读取的都是记录的最新值，无需判断版本。
5. 对于串行化，每次读写记录都需要加锁。

6. 对于读已提交和可重复读，通过MVCC必须保证读取已经提交的记录

#### 细节
1. MVCC通过ReadView判断版本链中哪个版本是当前事务可见的。

2. 事务在从开始到提交前，会被保存到一个事务链表中；一旦提交，则会从链表中移除。

3. ReadView主要有4个主要字段，m_ids保存当前活跃的事务id列表，min_trx_id表示活跃列表中最小事务id,max_trx_id表示活跃事务列表中最大事务id，create_id表示创建ReadView的事务id。

4. 当事务某个语句访问版本链时，会根据readview中字段判断版本是否可见，逻辑如下:
    1. 若被访问版本(undo log)的id与ReadView中create_id一致，表示版本是当前事务修改的，可见。
    
    2. 若被访问版本的id小于ReadView中最小id时，表明该版本在当前事务创建ReadView前就已经提交，可见。
    
3. 若被访问版本的id大于ReadView中最大id时，表明该版本在当前事务创建ReadView后才开启，不可见。
    
    4. 若被访问版本的id在ReadView中活跃id列表时（不为create_id)，表明创建该版本的事务还是活跃的，当前事务不可见；若不在，表明该版本事务已被提交，可见。
    
       
    
5. 


https://juejin.im/book/5bffcbc9f265da614b11b731/section/5c923cfcf265da60f00ecaa9
https://zhuanlan.zhihu.com/p/40208895

### 快照读与当前读

MySQL存在两种读方式，快照读和当前读
快照读是普通的select语句，通过MVCC和undo log读取历史版本数据；
当前读是select lock ... in share mode/for update读取最新的数据，通过next-key lock，也就是gap锁和行锁来实现的。

MVCC通过undo日志和ReadView能够解决部分的幻读，
一个事务在第二次读取前，另一个事务插入记录并提交，该事务由于是在第一次读取就生成ReadView，因此不会读取到插入的记录。但当该事务想要插入同样主键的记录，会发现异常，已经存在该记录。（幻行）

若要完全避免，需要手动加锁，以当前读next-key方式读取记录，next-key是行记录锁Record和Gap锁的结合，Record锁能够锁住当前记录的索引，Gap锁锁住当前记录与前一个记录索引间的间隙，避免其他线程在间隙中插入数据。
因此，在当前读时，next-key锁住当前记录的索引和前面的间隙，使得其他线程无法插入记录，只能等待当前事务执行完毕，从而完全避免幻读现象。

注意：
事务开启后，若先快照读，其他事务仍然能够插入记录。
若当前读，则先再间隙加锁，从而保证其他事务无法操作。

https://www.cnblogs.com/twoheads/p/10703023.html
https://blog.csdn.net/silyvin/article/details/79280934
https://blog.51cto.com/11819159/2128910


### 锁分类
1. 共享锁S 
2. 排他锁X

#### 锁结构
1. 事务对某条记录或表添加锁，实际上是生成一个锁结构，结构中有trx_id表示创建锁的事务id,
is_waitting表示是否等待，若是false，则表示获取锁成功，若为true，表示获取锁失败，阻塞等待。

#### 隐式锁
1. 每一个聚簇索引记录有一个trx_id隐藏列，记录最后改动该记录的事务id。若其他事务想要对这条记录添加S锁或X锁，会检查记录的trx_id是否为活跃的事务id，若是，则帮当前事务创建一个X锁（锁结构，is_waitting为false),同时自己进入等待状态（也为自己创建一个锁结构）