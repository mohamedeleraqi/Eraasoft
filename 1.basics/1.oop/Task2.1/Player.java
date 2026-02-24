public class Player {
    
    private int playerNumber;
    private String playerName;


    public void setPlayerNumber(int playerNumber){

        if(playerNumber > 0 ){

            this.playerNumber = playerNumber;
        }else{

            System.out.println("The number is invalid");
        }

    }

    public int getPlayerNumber(){

        return this.playerNumber;
    }

    public void setPlayerName(String playerName){

        if(playerName.length() >= 5){

            this.playerName = playerName;
        } else{

            System.out.println("The name must contain at least 5 letters.");
        }
    }

    public String getPlayerName(){
        return playerName;
    }
    
}
