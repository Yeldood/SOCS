package sudokuCode;

public class SudokuSolver {

	public static void main(String[] args)throws Exception {
		
		Board puzzle = new Board();
		puzzle.loadPuzzle("D2lhard");
		puzzle.display();
		System.out.println();
		puzzle.logicCycles();
		System.out.println("\n Number of Potentials:");
		puzzle.displayPotentials();
		System.out.println("\nCompleted board: ");
		
		puzzle.display();
		System.out.println("Error Found: " + puzzle.errorFound());
		System.out.println("Solved: " + puzzle.isSolved());
		

	}

}