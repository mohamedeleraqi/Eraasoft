import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        
        Scanner input = new Scanner (System.in); 

        
        Player player1 = new Player();
        System.out.println("Please Enter the Player Name");
        player1.setPlayerName(input.next());
        System.out.println("Please Enter the Player Number");
        player1.setPlayerNumber(input.nextInt());
        
        System.out.println("Player Namer : " + player1.getPlayerName() + " \nPlayer Number : " + player1.getPlayerNumber() );
    }
}
