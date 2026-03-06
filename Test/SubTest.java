    package Test;
    public class SubTest extends Test{


        
       @Override
        public void print(){
                super.print();
                System.out.println("From SubTest");
            

        }
        @Override
        public String reprint(){
          
         /return "From SubTest";
        }
    }