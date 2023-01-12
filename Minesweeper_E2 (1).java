/*====================================================================================
   Minesweeper_V3
   Programmer: Cherry Yinuo Yang
   Course: ICS3U1
   Last modified: June 22ND, 2021
--------------------------------------------------------------------------------------
   This program lets the user play the game minesweeper, where the player saves the 
   win and loss statistics in a seperate txt document through file I/O.
====================================================================================== */
import java.util.*;
import java.io.*;

public class Minesweeper_E2
{
   
   //constants for character values for the boards
   static final char MINE = 'X';
   static final char FLAG = 'M';
   static final char UNDIGGED = '~';
   
   //constants for mininum and maximum values when setting the boards
   static final int MIN_BOARD = 5, MAX_BOARD = 15;
   static final double MIN_MINE = 0.10, MAX_MINE = 0.80;
   
   //constants for racking the stats of the user
   static int wins = -1;
   static int losses = -1;
   static int recentRowsWin = -1;
   static int recentColsWin = -1;
   static int recentMineWin = -1;
   
   //variables for setting boards
   static int  rows, cols, numMines, boardSize, minMineNum, maxMineNum;
   static char [][] playerBoard;
   static char [][] mineBoard;
   
   //variables for taking a turn for digging
   static int action, actionRow, actionCol, round, again, count;   
   static boolean ask = true;
   static boolean playAgain = true;
   static boolean proceed = true;
   static boolean winOrLose;  
      
   public static void main (String [] args)
   {
      //Constant declaration
      final String BAR = "|";
      final String WELCOME = "WELCOME TO ICS MINESWEEPER";
      final String STR_COL = "column";
      final String STR_ROW = "row";
      final String STR_MINE = "mine";
      final int DIG_KEY = 1;
      final int FLAG_KEY = 2;
      
      //Variable Declaration   
      int newOrOld;                    //to find if player is new or returning
      String playerName = " ";         //find player's name;
      
      //Declare Scanner
      Scanner sc = new Scanner(System.in);     
       
      //Diaplay welcome message
      System.out.println("==================================================================");
      System.out.printf("%-20s%20s%20s%n", BAR, WELCOME, BAR);
      System.out.println("==================================================================");
      System.out.println("Hello! A huge welcome to you!");
      
      //Prompt user for name and create new files    
      do 
      {          
         try
         {
            System.out.println();      //blank line for spacing
            System.out.print("Please enter your name: ");
            playerName =sc.nextLine();       
            playerName = playerName + ".txt";
         
            //Prompt for user input to determine if they are a new or returning player
            System.out.println("If you are a new player, please enter 1, so we can create a profile for you!");
            System.out.println("If you are an returning player, please enter 2, so we can retrieve your profile for you!");
            newOrOld = sc.nextInt();
         
            //check if old or new
            if (newOrOld == 1)
            {
               ask = false;      //moves on to next step
               
               //gives value of zero for all stats
               wins = 0;
               losses = 0;
               recentRowsWin = 0;
               recentColsWin = 0;
               recentMineWin = 0;   
               
               //create the file    
               wrtieFile(playerName);
            }
            else if (newOrOld == 2)
            {
               ask = false;      //moves on to next step
               //check returning player file and print stats
               returningPlayer(playerName);     
            }
            else
            {
               System.out.println("Please enter either 1 or 2 to continue, try again!");
            }      
         }   
         catch (InputMismatchException e) //if user did not enter integer
         {
            System.out.println("You have entered invalid input, try again!"); 
            ask = true;
         }
         sc.nextLine();
            
      }while (ask);
      ask = true;          //reset for future use
   
      do
      {
         //Initialize and reset for first round
         round=0;
         count=0; 
             
         //Ask user for the size of the board they want. 
         System.out.println();
         System.out.println();
         System.out.println("Please enter the size of the game board you want.");
         
         //collect inputs 
         rows = findInput (MIN_BOARD, MAX_BOARD, STR_ROW);
         cols = findInput (MIN_BOARD, MAX_BOARD, STR_COL);
                 
         //create board
         createBoard(rows, cols);  
         boardSize = rows*cols;
         minMineNum = (int)(MIN_MINE*boardSize);
         maxMineNum = (int)(MAX_MINE*boardSize);
         
         //prompt for mines and collect input 
         System.out.println("\n\nNow, you may choose how many mines you want to have,"); 
         numMines = findInput (minMineNum, maxMineNum, STR_MINE);
                 
         //set mines
         setMines (numMines);
         
         do
         { 
            //System.out.println("Check mines board delete later");
            //printBoard( mineBoard);
            printBoard (playerBoard);
                    
            //Ask user for coordinates they want. 
            actionRow = choose(rows, STR_ROW);
            actionCol = choose(cols, STR_COL);
           
            //Collect input on what coordinates they want their action to be
            do 
            { 
               try
               {
                  //check if already digged
                  if  (playerBoard [actionRow][actionCol] != UNDIGGED && playerBoard [actionRow][actionCol]!= 'M')
                  {
                     System.out.println("\nThe position you chose already has been digged! Please try again");
                     ask = false;
                     proceed = true;     
                  }
                  else
                  {   
                     System.out.println();
                     System.out.println("Would you like to [1] DIG or [2] FLAG/UNFLAG a location?");
                     action = sc.nextInt();
                  
                     if (action != DIG_KEY && action != FLAG_KEY)      
                     {
                        System.out.println("You should only use the numbers 1 or 2, please try again");
                     }
                     else if (action == DIG_KEY)      
                     {  
                        ask = false;
                        digLocation(actionRow, actionCol);
                     }    
                     else if (action == FLAG_KEY)
                     {
                        ask = false;
                        round ++;
                        //Check if already flagged
                        if (playerBoard[actionRow][actionCol] == FLAG)
                        {
                           playerBoard [actionRow][actionCol] = UNDIGGED;
                        }     
                        else
                        {
                           playerBoard [actionRow][actionCol] = FLAG;
                        }
                     }    
                  }
               }
               catch (InputMismatchException e)
               {
                  System.out.println("You have entered invalid input."); 
                  ask = true;
                  sc.nextLine();
               }
            }while (ask);      
            ask = true;       //reset for future use
            
         }while (proceed); 
         proceed = true;      //reset for future use
            
         do 
         { 
            try
            {
               System.out.println();
               System.out.println("Would you like to play again: [1] Yes [2] No");            
               again = sc.nextInt();
               
               if (again == 1 )      
               {
                  playAgain = true;
                  ask = false;
               }
               else if (again == 2)
               {
                  playAgain = false;
                  ask = false;
               }
               else 
               {
                  System.out.println("You have entered invalid input, try again!");
               }
            
            }
            catch (InputMismatchException e)
            {
               System.out.println("You have entered invalid input, try again!"); 
               sc.nextLine();
            }
         }while (ask);      
         ask = true;       //reset for future use
      }while(playAgain);   //reloop entire game
         
      System.out.println("Thank you for playing! Have a great rest of your day :)");
      wrtieFile(playerName); 
   } 
   
   
   /*==============================================================================
     wrtieFile (String name)                                                     
   -------------------------------------------------------------------------------
     String name - This parameter is the file name for the file for player               
   -------------------------------------------------------------------------------
     This program creates a file for the player and input the stats of the player
     in 5 lines using the buffered reader.                                                          
   ==============================================================================*/ 
   public static void wrtieFile (String name)
   {
      try
      {
         // Create a buffered writer using a file wruter
         BufferedWriter playerOut = new BufferedWriter (new FileWriter (name, false)); 
      
         //write 5 lines 
         playerOut.write(wins + "\n" + losses + "\n" + recentRowsWin + "\n" + recentColsWin + "\n" + recentMineWin);
         
         playerOut.close();
      }
      catch (IOException e)
      {
         System.out.println("There was a problem creating the file, please try again");
         ask = false;
      }
   }
   
         
   
   /*==============================================================================
     returningPlayer (String name)                                                     
     -----------------------------------------------------------------------------
     String name - This parameter is the file name for the file for player               
    ------------------------------------------------------------------------------
     This program reads the file of the returning player and record the stats
     of the player then prints the stats to the screen using the printStats() 
     method.                                                          
    ==============================================================================*/ 
   public static void returningPlayer (String name)
   {
      String lineIn;
      
      try
      {
         // Create a file  using a file reader
         Scanner statsFs = new Scanner(new File(name)); 
         
         //Tracking the stats of the user
         wins = statsFs.nextInt();
         losses = statsFs.nextInt();
         recentRowsWin = statsFs.nextInt();
         recentColsWin = statsFs.nextInt();
         recentMineWin = statsFs.nextInt();
         
         //method to print stats
         printStats();
       
         statsFs.close();
      }
      catch (NumberFormatException e) 
      {
         System.out.println("There was a problem with collecting the values, please try again.");
         ask = true;
      } 
      catch (IOException e)
      {
         System.out.println("The file is not found, make sure the name you entered was correct");
         ask = true;
      }
   
      
        
   } 
   
   /*==============================================================================
     printStats ()                                              
    ------------------------------------------------------------------------------
     This program prints out the stats that are useful to the user, which includes
     calculating the winnning rate of the user.                                                    
    ==============================================================================*/ 
   public static void printStats ()
   {
      double winRate;        
      System.out.println("Here are your current stats...");
   
      System.out.println("Wins: " + wins);
      System.out.println("Losses: " + losses);
      if (wins >0)
      {
         winRate = 100*((double)wins / (wins + losses));
         System.out.printf("Win rate:  %.2f%% \n", winRate); 
         System.out.printf("Size of most recent win: %dx%d \n", recentRowsWin, recentColsWin);
         System.out.println("Mines in most recent win: " + recentMineWin);       
      }
   }
    
    
   /*==============================================================================
    createBoard (int rowN, int colN)                                                     
   --------------------------------------------------------------------------------
    int rowN - This parameter is the number of rows the user chose for the board
    int colN - This parameter is the number of columns the user chose for the board
   --------------------------------------------------------------------------------
    This program creates a board for the user and also a board for the program to 
    set mines for in the future.                                                     
   ==============================================================================*/     
   public static void createBoard (int rowN, int colN)
   {
      playerBoard = new char [rowN][colN];
      mineBoard = new char [rowN][colN];
      
      for (int i=0; i < rowN; i++)
      {
         for (int j = 0; j < colN; j++)
         {
            playerBoard [i][j] = UNDIGGED;
            mineBoard [i][j] = UNDIGGED;
         }
      }
    
   }
   
   
   
   /*==============================================================================
     setMines (int numberOfMines)                                                     
   -------------------------------------------------------------------------------
     int numberOfMines - This parameter is number of mines the user wants               
   -------------------------------------------------------------------------------
     This program sets up the mines insdie the mine board.                                                          
   ==============================================================================*/ 
   public static void setMines (int numberOfMines)
   {
      int mineCount =0;
      int r, c;
      
      //loop to create the number of mines the user set
      while (mineCount < numberOfMines)
      {
         r =(int)(Math.random() * rows);
         c = (int)(Math.random() * cols);   
         if  (mineBoard [r][c]!=MINE)
         {
            mineBoard [r][c] = MINE;
            mineCount ++;
         }
         
      }
   }
   
   /*==============================================================================
     printBoard (char [][]array   )                                                  
   -------------------------------------------------------------------------------
     char [][] array = This parameter is the array that will be printed 
   -------------------------------------------------------------------------------
     This program prints an array with the proper horizontal and vertical bars 
     which indicates how the row and column number                                                          
   ==============================================================================*/ 
   public static void printBoard (char[][] array)
   {
      final String hBar = "-----";
      final char vBar = '|';
      
      //print bar on top
      System.out.print("      ");
      for (int i = 1; i <= cols; i++)
      {
         System.out.printf("%-5d", i);
      }
      System.out.println();
      
      //print line to seperate
      System.out.print("  ");
      for (int i = 1; i <= cols; i++)
      {
         System.out.printf("%5s", hBar);
      }
      System.out.println();
      
      //print  rest of the array
      for (int i = 0; i<rows; i++)
      {
         System.out.printf("%-2d%-4c", i+1, vBar );    //prints the side numbers
         
         for (int j = 0; j<cols; j++)
         {
            System.out.printf("%-5c", array[i][j]);
            
         }
         System.out.println();
      }
      
   }
   
   /*==============================================================================
     resetMine (int rowN, colN)                                                     
   -------------------------------------------------------------------------------
     int rowN - This parameter is the row number of the mine that needs to be 
                reset
     int colN - This parameter is the column number of the mine that needs to be
                reset                
   -------------------------------------------------------------------------------
     This program moves a mine from one place to elsewhere if it was hit on the 
     trial                                                                                          
   ==============================================================================*/ 
  
   public static void resetMine (int rowN, int colN)
   {
      int num = 0;
      int r, c;
       
      mineBoard [rowN][colN] = UNDIGGED;
      while (num <1)
      {
         r =(int)(Math.random() * rows);
         c = (int)(Math.random() * cols);   
         if  (mineBoard [r][c]!=MINE && (r!=rowN && c!=colN))
         {
            mineBoard [r][c] = MINE;
            num++;
         }
      }
       
   }

   /*==============================================================================
     endDisplay (boolean success)                                                  
   -------------------------------------------------------------------------------
     boolean success - this parameter is true when the player has won the game
    ------------------------------------------------------------------------------
     This program displays the statement that informs the user if won and also
     updates the variables which will update the profiles for the player. It also 
     uses the printStats() method to print the stats                                                                                          
   ==============================================================================*/     
   public static void endDisplay (boolean success)
   {
      printBoard(playerBoard);
   
      if (success)
      {
         wins++;
         recentRowsWin = rows;
         recentColsWin = cols;
         recentMineWin = numMines;
         System.out.println("You WIN!!!!!!!!!! You have successfully cleraed the field!");
      }
      else
      {
         losses++;
         System.out.println("Oh NO! You have hit a mine. You lost :("); 
      }
      
      printStats();
   }
   
   /*==============================================================================
     printCount (int rowN, colN)                                                     
   -------------------------------------------------------------------------------
     int rowN - This parameter is the row number of location to dig
     int colN - This parameter is the column number of the location to dig                
   -------------------------------------------------------------------------------
     This program prints the number of mines around a certain location and 
     continues to find the count until a number is found                                                                                          
   ==============================================================================*/ 
   public static void printCount (int rowN, int colN)
   {
      int count=0;
      int i = rowN, j = colN; 
        
      //top left
      if (i-1!=-1 && j-1!=-1 && mineBoard [i-1][j-1]==MINE)
         count++;
      //top mid
      if (i-1!=-1 &&  mineBoard [i-1][j]==MINE)
         count++;
      //top right
      if (i-1!=-1 && j+1!=cols && mineBoard [i-1][j+1]==MINE)
         count++;
      //mid left                    
      if (j-1!=-1 &&  mineBoard [i][j-1]==MINE)
         count++;
      //mid right
      if (j+1!=cols &&  mineBoard [i][j+1]==MINE)
         count++;
      //bottom left
      if (i+1!=rows && j-1!=-1 && mineBoard [i+1][j-1]==MINE)
         count++;         
      //bottom mid
      if (i+1!=rows && mineBoard [i+1][j]==MINE)
         count++;             
     //bottom right 
      if (i+1!=rows &&  j+1!=cols && mineBoard [i+1][j+1]==MINE)
         count++;
   
      char countM = (char) (count + '0');
       
      playerBoard [rowN][colN] = countM;
      
      if (playerBoard [rowN][colN] == '0')
      {
         playerBoard [rowN][colN] = ' ';   
         for (int p=-1;p<=1;p++) 
         {
            for (int q=-1;q<=1;q++) 
            {    
               if (p+rowN > -1 && p+rowN <rows  && q+colN > -1 && q+colN < cols)
               {
                  if (p != 0 || q !=0)
                  {
                     if (playerBoard [p+rowN][q+colN] == UNDIGGED)
                     {
                        //System.out.println((p+rowN) + " "+ (q+colN));
                        printCount (p+rowN,q+colN);
                     }                  
                  }
               }
            }
         }
      }
      
   }
   
   /*==============================================================================
     int findInput(int mininum, int maximum, String str)                                                     
   -------------------------------------------------------------------------------
     int mininum - This parameter is mininum value the input can be
     int maximum - This parameter is maximum value the input can be  
     String str - This parameter is a string value to make the prompts clear              
   -------------------------------------------------------------------------------
     return int - The input of a measurment within the set range             
   -------------------------------------------------------------------------------
     This program finds the input of a number between a certain range set by the
     parameter, and catches anything that is incorrect or invalid.                                                                                          
   ==============================================================================*/ 

   public static int findInput(int mininum, int maximum, String str)
   {
      int input=0;
      Scanner sc = new Scanner (System.in);
   
      do 
      { 
         try
         {
            System.out.println();
            System.out.printf("Please enter the number of %ss.\n", str);
            System.out.println("Make sure the number of" + str + "s are numbers between " + mininum + " and " + maximum + " inclusive.");
               
            input = sc.nextInt();
               
            if (input < mininum)      
            {
               System.out.println("You should not have less than " + mininum + " " + str);
            }
            else if (input > maximum)
            {
               System.out.println("You should not have more than " + maximum + " " + str);
            }
            else 
            {
               ask = false;
            }   
         }
         catch (InputMismatchException e)
         {
            System.out.println("You have entered invalid input, try again!"); 
            ask = true;
            sc.nextLine();
         }
      }while (ask);
      ask = true;       //reset for future use
      
      return input;
      
   } 
   
   /*==============================================================================
     int choose(int maximum, String str)                                                     
   -------------------------------------------------------------------------------
     int maximum - This parameter is maximum value the input can be  
     String str - This parameter is a string value to make the prompts clear              
   -------------------------------------------------------------------------------
     return int - The input of a chosen value within the set range             
   -------------------------------------------------------------------------------
     This program finds the number betweenthe maximum value and zero, and catches 
     anything that is incorrect or invalid.                                                                                          
   ==============================================================================*/ 
   public static int choose(int maximum, String str)
   {
      int chooseAction = 0;
      Scanner sc = new Scanner (System.in);
      
      do 
      { 
         try
         {
            System.out.println();
            System.out.print("Choose a " + str + " :");            
            chooseAction = sc.nextInt();
            chooseAction = chooseAction -1;
                  
            if (chooseAction >= maximum || chooseAction < 0)      
            {
               System.out.println("The " + str + "  you chose does not exist, please try again");
            }
            else 
            {
               ask = false;
            }
         }
         catch (InputMismatchException e)
         {
            System.out.println("You have entered invalid input, try again!"); 
            ask = true;
            sc.nextLine();
         }
               
      }while (ask);
      ask = true;       //reset for future use
   
      return chooseAction;
   
   }
   
   /*==============================================================================
     digLocation(int rowN, int colN)
   -------------------------------------------------------------------------------
     int rowN - This parameter is the row number of the mine chosen
     int colN - This parameter is the column number of the mine chosen
   -------------------------------------------------------------------------------
     This program digs up the location indicated in the parameter and goes into 
     other methods when needed. It checks if a location can be digged and if it is
     a mine or not.                                                                                    
   ==============================================================================*/ 
   public static void digLocation(int rowN, int colN)
   {
      Scanner sc = new Scanner (System.in);
      
      if (playerBoard [rowN][colN] == FLAG)
      {
         System.out.println("You have to unflag before digging!! Please try again.");
      }
      else if (mineBoard [rowN][colN] == MINE)
      {
                        //if first round, set the mine elsewhere
         if (round ==0)
         {
            resetMine(rowN, colN);
            printCount(rowN, colN);
            round++;
         }
         else
         {
            playerBoard [rowN][colN] = MINE;
            winOrLose = false ;        //false meaning the user lost
            endDisplay (winOrLose);    //print necessary elements 
            proceed = false;           //don't play again
         }
      }
      else 
      {
         printCount (rowN, colN);
         round ++;                     //indicates that a round was played
         count = 0;   
         for (int i = 0; i<rows; i++)
         {
            for (int j = 0; j<cols; j++)
            {
               if (playerBoard[i][j] != mineBoard [i][j])
               {
                  if (playerBoard [i][j] != FLAG && mineBoard[i][j] == FLAG);
                  else
                  {
                     count++;             //count if all places w/out mines are digged
                  }
               }
            }
         }
         if (count == boardSize)
         {
            winOrLose = true;
            endDisplay (winOrLose);
            proceed = false;
         }
      } 
   }
    
}
   
