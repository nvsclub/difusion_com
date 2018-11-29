public class Thread_test{
  public static void main(String[] argv){
    for(int it = 0; it < 10; it++){
      new Thread(new Runnable(){
        public void run() {
          System.out.println("Hello World!");
          
        }
      }).start();
    }
  }
}