[TOC]

网站～



# 并发

## 一. 线程
### 1. 进程与线程
1. 程序是一个静态概念，即一段代码，包含数据与执行逻辑。
2. 进程是程序在数据上的一次执行过程，进程是系统进行资源分配的最小单位，线程是进程内部的一个执行序列，是CPU调度和执行的最小单位。
3. 线程自己基本上不拥有系统资源,只拥有一点在运行中必不可少的资源(如程序计数器,一组寄存器和栈),但是它可与同属一个进程的其他的线程共享进程所拥有的全部资源。
4. 一个线程可以创建和撤销另一个线程;同一个进程中的多个线程之间可以并发执行。

区别归纳：
1. 解释：进程是资源分配的最小单位，线程是程序执行的最小单位。
2. 切换开销：进程有自己的独立地址空间，每启动一个进程，系统就会为它分配地址空间，建立数据表来维护代码段、堆栈段和数据段，这种操作非常昂贵。而线程是共享进程中的数据的，使用相同的地址空间。因此CPU切换一个线程的花费远比进程要小很多，同时创建一个线程的开销也比进程要小很多。
3. 通信：进程间通信IPC，线程间可以直接读写进程数据段（如全局变量）来进行通信，注意需要线程同步和互斥手段的辅助，以保证数据的一致性。
4. 独立性：进程拥有自己的独立空间，进程间相互独立。各线程间共享进程的内存空间，必须依赖进程存在，某进程内的线程在其它进程不可见。

### 2. 线程创建
创建线程有三种方式：
1. 继承Thread类创建线程：继承Thread类（Thread类实现Runnable接口），重写run方法，表示要执行的任务；创建Thread子类对象，并调用start方法启动线程。
2. 实现runnable接口创建线程：实现runnable接口，实现run方法定义具体任务。创建runnable实现类的对象，并将其插入到Thread构造函数，创建Thread子类对象，调用start方法启动线程。
3. Callable+Future模式
    1. 定义Callable接口的实现类，并重写call方法，将其作为线程的执行体，同时具有返回值。
    2. 创建Callable任务对象，提交到线程池ExectorService执行submit,返回Future子类对象
    3. 通过Future子类对象获取结果。
4. Callable+FutureTask模式
    1. 定义Callable接口的实现类，并重写call方法，将其作为线程的执行体，同时具有返回值。
    2. 创建Callable任务对象，传入到FutureTask类并创建对象
    3. 使用Thread线程或线程池执行FutureTask任务对象
    4.  调用FutureTask对象的get方法获取call任务的返回值。

分析：
1. 采用callable和runnable接口创建线程，其优点是：将任务对象和线程对象分开，Thread子类对象只是封装callable和runnable接口对象，还可以继承、实现其他接口和类。同时，多个线程可以共享一个任务对象，适合多个线程处理一个同一个资源的情况。缺点是：编程稍微复杂，若在run或call方法想获取当前线程，必须使用Thread的currentThread方法。
2.	继承Thread类，优点是：实现简单，直接使用this即可获取当前线程。Thread子类对象已经继承Thread类，无法再继承其他类。
3.	采用Callable接口，执行体可以返回值，从而进行比较。

#### 2. Callable Future与FutureTask详解
Callable是个接口，声明了call方法，泛型接口，call返回结果类型是传入的类型。
```java
public interface Callable<V> {
    V call() throws Exception;
}
```
Future可以对具体的Runnable或Callable任务提供三种功能：
1. 获取任务执行状态：完成/终止
2. 取消任务
3. 获取任务执行的结果

```java
public interface Future<V> {
    // 取消任务
    boolean cancel(boolean mayInterruptIfRunning);
    // 查看任务状态
    boolean isCancelled();
    boolean isDone();
    V get() throws InterruptedException, ExecutionException;
    // 在指定时间内获取任务结果，若失败返回null
    V get(long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException;
}
```
##### FutureTask
FutureTask实现RunnableFuture接口，该接口继承了Runnable和Future接口；
因此FutureTask类既可以作为任务被线程执行，又可以作为Future获取结果。
构造方法：
```java
    public FutureTask(Callable<V> callable);
    public FutureTask(Runnable runnable, V result);
```

执行案例: Callable+Future模式 & Callable+FutureTask模式

https://www.cnblogs.com/dolphin0520/p/3949310.html


### 3. 线程的状态
线程一共有5种状态：
1.	新建：线程被创建后，即进入了新建状态。
2.	就绪：也被称为“可执行状态”，线程被创建后，其他线程调用了该线程对象的start方法，随时可获取CPU时间运行。
3.	运行：线程获取CPU时间运行。注意，线程只能由就绪状态到运行状态。（可以调用Thread.yield方法让出时间片退回到就绪状态，但注意让出时间片不是强制，而是看当前线程意愿）
4.	阻塞状态：阻塞是由于线程由于某种原因放弃CPU使用权，停止运行。直到重新进入就绪状态，才有机会转到运行状态。阻塞有三种：
    1. 阻塞Blocked：线程竞争synchronized锁或其他锁失败，阻塞进入到同步阻塞队列。直到其他线程释放锁，才有机会获取锁，变成就绪状态。
    2. 等待Waitting：线程获取锁后，因为某种条件不满足，调用锁对象的wait方法，进入到等待阻塞队列，并释放锁。直到通知线程完成条件，调用锁对象的notify/notifyAll方法通知等待线程，等待线程才能从等待阻塞状态转到同步阻塞，重新竞争锁。
    3. 超时等待Time_Waitting：调用线程的sleep()或join()或发出了I/O请求时，线程会进入到阻塞状态。当sleep()状态超时、join()等待线程终止或者超时、或者I/O处理完毕时，线程重新转入就绪状态。
5.	死亡状态：线程执行完毕或因异常退出，生命周期结束。

    ![线程状态](pics\java_cc_thread_state.png)

### 4. 线程方法
[pic]

1. Thread.sleep():让当前线程睡眠指定毫秒数，进入超时等待，但不释放锁；睡眠结束进入就绪状态，抛出InterruptedException。

2. join(): 当前线程中调用其他线程对象的join方法，如t1.join()，当前线程进入阻塞状态，执行其他线程t1，当其他线程执行完毕，当前线程才可运行。注意，当前线程不释放锁。 <br>
join方法的作用是将分出来的线程合并回去，等待分出来的线程执行完毕后继续执行原有线程。类似于方法调用。（相当于调用thead.run()）

3. Thread.yield(): 让当前正在执行的线程放弃CPU时间片，重新进入就绪状态。 <br>
作用：让相同优先级的线程轮流执行，但并不保证一定会轮流执行。实际中无法保证yield()达到让步目的，因为让步的线程还有可能被线程调度程序再次选中。Thread.yield()不会导致阻塞，而是就绪。

4. interupt：当调用一个线程对象的interrupt方法时，会将该线程的中断标志置为true；同时若该线程处于阻塞状态（调用了可以抛出InterruptedException的方法），则该线程会抛出一个InterruptedException异常，同时将中断标志清除设为false； <br>
若线程方法中没有调用抛出InterruptedException的方法，则可以通过isInterrupted检查中断标识来判断它是被中断，若被中断再执行相应操作。
若调用了，则在catch异常处理阶段进行相应处理。
即调用线程的interrupt方法，只是将线程中断标志置位，同时若线程处于阻塞状态，会抛出InterruptedException异常。不会停止线程，线程是否停止根据自己判断，在中断标志被置位或抛异常后进行什么逻辑。
```java
public class InterruptTest {
    static class MyThread extends Thread{
        public synchronized void run(){
            System.out.println(Thread.currentThread().getName() + "运行");
            /*
            // 无阻塞方法，自己判断中断位
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("线程未中断运行");
            }
            System.out.println("线程被中断");
            */
            // 有阻塞方法，在catch进行相应处理
            try {
                System.out.println("进入睡眠状态");
//                Thread.currentThread().sleep(10000);
                Thread.currentThread().wait();
                System.out.println("睡眠完毕");
            } catch (InterruptedException e) {

                System.out.println("得到中断异常" + Thread.currentThread().isInterrupted());
            }
            System.out.println("run方法执行完毕");
        }
    }

    public static void main(String[] args) {

        System.out.println("主线程运行");
        MyThread t1 = new MyThread();
        t1.start();
        try {
            Thread.currentThread().sleep(2000);
        } catch (InterruptedException e) {

        }
        // 调用线程对象interrupt方法
        t1.interrupt();
    }
}
```

5. setDaemon(boolean on):将线程转换为守护线程,on=true表示将当前线程标记为守护线程，必须在线程启动前设置。
守护线程的唯一用途是为其他线程提供服务。比如计时线程，它定时发送信号给其他线程；
当只剩下守护线程时，JVM就退出了。
守护线程应该永远不去访问固有资源，如文件、数据库，因为它会在任何时候甚至在一个操作的中间发生中断。
注意！Java虚拟机退出时Daemon线程中的finally块并不一定会被执行。

https://www.cnblogs.com/dolphin0520/p/3920357.html

## 二. 线程安全性
### 1. 线程安全与同步
1. 线程安全是指某个函数、对象等在多线程环境下执行，能够正确地处理多线程之间的共享变量，保证程序能够正常执行。
2. 《深入Java虚拟机》：当多个线程访问同一个对象时，如果不用考虑这些线程在运行时环境下的调度和交替运行，也不需要进行额外的同步，或者在调用方进行任何其他的协调操作，调用这个对象的行为都可以获取正确的结果，那这个对象是线程安全的。
3. 线程同步：多线程执行过程中，为保证共享变量的正确性，采用同步机制保证一个时刻只有一个线程才能对共享变量操作，其他线程只有等操作完毕后才能操作。

4. 线程安全是一种状态，线程同步是实现线程安全的一种手段。（线程安全是结果，线程同步是手段）

https://zh.wikipedia.org/wiki/%E7%BA%BF%E7%A8%8B%E5%AE%89%E5%85%A8
https://www.jianshu.com/p/44831d1d10d3


### 2. 并发编程问题
#### 线程开销[*]
多线程影响性能的首先是线程的上下文切换。当一个线程CPU时间片执行完毕，会保存该线程状态，切换到另一个线程执行。
减少上下文切换方法：
1. 无锁并发：通过某种策略（比如hash分隔任务）使得每个线程不共享资源，避免锁的使用。
2. CAS：是比锁更轻量级的线程同步方式
3. 避免创建不需要的线程，避免线程一直处于等待状态
4. 协程：单线程实现多任务调度，单线程维持多任务切换

**vmstat可以查看上下文切换次数
jstack 可以dump 线程信息，查看一个进程中各个线程的状态**

#### 死锁
1. 饥饿：
    1. 当线程由于无法访问它所需要的资源而不能继续执行时，就发生了饥饿（某线程永远等待）。引发饥饿的最常见资源就是CPU时钟周期。比如线程的优先级问题。在Thread API中定义的线程优先级只是作为线程调度的参考。在Thread API中定义了10个优先级，JVM根据需要将它们映射到操作系统的调度优先级。这种映射是与特定平台相关的，因此在某个操作系统中两个不同的Java优先级可能被映射到同一优先级，而在另一个操作系统中则可能被映射到另一个不同的优先级。
    2. 当提高某个线程的优先级时，可能不会起到任何作用，或者也可能使得某个线程的调度优先级高于其他线程，从而导致饥饿。
    3. 通常，我们尽量不要改变线程的优先级，只要改变了线程的优先级，程序的行为就将与平台相关，并且会导致发生饥饿问题的风险。

    4. 事务T1封锁了数据R,事务T2又请求封锁R，于是T2等待。T3也请求封锁R，当T1释放了R上的封锁后，系统首先批准了T3的请求，T2仍然等待。然后T4又请求封锁R，当T3释放了R上的封锁之后，系统又批准了T的请求......T2可能永远等待

2. 活锁
    1. 活锁是另一种形式的活跃性问题，该问题尽管不会阻塞线程，但也不能继续执行，因为线程将不断重复执行相同的操作，而且总会失败。活锁通常发生在处理事务消息的应用程序中。如果不能成功处理某个消息，那么消息处理机制将回滚整个事务，并将它重新放到队列的开头。虽然处理消息的线程并没有阻塞，但也无法继续执行下去。这种形式的活锁通常是由过度的错误恢复代码造成的，因为它错误地将不可修复的错误作为可修复的错误。

    2. 当多个相互协作的线程都对彼此进行响从而修改各自的状态，并使得任何一个线程都无法继续执行时，就发生了活锁。要解决这种活锁问题，需要在重试机制中引入随机性。在并发应用程序中，通过等待随机长度的时间和回退可以有效地避免活锁的发生。


### 3. 线程通信/同步工具使用
当多个线程访问某个状态变量，并有一个线程执行写入操作时，需要采用同步机制保证线程安全性。
同步机制包括synchronized内置锁、显式锁、volatile变量和原子变量。

#### 1. synchronized
synchronized用于对对象加锁，锁住方法或代码块，可以保证被它修饰的方法或代码块在任意时刻只有一个线程被访问
1. 一种使用方法是对代码块使用synchronized关键字
    ```java
    public void fun(){
        synchronized (this){ }
    }
    ```
    1. 括号中锁定的是普通对象或Class对象。
    2. 如果是this，表示在执行该代码块时锁定当前对象，其他线程不能调用该对象的其他锁定代码块，但可以调用其他对象的所有方法(包括锁定的代码块)，也可以调用该对象的未锁定的代码块或方法。
    3. 如果是Object o1，表示执行该代码块的时候锁定该对象，其他线程不能访问该对象（该对象是空的，没有方法，自然不能调用）
    4. 如果是类.class，那么锁住了该类的Class对象，只对静态方法生效。

2. 另一种写法是将synchronized作为方法的修饰符
    ```java
    public synchronized void fun() {} //这个方法执行的时候锁定该当前对象
    ```
    1. 每个类的对象对应一把锁，每个 synchronized 方法都必须获得调用该方法的一个对象的锁方能执行，否则所属线程阻塞，方法一旦执行，就独占该锁，直到从该方法返回时才将锁释放，此后被阻塞的线程方能获得该锁，重新进入可执行状态。

    2. 如果synchronized修饰的是静态方法，那么锁住的是这个类的Class对象，没有其他线程可以调用该类的这个方法或其他的同步静态方法。

    3. 实际上，synchronized(this)以及非static的synchronized方法，只能防止多个线程同时执行同一个对象的这个代码段。 

3. synchronized锁住的是括号里的对象，而不是代码。对于非静态的synchronized方法，锁的就是对象本身也就是this。

4. 获取锁的线程释放锁只会有两种情况：
    1. 获取锁的线程执行完了该代码块，然后线程释放对锁的占有；
    2. 线程执行发生异常，此时JVM会让线程自动释放锁。


#### 2. Lock
1. 锁是可重入的(reentrant),因为线程可以重复获得已经持有的锁。锁保持一个持有计数（hold count）来跟踪对lock方法的嵌套调用。线程在每一次调用lock都要调用unlock来释放锁。由于这一特性，被一个锁保护的代码可以调用另一个使用相同的锁的方法。
    ```java
    private Lock lock = new ReentrantLock();

    lock.lock();
    try {
        ...
    } finally {
        lock.unlock();
    }
    ```

#### 3. volatile
1. volatile修饰的变量，jvm虚拟机只是保证从主内存加载到线程工作内存的值是最新的。
2. JMM内存模型，每个线程都有自己的私有内存空间，修改共享数据时，先将数据从共享空间拷贝到私有空间；更新完毕后，再将更新的数据从私有空间拷贝到共享空间。
3. 内存可见性问题是，当多个线程操作共享数据时，彼此不可见。
4. 解决这个问题有两种方法：
    1. 加锁：加锁会保证读取的数据一定是写回之后的，内存刷新。但是效率较低
    2. volatile：会保证数据在读操作之前，上一次写操作必须生效，即写回。
        1. 修改volatile变量时会强制将修改后的值刷新到主内存中。
        2. 修改volatile变量后会导致其他线程工作内存中对应的变量值失效。因此，再读取该变量值的时候就需要重新从读取主内存中的值。
4. 相较于synchronized是一种较为轻量级的同步策略，但是volatile不具备互斥性；不能保证修改变量时的原子性。

#### 4. Atomic
1. Atomic原子类主要分为原子更新基本类、原子更新数组、原子更新引用类型、原子更新字段类，主要用于高并发环境下数据的原子性操作，简化同步处理。

### 4. ReentrantLock使用详解
#### 使用
1. 加锁与释放lock.lock()与unlock()
2. 尝试获取锁lock.trylock() 尝试获取锁,当获取失败则直接返回false不会等待;直接使用非公平锁的nonfairTryAcquire方法。
3. 超时获取lock.trylock(1000) 尝试获取锁,当超过时间，则直接返回false。
4. 可中断lock.lockInterruptibly() 获取锁期间被中断抛出异常。
```java
    ReentrantLock lock = new ReentrantLock();
    public void lockTest(){
        try {
            lock.lock();
            lock.tryLock();
        }finally {
            lock.unlock();
        }

    
        try {
            lock.tryLock(5L, TimeUnit.SECONDS); // 等待期间被中断，抛出异常
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }

        try {
            lock.lockInterruptibly();        // 等待期间被中断，抛出异常
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }
```
https://blog.csdn.net/weixin_41704428/article/details/80374312

#### 锁实现原理
ReentrantLock的锁功能主要是通过继承了AbstractQueuedSynchronizer的内部类Sync来实现的，其lock()获取锁的主要流程如下： <br>
1. ReentrantLock的lock方法会调用其成员内部类sync的lock方法
```java
    public void lock() {
        this.sync.lock();
    }

```
2. sync有两个实现类：公平锁FairSync和非公平锁NonfairSync实现了各自的lock方法,lock方法则调用了父类的acquire方法。
```java
    static final class NonfairSync extends ReentrantLock.Sync {
        private static final long serialVersionUID = 7316153563782823691L;

        NonfairSync() {
        }

        final void lock() {
            if (this.compareAndSetState(0, 1)) {
                this.setExclusiveOwnerThread(Thread.currentThread());
            } else {
                this.acquire(1);
            }

        }

        protected final boolean tryAcquire(int var1) {
            return this.nonfairTryAcquire(var1);
        }
    }

    static final class FairSync extends ReentrantLock.Sync {
        private static final long serialVersionUID = -3000897897090466540L;

        FairSync() {
        }

        final void lock() {
            this.acquire(1);
        }

        protected final boolean tryAcquire(int var1) {
            Thread var2 = Thread.currentThread();
            int var3 = this.getState();
            if (var3 == 0) {
                if (!this.hasQueuedPredecessors() && this.compareAndSetState(0, var1)) {
                    this.setExclusiveOwnerThread(var2);
                    return true;
                }
            } else if (var2 == this.getExclusiveOwnerThread()) {
                int var4 = var3 + var1;
                if (var4 < 0) {
                    throw new Error("Maximum lock count exceeded");
                }

                this.setState(var4);
                return true;
            }

            return false;
        }
    }
```
3. acquire方法在AQS中实现，通过tryAcquire()方法试图获取锁，获取到直接返回结果，否则通过嵌套调用acquireQueued()、addWaiter()方法将请求获取锁的线程加入等待队列，如果成功的话，将当前请求线程阻塞。tryAcquire又是由子类各自实现，定义了自己尝试获取锁的逻辑。
    + 模板模式
```java
    public final void acquire(int var1) {
        if (!this.tryAcquire(var1) && this.acquireQueued(this.addWaiter(AbstractQueuedSynchronizer.Node.EXCLUSIVE), var1)) {
            selfInterrupt();
        }
    }
```

不同锁实现自己lock和tryAcquire方法
##### 非公平锁
1. 非公平锁lock方法上来就无视等待队列的存在而抢占锁，通过基于CAS操作的compareAndSetState(0, 1)方法，试图修改当前锁的状态，这个0表示AbstractQueuedSynchronizer内部的一种状态，针对互斥锁则是尚未有线程持有该锁，而>=1则表示存在线程持有该锁，并重入对应次数，这个上来就CAS的操作也是非公共锁的一种体现，CAS操作成功的话，则将当前线程设置为该锁的唯一拥有者。
2. 抢占不成功的话，则调用父类的acquire()方法，按照上面讲的，继而会调用tryAcquire()方法，这个方法也是由最终实现类NonfairSync实现的。

3. tryAcquire：还是上来先判断锁的状态，通过CAS来抢占，抢占成功，直接返回true，如果锁的持有者线程为当前线程的话，则通过累加状态标识重入次数。抢占不成功，或者锁的本身持有者不是当前线程，则返回false，继而后续通过进入等待队列的方式排队获取锁。可以通过以下简单的图来理解：
```java
        final boolean nonfairTryAcquire(int var1) {
            Thread var2 = Thread.currentThread();
            int var3 = this.getState();
            if (var3 == 0) {
                if (this.compareAndSetState(0, var1)) {
                    this.setExclusiveOwnerThread(var2);
                    return true;
                }
            } else if (var2 == this.getExclusiveOwnerThread()) {
                int var4 = var3 + var1;
                if (var4 < 0) {
                    throw new Error("Maximum lock count exceeded");
                }

                this.setState(var4);
                return true;
            }

            return false;
        }
```

##### 公平锁
1. 公平锁的lock()方法就比较简单了，直接调用acquire()方法, acquire调用锁对tryAcquire()方法
2. tryAcquire：当前线程会在得到当前锁状态为0，即没有线程持有该锁，并且通过!hasQueuedPredecessors()判断当前等待队列没有前继线程（也就是说，没有比我优先级更高的线程在请求锁了）获取锁的情况下，通过CAS抢占锁，并设置自己为锁的当前拥有者，当然，如果是重入的话，和非公平锁处理一样，通过累加状态位标记重入次数。



https://blog.csdn.net/lipeng_bigdata/article/details/52154637

#### Condition条件对象
1. AQS对象内部有一个ConditionObject内部类，ConditionObject其内部维护了一个单向队列，保存了头节点和尾节点，节点类型是AQS同步队列的节点类型Node。
2. AQS子类对象Sync内部实现了一个创建并返回ConditionObject对象的方法newCondition，并进一步对ReentrantLock对象提供该方法，因此，通过ReentrantLock锁对象可以获取ConditionObject条件对象，其内部封装了表示等待队列的链表，当锁释放时，由于ConditionObject是AQS内部类，持有AQS对象的外部引用，因此，可以通过ConditionObject对象可以获取外部锁对象。

```java
public class ReentrantLock implements Lock {
   private final Sync sync;	// AQS子类对象
   abstract static class Sync extends AbstractQueuedSynchronizer {//AQS子类
       final ConditionObject newCondition() {
           return new ConditionObject();
       }
       // ... 为节省篇幅，省略其他方法
   }

   public Condition newCondition() {	// lock的获取Condition条件
       return sync.newCondition();
   }

   // ... 为节省篇幅，省略其他方法
}
```

3. 同时ConditionObject实现了Condition接口，Condition接口内部包含一套await/signal方法，类似await/notify方法；

```java
public interface Condition {
    void await() throws InterruptedException;
    void awaitUninterruptibly();
    long awaitNanos(long nanosTimeout) throws InterruptedException;
    boolean await(long time, TimeUnit unit) throws InterruptedException;
    boolean awaitUntil(Date deadline) throws InterruptedException;
    void signal();
    void signalAll();
}
```

4. 类似内置锁await/notify机制，线程首先需要获取锁，调用Condition对象的await/signal方法需要获取产生了该Condition对象的锁，

使用方式：
1. 等待线程：通过显示锁ReentrantLock对象产生Condition对象，在线程持有该显示锁对象的情况下，当条件不满足，调用Condition对象的await方法将线程插入等待队列，并释放锁。
2. 通知线程：获取显示锁，完成条件，调用Condition对象的signal方法通知所有等待条件的锁。
```java
Lock lock = new ReentrantLock();
Condition condition = lock.newCondition();
//等待线程的典型模式
public void conditionAWait() throws InterruptedException {
    lock.lock();    //获取锁
    try {
        while (条件不满足) {
            condition.await();  //使线程处于等待状态
        }
        条件满足后执行的代码;
    } finally {
        lock.unlock();    //释放锁
    }
}
//通知线程的典型模式
public void conditionSignal() throws InterruptedException {
    lock.lock();    //获取锁
    try {
        完成条件;
        condition.signalAll();  //唤醒处于等待状态的线程
    } finally {
        lock.unlock();    //释放锁
    }
}
```

与内置锁不同的是：
一个显示锁可以创建多个Condition对象，对应多条不满足相应条件的等待队列。
https://mp.weixin.qq.com/s?__biz=MzIxNTQ3NDMzMw==&mid=2247483917&idx=1&sn=6d074607603149b7e38b33a7bed7f417&scene=19#wechat_redirect

1. 同步队列：由于线程未获取到锁，将未获取锁的线程封装成Node节点，插入到同步队列中。
2. 等待队列：由于线程在执行过程中某个条件未满足（已获取锁），从而执行锁的await()方法，释放锁，并阻塞，插入到等待队列中等待条件完成。


#### 公平锁与非公平锁

当持有锁的时间相对较长或者请求锁的平均时间间隔较长，应该使用公平锁。在这些情况下，插队带来的吞吐量提升（当锁处于可用状态时，线程却还处于被唤醒的过程中）可能不会出现。

非公平锁可能会引起线程饥饿，但是线程切换更少，吞吐量更大


#### synchronize和lock区别 
1. 层次：前者是JVM实现，后者是JDK-AQS实现
2. 功能：synchronized仅能实现互斥与重入，后者可以实现可中断、可轮询、可定时、可公平、绑定多个条件、非块结构synchronized在阻塞时不会响应中断，Lock会响应中断，并抛出InterruptedException异常。
3. 异常：前者线程中抛出异常时JVM会自动释放锁，后者必须手工释放
4. 性能：synchronized性能已经大幅优化，如果synchronized能够满足需求，则尽量使用synchronized.

#### ReentrantLock特有功能：
1. 可以指定公平锁和非公平锁，synchronized只能是非公平锁。所谓公平锁，根据线程在阻塞队列中的顺序来获取锁，而非公平锁则是哪个线程抢到就是哪个线程获取锁。
2. ReentrantLock提供了Condition条件类，可以根据等待条件的不同，设置不同的等待队列，从而对阻塞线程分组。而synchronized只对应一个等待队列。
3. ReentrantLock提供了一个中断等待锁的线程的机制，通过lock.lockInterruptibly()来实现这个机制。也就是说，正在等待条件的线程可以选择放弃等待，处理其他线程。
4. 定时或轮询: trylock()/tryLock(time)


### 4. Java并发安全特性
#### 1. 原子性
1. 一个或多个操作，在执行过程中要么全部执行，要么全部不执行。在Java中，只有对基本类型的变量读取和赋值是原子性操作，其他都不是。
    ```java
    x = 10;         //语句1
    y = x;         //语句2
    x++;           //语句3
    x = x + 1;     //语句4
    ```
    只有语句1是原子性操作，其他语句首先要获取x的值，才能进行其他操作，而在读取x值后，可能发生阻塞，从而影响原子性。
    为保证多个操作的原子性，可以使用synchronized或lock锁，保证一次只有一个线程进行操作。

#### 2. Java内存模型与内存可见性
1. Java中，线程共享堆内存。线程之间的通信由Java内存模型JMM控制，线程之间的共享变量存储在主内存中，每个线程都有一个私有的本地内存（并不真实存在），本地内存中存储了线程读写共享变量的副本。
![隔离级别](pics\java_cc_jmm.png)

2. 如果线程A与线程B之间要通信的话,必须要经历下面2个步骤：
    1. 线程A把本地内存A中更新过的共享变量刷新到主内存中去。
    2. 线程B到主内存中去读取线程A之前已更新过的共享变量。

3. volatile关键字可以实现变量的内存可见性，当某变量使用了volatile关键字修饰后，线程对该变量的操作会立即更新到主内存中，其他线程能看到变量的变化，需要读取时从主内存中读取新的值，即为内存可见性。

4. synchronized和Lock也能够保证可见性，synchronized和Lock能保证同一时刻只有一个线程获取锁然后执行同步代码，并且在释放锁之前会将对变量的修改刷新到主存当中。因此可以保证可见性。

#### 3. 指令重排序
1. 在执行程序时，为了提高性能，编译器和CPU常常会对指令进行重排序，分为以下3种类型：
    1. 编译优化重排序。编译器在不改变单线程程序语义的前提下，可以重新安排语句执行顺序。
    2. 指令级并行的重排序。CPU采用了指令级并行技术将多条指令重叠执行。
    3. 内存系统的重排序。由于CPU使用cache和读/写缓冲区，因此加载和存储操作可能在乱序执行。
    
    其中1属于编译器重排序,2和3属于处理器重排序，这些重排序会导致多线程出现内存可见性问题。

2. 对于编译器,JMM的编译器重排序规则会禁止特定类型的编译器重排序(不是所有的编译器重排序都要禁止)。

3. 对于处理器重排序,JMM的处理器重排序规则会要求Java编译器在生成指令序列时,插入特定类型的内存屏障(Memory Barriers,Intel称之为Memory Fence)指令,通过内存屏障指令来禁止特定类型的处理器重排序。

4. JMM把内存屏障分为四类：
    1. LoadLoad屏障：对于这样的语句Load1; LoadLoad; Load2，在Load2及后续读取操作要读取的数据被访问前，保证Load1要读取的数据被读取完毕。
    2. StoreStore屏障：对于这样的语句Store1; StoreStore; Store2，在Store2及后续写入操作执行前，保证Store1的写入操作对其它处理器可见。
    3. LoadStore屏障：对于这样的语句Load1; LoadStore; Store2，在Store2及后续写入操作被刷出前，保证Load1要读取的数据被读取完毕。
    4. StoreLoad屏障：对于这样的语句Store1; StoreLoad; Load2，在Load2及后续所有读取操作执行前，保证Store1的写入对所有处理器可见。它的开销是四种屏障中最大的。在大多数处理器的实现中，这个屏障是个万能屏障，兼具其它三种内存屏障的功能。

### 4. 同步工具原理
#### synchronize实现原理
1. synchronize同步代码块是使用monitorenter和monitorexit指令实现；monitorenter和monitorexit指令是在编译后插入到同步代码块开始和结束的的位置。
2. 任何一个对象都有一个monitor与之关联，当一个monitor被某个线程持有之后，该对象将处于锁定状态。线程执行到monitorenter指令时，会尝试获取该对象对应的monitor所有权，也即获得对象的锁。

3. monitorenter ：每个对象有一个监视器锁（monitor）。当monitor被占用时就会处于锁定状态，线程执行monitorenter指令时尝试获取monitor的所有权，过程如下：
    1. 如果monitor的进入数为0，则该线程进入monitor，然后将进入数设置为1，该线程即为monitor的所有者。
    2. 如果线程已经占有该monitor，只是重新进入，则进入monitor的进入数加1.
    3. 如果其他线程已经占用了monitor，则该线程进入阻塞状态，直到monitor的进入数为0，再重新尝试获取monitor的所有权。

4. monitorexit：执行monitorexit的线程必须是对象所对应的monitor的所有者。指令执行时，monitor的进入数减1，如果减1后进入数为0，那线程退出monitor，不再是这个monitor的所有者。其他被这个monitor阻塞的线程可以尝试去获取这个 monitor 的所有权。

5. 其实wait/notify等方法也依赖于monitor对象，这就是为什么只有在同步的块或者方法中才能调用wait/notify等方法，否则会抛出java.lang.IllegalMonitorStateException的异常的原因

6. 在HotSpotJVM实现中，锁有个专门的名字：对象监视器。

7. synchronized同步方法使用的是ACC_SYNCHRONIZED，JVM通过该标识判断方法是否为同步方法，从而执行相应的同步操作。


#### volatile实现原理
1. 加入volatile关键字时，会多出一个lock前缀指令，lock指令相当于一个内存屏障，提供了3个功能：
    1. 它确保了重排序时屏障前的代码不会排到屏障后，屏障后的代码不会排到屏障前，且执行到屏障时，前面代码都已经执行完毕。
    2. 强制对缓存的修改立即写入到内存。
    3. 如果是写操作，会导致其他CPU中的缓存行无效，只能从主内存读取。
    （缓存行无效是指：如果另一个线程再去读时，此时该线程的缓存行已经无效，所以需要重新从主内存中读取，如果线程已经读取了，则该值有效的，因此不能保证原子性）

#### Atomic原理
1. jdk1.5之后Java添加了一批原子类，主要分为原子更新基本类、原子更新数组、原子更新引用类型、原子更新字段类，主要用于高并发环境下数据的原子性操作，简化同步处理。

2. AtomicInteger是一种提供整型原子操作的类型，Java中自增等操作都不是原子操作，而AtomicInteger提供了一种线程安全的增减操作，主要有获取当前值、返回当前值并进行自增减操作等。相对于普通的加锁同步操作，直接使用AtomicInteger提供的方法即可保证线程安全。
```java
    public final int get() //获取当前的值
    public final int getAndSet(int newValue)	//获取当前的值，并设置新的值
    public final int getAndIncrement()			//获取当前的值，并自增
    public final int getAndDecrement() 		//获取当前的值，并自减
    public final int getAndAdd(int delta) 		//获取当前的值，并加上预期的值
```

3. 内部实现原理：
#### CAS详解
1. CAS（Compare And Swap）比较并交换，JUC包几乎完全建立在CAS上。CAS主要有3个操作数：内存值V、旧的预期值A、新值B；当内存V中存在的值和旧值A一样时，将值更新为B，否则什么也不做。（乐观锁）CAS是基于Unsafe实现的，保证了硬件级别的原子性操作。

2. 基于CAS分析AtomicInteger原理：
    Atomic类有3个主要成员：
    1.	Unsafe是CAS的核心类，提供对底层操作系统的原子性操作。
    2.	valueOffset表示的是变量值在内存中的偏移地址，因为Unsafe就是根据内存偏移地址获取数据的原值.
    3.	value 是一个volatile变量，在内存中可见，因此 JVM 可以保证任何时刻任何线程总能拿到该变量的最新值。如自增操作，对当前值+1，调用使用unsafe的native方法，即硬件级别CAS对当前值更新。
    ```java
    // setup to use Unsafe.compareAndSwapInt for updates
    private static final Unsafe unsafe = Unsafe.getUnsafe();
    private static final long valueOffset;
    static {    
            try {        
                    valueOffset = unsafe.objectFieldOffset (AtomicInteger.class.getDeclaredField("value"));   
            } catch (Exception ex) { 
                throw new Error(ex); 
            }
        }

    public final int addAndGet(int delta) {
        for (;;) {
            int current = get();
            int next = current + delta;
            if (compareAndSet(current, next))
                return next;
        }
    }
    ```
    
3. 以addAndGet方法为例，查看实现方式
    
1. 首先获取当前值cur，再根据增量得到修改值next，最后采用CAS操作将cur更新为next。在多线程情况下，value初始值为10，若线程1执行到第3步，此时线程2再执行，获取value并加1，采用CAS操作将value更新为11；此时线程1再执行，采用CAS操作发现cur和内存值不匹配，因此更新失败。再重新循环，由于get返回value，由volatile修饰，内存可见，因此线程1获取新值11，再采用CAS操作更新成功。因此，整个过程通过CAS操作和volatile关键字保证线程安全性。
    
4. CAS缺点：
	1. ABA问题：如果一个变量V初次读取值为A，在准备更新时值仍然为A，是否能保证V没有被修改过？存在V从A被修改为B，再修改到A；但CAS操作会认为没有被修改，被称为ABA问题。
	2. Java提供AtomicStampedReference来解决这个问题，主要思想是：为变量V设置一个版本，每更新一次版本也更新；当CAS操作时，发现版本不一致，即不更新。不过目前来说，这两个类比较鸡肋，大部分情况下的ABA问题不会影响程序并发的正确性，如果需要解决ABA问题，改用传统的互斥同步可能会比原子类更高效。

5. UnSafe
	Unsafe是CAS实现的核心类，Java无法直接访问底层操作系统，而是通过本地方法（native）来访问。但仍然保留一个后门Unsafe，Unsafe提供了硬件级别的原子操作，包括访问参数内存地址、比较与交换机制、挂起或唤醒线程等。

6. 注意：Unsafe类是如此地不安全，以至于JDK开发者增加了很多特殊限制来访问它。

https://www.jianshu.com/p/509aca840f6d
http://www.cnblogs.com/xrq730/p/4976007.html
https://www.jianshu.com/p/a16d638bc921
https://www.jianshu.com/p/a897c4b8929f



#### 锁类型

##### synchronized底层优化
synchronized是通过对象内部的监视器实现锁机制，但是监视器锁本质又是依赖于底层的操作系统的Mutex Lock来实现的。而操作系统实现线程之间的切换这就需要从用户态转换到核心态，这个成本非常高，状态之间的转换需要相对比较长的时间。

### 同步容器
1. ThreadLocal
2. BlockingQueue
3. ConcurrentHashMap
4. CopyOnWriteArrayList

### 同步工具
1. CountDownLatch
2. CyclicBarrier
3. Semaphore
4. Exchanger
5. FutureTask
6. CompletableFuture
7. ForkJoin

### 4. 线程安全性
原子性 内存可见性 

#### 锁
5. wait & notify()|notifyAll()
wait（锁对象.wait）
调用锁obj的wait(), notify()方法前，必须获得obj锁，也就是必须写在synchronized(obj) 代码段内。
obj.wait()，当前线程调用对象的wait()方法，当前线程释放对象锁，进入等待队列。依靠notify()/notifyAll()唤醒或者wait(long timeout)timeout时间到自动唤醒。
调用wait()方法的线程，如果其他线程调用该线程的interrupt()方法，则会重新尝试获取对象锁。只有当获取到对象锁，才开始抛出相应的InterruptedException异常，从wait中返回。
notify（对象.notify）
obj.notify()唤醒在此对象监视器上等待的单个线程，选择是任意性的。notifyAll()唤醒在此对象监视器上等待的所有线程。
wait&notify 最佳实践
等待方（消费者）和通知方（生产者）
等待方：
synchronized(obj){
	while(条件不满足){
obj.wait();
}
消费;
}

通知方：
synchonized(obj){
	完成条件;
	obj.notifyAll();
}

