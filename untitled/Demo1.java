public class Demo1 {

    public static void main(String[] args) {
        MyThread myThread = new MyThread();
        myThread.start();
        for (int i = 0; i < 20; i++) {
            System.out.println("主线程--" + i);
        }

    }

    static class MyThread extends Thread {
        @Override
        public void run() {
            for (int i = 0; i < 20; i++) {
                System.out.println("新建线程--" + i);
            }
        }
    }
}
