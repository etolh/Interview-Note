[TOC]

# Java并发
## 一. 线程
### 1. 进程与线程
程序是一个静态概念，即一段代码，包含数据与执行逻辑。
进程是程序在数据上的一次执行过程，进程是系统进行资源分配的最小单位，线程是进程内部的一个执行序列，是CPU调度和执行的最小单位。
线程自己基本上不拥有系统资源,只拥有一点在运行中必不可少的资源(如程序计数器,一组寄存器和栈),但是它可与同属一个进程的其他的线程共享进程所拥有的全部资源。
一个线程可以创建和撤销另一个线程;同一个进程中的多个线程之间可以并发执行。

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



#### Callable Future与FutureTask详解
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
    1. 同步阻塞：线程竞争synchronized锁或其他锁失败，阻塞进入到同步阻塞队列。直到其他线程释放锁，才有机会获取锁，变成就绪状态。
    2. 等待阻塞：线程获取锁后，因为某种条件不满足，调用锁对象的wait方法，进入到等待阻塞队列，并释放锁。直到通知线程完成条件，调用锁对象的notify/notifyAll方法通知等待线程，等待线程才能从等待阻塞状态转到同步阻塞，重新竞争锁。
    3. 其他阻塞：调用线程的sleep()或join()或发出了I/O请求时，线程会进入到阻塞状态。当sleep()状态超时、join()等待线程终止或者超时、或者I/O处理完毕时，线程重新转入就绪状态。
5.	死亡状态：线程执行完毕或因异常退出，生命周期结束。
[pic]

### 4. 线程方法
[pic]
1. Thread.sleep():让当前线程睡眠指定毫秒数，但不释放锁；睡眠结束进入就绪状态。

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
线程安全是指某个函数、对象等在多线程环境下执行，能够正确地处理多线程之间的共享变量，保证程序能够正常执行。
《深入Java虚拟机》：当多个线程访问同一个对象时，如果不用考虑这些线程在运行时环境下的调度和交替运行，也不需要进行额外的同步，或者在调用方进行任何其他的协调操作，调用这个对象的行为都可以获取正确的结果，那这个对象是线程安全的。

线程同步：多线程执行过程中，为保证共享变量的正确性，采用同步机制保证一个时刻只有一个线程才能对共享变量操作，其他线程只有等操作完毕后才能操作。
线程安全是一种状态，线程同步是实现线程安全的一种手段。
（线程安全是结果，线程同步是手段）

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

### 3. 线程通信/同步工具使用
当多个线程访问某个状态变量，并有一个线程执行写入操作时，需要采用同步机制保证线程安全性。
同步机制包括synchronize内置锁、显式锁、volatile变量和原子变量。

#### 1. synchronized

#### 2. Lock
#### 3. volatile
#### 4. Atomic

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

