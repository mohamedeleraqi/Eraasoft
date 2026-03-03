public class ReturnSum extends Sum{
    

    private int num3;

    public ReturnSum(){
        
    }
    public ReturnSum(int num1,int num2 , int num3){

        super(num1, num2);

        CheckNumber(num1,num2, num3);

    }


   public void  CheckNumber(int num1 , int num2 , int num3){

        if( num1 %2 == 0 && num2 %2 == 0 && num3 %2 == 0){

            System.out.println("Even Number :"+(num1 + num2 + num3));

        }else if(num1 %2 > 0 && num2 %2 > 0 && num3 %2 > 0){

            System.out.println("Odd Number :"+(num1 + num2 + num3));

        }else{
            System.out.println("Some thing went Wrong");
        }

    }

}
