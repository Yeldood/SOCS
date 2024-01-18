package sudokuCode;

import java.io.File;
import java.util.Scanner;
import java.util.Stack;

public class Board{
	
	/*The Sudoku Board is made of 9x9 cells for a total of 81 cells.
	 * In this program we will be representing the Board using a 2D Array of cells.
	 * 
	 */

	private Cell[][] board = new Cell[9][9];
	
	//The variable "level" records the level of the puzzle being solved.
	private String level = "";

	
	///TODO: CONSTRUCTOR
	//This must initialize every cell on the board with a generic cell.  It must also assign all of the boxIDs to the cells
	public Board()
	{
		for(int y = 0; y < 9; y++)
			for(int x = 0 ; x < 9; x++)
			{
				board[y][x] = new Cell();
				board[y][x].setBoxID( 3*(x/3) + (y)/3+1);
			}
	}
	
	public Board(Cell[][] board) {
	    this.board = new Cell[9][9];
	    for (int y = 0; y < 9; y++) {
	        for (int x = 0; x < 9; x++) {
	            Cell newCell = new Cell();

	            newCell.setBoxID(board[y][x].getBoxID());
	            newCell.setNumber(board[y][x].getNumber());
            	for(int u = 1; u < 10; u++)
            		if(board[y][x].canBe(u))
            			newCell.canBe(u);
	            board[y][x] = newCell;

	        }
	    }
	}

	
	///TODO: loadPuzzle
	/*This method will take a single String as a parameter.  The String must be either "easy", "medium" or "hard"
	 * If it is none of these, the method will set the String to "easy".  The method will set each of the 9x9 grid
	 * of cells by accessing either "easyPuzzle.txt", "mediumPuzzle.txt" or "hardPuzzle.txt" and setting the Cell.number to 
	 * the number given in the file.  
	 * 
	 * This must also set the "level" variable
	 * TIP: Remember that setting a cell's number affects the other cells on the board.
	 */
	public void loadPuzzle(String level) throws Exception
	{
		this.level = level;
		String fileName = "easyPuzzle.txt";
		if(level.contentEquals("medium"))
			fileName = "mediumPuzzle.txt";
		else if(level.contentEquals("hard"))
			fileName = "hardPuzzle.txt";
		else if(level.contentEquals("D2lhard"))
			fileName = "D2lhard.txt";
		else if(level.contentEquals("oni"))
			fileName = "oni.txt";
		Scanner input = new Scanner (new File(fileName));
		
		for(int y = 0; y < 9; y++)
			for(int x = 0 ; x < 9; x++)
			{
				int number = input.nextInt();
				if(number != 0)
					solve(x, y, number);
			}
						
		input.close();
		
	}
	
	///TODO: isSolved
	/*This method scans the board and returns TRUE if every cell has been solved.  Otherwise it returns FALSE
	 * 
	 */
	public boolean isSolved()
	{
		for(int x = 0; x < 9; x++)
			for(int y = 0; y < 9; y++)
				if(board[y][x].numberOfPotentials() != 1 || board[y][x].getNumber() == 0)
					return false;
		return true;
	}
	


	///TODO: DISPLAY
	/*This method displays the board neatly to the screen.  It must have dividing lines to show where the box boundaries are
	 * as well as lines indicating the outer border of the puzzle
	 */
	public void display()
	{
		System.out.println("╔══════╦══════╦══════╗");

		for(int y = 0; y < 9; y++) {
			if(y % 3 == 0 && y != 0)
				System.out.println("╚══════╣══════╣══════╣");
			for(int x = 0; x < 9; x++) { 
				if(x % 3 == 0) {
					System.out.print("║");
				}
				System.out.print(board[y][x].getNumber() + " ");
			}
			System.out.print("║");

			System.out.println();
		}
		System.out.println("╚══════╩══════╩══════╝");

	}
	
	public void displayBoxID()
	{
		for(int x = 0; x < 9; x++) {
			for(int y = 0; y < 9; y++)
				System.out.print(board[y][x].getBoxID());
			System.out.println();
		}
	}
	
	///TODO: solve
	/*This method solves a single cell at x,y for number.  It also must adjust the potentials of the remaining cells in the same row,
	 * column, and box.
	 */
	public void solve(int x, int y, int number)
	{
		board[y][x].setNumber(number);
		
		for(int u = 1; u < 10; u++)
			if(u != number)
				board[y][x].cantBe(u);

		for(int i = 0; i < 9; i++){//row
			if(i == x)
				continue;
			board[y][i].cantBe(number);
		}
			
		for(int i = 0; i < 9; i++){//column
			if(i == y)
				continue;
			board[i][x].cantBe(number);
		}
		//box
		int iD = board[y][x].getBoxID();

		for (int i = (int)((iD - 1) / 3) * 3; i < ((int)((iD - 1) / 3)) * 3 + 3; i++) {
		    for (int u = (iD - 1) % 3 * 3; u < ((iD - 1) % 3) * 3 + 3; u++) {
		        if (board[i][u].getBoxID() == board[y][x].getBoxID() && i != y && u != x) {
		            board[i][u].cantBe(number);
		        }
		    }
		}
	}
	

	//logicCycles() continuously cycles through the different logic algorithms until no more changes are being made.
	public void logicCycles()throws Exception
	{
		Stack <Cell[][]> stack = new Stack<Cell[][]>();
		Board[] manta = new Board[81];
		int pointer = 0;
		boolean check = false;
		int previousSolve = -3;
		System.out.println("2");
		while(isSolved() == false)
		{
			int changesMade = 0;
			do
			{
				changesMade = 0;
				changesMade += logic1();
				changesMade += logic2();
				changesMade += logic3();
			//	changesMade += logic4();
			//	System.out.println("P:");
			//	displayPotentials();
				display();
			//	displayPotentials();
				Thread.sleep(10);///TODO: get rid of
				if(errorFound()) {//reverts board to previous version, and eliminate previous guess
					pointer--;
					board = manta[pointer].board;//reverts board to previous version
					
					for(int y = 0; y < 9; y++)
						for(int x = 0; x < 9; x++)
							if(board[y][x].getNumber() == 0  && board[y][x].numberOfPotentials() > 1) 
								board[y][x].cantBe(previousSolve);		
							else if(board[y][x].getNumber() == 0 && board[y][x].numberOfPotentials() == 1) {
								solve(x,y, board[y][x].getFirstPotential());
							}
					changesMade++;
//					stack.pop();
//					for(int y = 0; y < 9; y++)//eliminate prev guess
//						for(int x = 0; x < 9; x++)
//							if(board[y][x].getNumber() == 0 && board[y][x].numberOfPotentials() > 1) {
//								board[y][x].cantBe(board[y][x].getFirstPotential());
//					}
				}
				
				
			System.out.println("Changes made: " + changesMade);
			}while(changesMade != 0);
			check = false;
			
			for(int y = 0; y < 9; y++){//Copy board then guess the first potential of the first cell with current board
				for(int x = 0; x < 9; x++){
					if(board[y][x].getNumber() == 0 && board[y][x].numberOfPotentials() > 1) {
		//				stack.push(board);
						manta[pointer] = new Board();
						manta[pointer].board = boardCopy().board;
		//				board = boardCopy().board;
						
						previousSolve = board[y][x].getFirstPotential();
						solve(x,y,previousSolve);//Solves for a guess
						

						
						System.out.println("Board guess made");
						System.out.println("New board");
						display();
						
						check = true;
						pointer++;
						break;
					}
				}
				if(check)
					break;
			}
			
		}
		
	}
	 public Board boardCopy() {
			boolean[] currentPotential = new boolean[10];
			Board temp = new Board();
			
			for(int h = 0; h < 9; h++)
				for(int l = 0; l < 9; l++) {
					currentPotential = new boolean[10];
					for(int u = 1; u < 10; u++) {
						currentPotential[u] = board[h][l].canBe(u);
					}
					Cell tempCell = new Cell();
					temp.board[h][l] = tempCell;
					temp.board[h][l].setBoxID(board[h][l].getBoxID());
					temp.board[h][l].setNumber(board[h][l].getNumber());
					temp.board[h][l].setPotential(currentPotential);	
				}
			return temp;
	 }

	///TODO: logic1
	/*This method searches each row of the puzzle and looks for cells that only have one potential.  If it finds a cell like this, it solves the cell 
	 * for that number. This also tracks the number of cells that it solved as it traversed the board and returns that number.
	 */
	public int logic1()
	{
		int changesMade = 0;
		
		for(int y = 0; y < 9; y++)
			for(int x = 0; x < 9; x++)
				if(board[y][x].getNumber() == 0 && board[y][x].numberOfPotentials() == 1) {
					solve(x, y, board[y][x].getFirstPotential());
					changesMade++;
				}
//		System.out.println("Logic 1: " + changesMade);
		return changesMade;
					
	}
	
	///TODO: logic2
	/*This method searches each row for a cell that is the only cell that has the potential to be a given number.  If it finds such a cell and it
	 * is not already solved, it solves the cell.  It then does the same thing for the columns.This also tracks the number of cells that 
	 * it solved as it traversed the board and returns that number.
	 */
	
	public int logic2()
	{
		int changesMade = 0;
		byte[] manta = new byte[10];
		for(int y = 0; y < 9; y++) {
			
			for(int u = 1; u < 10; u++)
				manta[u] = 0;
			
			for(int x = 0; x < 9; x++)
				for(int u = 1; u < 10; u++)
					if(board[y][x].canBe(u))
						manta[u]++;
			
			for(int u = 1; u < 10; u++) 
				if(manta[u] == 1) //if it is already solved || is the only one with that potential
					for(int x = 0; x < 9; x++) 
						if(board[y][x].canBe(u) && board[y][x].numberOfPotentials() > 1) {
							solve(x, y, u);
							changesMade++;
						}	
		}

		for(int y = 0; y < 9; y++) {
			
			for(int u = 1; u < 10; u++)
				manta[u] = 0;
			
			for(int x = 0; x < 9; x++)
				for(int u = 1; u < 10; u++)
					if(board[x][y].canBe(u))
						manta[u]++;
			
			for(int u = 1; u < 10; u++) 
				if(manta[u] == 1) 
					for(int x = 0; x < 9; x++) 
						if(board[x][y].canBe(u) && board[x][y].getNumber() == 0) {
							solve(y, x, u);
							changesMade++;
						}	
		}
//		System.out.println("Logic 2: " + changesMade);
		return changesMade;
	}
	
	///TODO: logic3
	/*This method searches each box for a cell that is the only cell that has the potential to be a given number.  If it finds such a cell and it
	 * is not already solved, it solves the cell. This also tracks the number of cells that it solved as it traversed the board and returns that number.
	 */
	public int logic3()
	{
		int changesMade = 0;
		byte[] manta = new byte[10];

		for(int boxID = 1; boxID < 10; boxID++) {
			for(int u = 1; u < 10; u++)
				manta[u] = 0;
			
			for(int y = 0; y < 9; y++)  
				for(int x = 0; x < 9; x++)
					if(board[y][x].getBoxID() == boxID)
						for(int u = 1; u < 10; u++)
							if(board[y][x].canBe(u))
								manta[u]++;
					
			for(int u = 1; u < 10; u++)
				if(manta[u] == 1) 
					for(int y = 0; y < 9; y++)
						for(int x = 0; x < 9; x++)
							if(board[y][x].getBoxID() == boxID && board[y][x].getNumber() == 0 && board[y][x].canBe(u)){
								solve(x, y, u);
								changesMade++;
							}
			
		}
	//	System.out.println("Logic 3: " + changesMade);
		return changesMade;
	}
	
	
	///TODO: logic4
		/*This method searches each row for the following conditions:
		 * 1. There are two unsolved cells that only have two potential numbers that they can be
		 * 2. These two cells have the same two potentials (They can't be anything else)
		 * 
		 * Once this occurs, all of the other cells in the row cannot have these two potentials.  Write an algorithm to set these two potentials to be false
		 * for all other cells in the row.
		 * 
		 * Repeat this process for columns and rows.
		 * 
		 * This also tracks the number of cells that it solved as it traversed the board and returns that number.
		 */
	public int logic4()///TODO: make work
	{
		int changesMade = 0;
		boolean[] visited = new boolean[10];
		
		int[] manta = new int[10];
		for(int y = 0; y < 9; y++) {
			int temp1 = 0, temp2 = 0;
			for(int u = 1; u < 10; u++) {
					manta[u] = 0;
			}
			
		    for (int u = 1; u < 10; u++) {
		            visited[u] = false;
		        }
			
			for(int x = 0; x < 9; x++)
				for(int u = 1; u < 10; u++)
					if(board[y][x].canBe(u))
						manta[u]++;
			
			for(int u = 1; u < 10; u++) {
				if(manta[u] == 2)
					temp1 = manta[u];
			}
//			for(int u = 1; u < 10; u++)
//				System.out.println("number " + u + " occurs " + manta[u] + " times");
			
			
				
//			for(int u = 1; u < 10; u++)///TODO: might do every number twice
//				if(manta[u] == 2 ){//two in the row are the only two with potentials to be u
//					for(int n = u + 1; n < 10; n++)
//						if(n != u && manta[n] == 2  && !visited[n] && !visited[u]){
//							for (int x = 0; x < 9; x++) {
//	                            if (!board[y][x].canBe(u) && !board[y][x].canBe(n) && board[y][x].getNumber() == 0) {
//	    							visited[n] = true;
//	    							visited[u] = true;
//	    							changesMade++;
//	                                board[y][x].cantBe(u);
//	                                board[y][x].cantBe(n);
//	                            
//	                            	for(int i = 0; i < 10; i++)
//	                            		if(i != u && i != n)
//	                            			board[y][x].cantBe(i);
//	                            }
//	                            else {
//	                            	System.out.println("1L:4 should work at x" + x + " y" + y);
//	                            	System.out.println("u = " + u + " n = " + n);
//	                            }
//	                        }
//
//						}
//				}
		}
			
		
		for(int x = 0; x < 9; x++) {

		    for (int u = 1; u < 10; u++) {
	            visited[u] = false;
	        }
			
			
			for(int y = 0; y < 9; y++)
				for(int u = 1; u < 10; u++)
					if(board[y][x].canBe(u))
						manta[u]++;
			
//			for(int u = 1; u < 10; u++)
//				System.out.println("number " + u + " occurs " + manta[u] + " times");
				
			for(int u = 1; u < 10; u++)///TODO: might do every number twice
				if(manta[u] == 2 && !visited[u])//two in the row are the only two with potentials to be u
					for(int n = 0; n < 10; n++)
						if(n != u && manta[n] == 2 && !visited[n] && !visited[u]){
							
							//if another number (n) has two potentials in the row
	                        for (int y = 0; y < 9; y++) {
	                            if (!board[y][x].canBe(u) && !board[y][x].canBe(n) && board[y][x].getNumber() == 0) {

	    							changesMade++;
	                                board[y][x].cantBe(u);
	                                board[y][x].cantBe(n);
	                            	for(int i = 0; i < 10; i++)
	                            		if(i != u && i != n)
	                            			board[y][x].cantBe(i);
	                            }
	                            else{
	                            	System.out.println("2L:4 should work at x" + x + " y" + y);
	                            	System.out.println("u = " + u + " n = " + n);
	                            }
	                        }

						}
		}	
		
		System.out.println("Logic 4: " + changesMade);
		return changesMade;
	}
	
	
	///TODO: errorFound
	/*This method scans the board to see if any logical errors have been made.  It can detect this by looking for a cell that no longer has the potential to be 
	 * any number.
	 */
	public boolean errorFound()
	{
		for(int y = 0; y < 9; y++)
			for(int x = 0; x < 9; x++)
				if(board[y][x].numberOfPotentials() == 0) {
					System.out.println("Error Found at:");
					System.out.println("x = " + x);
					System.out.println("y = " + y);
					System.out.println();
					return true;
				}
		return false;
	}



	public void displayPotentials() {
		for(int x = 0; x < 9; x++) {
			for(int y = 0; y < 9; y++)
				System.out.print(board[y][x].numberOfPotentials());
			System.out.println();
		}		
	}
	
	
	
	
}