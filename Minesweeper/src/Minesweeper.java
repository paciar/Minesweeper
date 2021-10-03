import java.util.*;

public class Minesweeper {
	
	static boolean startGame, running, playing = true;
	static int gridSize, numBombs, x, y, r, c;
	static String userReply, gameOverMessage;
	static Scanner sk = new Scanner(System.in);
	static String[][] unrevealedGrid; 
	static int[][] revealedGrid;
	
	public static void main(String[] args) { // finished method
		System.out.println("Welcome to Minesweeper!");
		while (playing) {
			startGame = true;
			running = true;
			while (startGame) {
				userPreferences();
				startGame = false;
			}
			
			setUpArrays();
			generateBombs();
			setUpRevealedGrid();
			printGrid();
			
			while (running) {
				userInput();
				printGrid();
				checkIfGameOver();
			}
			
			System.out.println(gameOverMessage + " Would you like to play again? Y/N: ");
		    playing = sk.nextLine().trim().equalsIgnoreCase("y");
		}
	}
	
	public static void userPreferences() { // finished method
		System.out.println("Enter the size of the grid you would like to play on (between 10 and 30): ");
		gridSize = sk.nextInt();
		if (gridSize < 10 || gridSize > 30) {
			System.out.println("Make sure that the number you entered is between 10 and 30. Try again: ");
			gridSize = sk.nextInt();
		}
		
		System.out.println("Enter the number of bombs you would like to play with (between 10 and 30): ");
		numBombs = sk.nextInt();
		if (numBombs < 10 || numBombs > 30) {
			System.out.println("Make sure that the number you entered is between 10 and 30. Try again: ");
			numBombs = sk.nextInt();
		}
	}
	
	public static void setUpArrays() { // finished method
		unrevealedGrid = new String[gridSize][gridSize];
		for (int row = 0; row < gridSize; row++) {
			for (int col = 0; col < gridSize; col++) {
				unrevealedGrid[row][col] = "?";
			}
		}
		
		revealedGrid = new int[gridSize][gridSize];
		for (int row = 0; row < gridSize; row++) {
			for (int col = 0; col < gridSize; col++) {
				revealedGrid[row][col] = 0;
			}
		}
	}
	
	public static void generateBombs() { // finished method
		for (int i = 0; i < numBombs; i++) {
			int randomX = (int)(Math.random()*(gridSize-1));
			int randomY = (int)(Math.random()*(gridSize-1));
			
			if (revealedGrid[randomY][randomX] == -1) {
				randomX = (int)(Math.random()*(gridSize-1));
				randomY = (int)(Math.random()*(gridSize-1));
			}
			revealedGrid[randomY][randomX]--;
		}
	}
	
	public static void setUpRevealedGrid() { // finished method
		for (int row = 0; row < gridSize; row++) {
			for (int col = 0; col < gridSize; col++) {
				if (revealedGrid[row][col] == -1) {
					for (int i = row-1; i <= row+1; i++) {
						for (int j = col-1; j <= col+1; j++) {
							if (i < 0 || i >= unrevealedGrid.length || j < 0 || j >= unrevealedGrid[i].length)
							        continue;
							if (revealedGrid[i][j] != -1)
								revealedGrid[i][j]++;
						}
					}
				}
			}
		}
	}
	
	public static void printGrid() { // finished method
		String topRow = "   ";
		for (int i = 0; i < gridSize; i++) {
			if (i < 9)
				topRow += (i+1) + "  ";
			else
				topRow += (i+1) + " ";
		}
		System.out.println(topRow);
		
		for (int row = 0; row < gridSize; row++) {
			if (row < 9)
				System.out.print((row+1) + "  ");
			else
				System.out.print((row+1) + " ");
			for (int col = 0; col < gridSize; col++) {
				System.out.print(unrevealedGrid[row][col] + "  ");
			}
			System.out.println();
		}
	}
	
	public static void userInput() { // finished method
		System.out.println("Enter the x-coordinate of the cell that you would like to flag/clear: ");
		x = sk.nextInt();
		if (x < 1 || x > gridSize) {
			System.out.println("Make sure that the coordinate is between 1 and " + gridSize + ", inclusive. Try again: ");
			x = sk.nextInt();
		}
		
		System.out.println("Enter the y-coordinate of the cell that you would like to flag/clear: ");
		y = sk.nextInt();
		if (y < 1 || y > gridSize) {
			System.out.println("Make sure that the coordinate is between 1 and " + gridSize + ", inclusive. Try again: ");
			y = sk.nextInt();
		}
		
		r = y-1;
		c = x-1;
		
		System.out.println("Enter 'F' to flag the cell or 'C' to clear the cell: "); 
		userReply = sk.next();
		sk.nextLine();
		if (!userReply.equalsIgnoreCase("F") && !userReply.equalsIgnoreCase("C")) {
			System.out.println("Make sure that you enter 'F' or 'C'. Try again: ");
			userReply = sk.next();
			sk.nextLine();
		}
		
		if (userReply.equalsIgnoreCase("F")) {
			if (!unrevealedGrid[r][c].equals("F") && unrevealedGrid[r][c].equals("?"))
				unrevealedGrid[r][c] = "F";
			else if (unrevealedGrid[r][c].equals("F"))
				unrevealedGrid[r][c] = "?";
		}
		
		if (userReply.equalsIgnoreCase("C")) {
			if (unrevealedGrid[r][c].equals("?")) {
				if (revealedGrid[r][c] > 0)
					unrevealedGrid[r][c] = Integer.toString(revealedGrid[r][c]);
				else if (revealedGrid[r][c] == 0) {
					revealZeros(r, c);
				}
				else { // if revealedGrid[r][c] == -1 {
					unrevealedGrid[r][c] = "B";
					lose();
				}
			}
			else if (unrevealedGrid[r][c].equals("F")) 
				System.out.println("Sorry, you can't clear a flagged cell! Either unflag the cell or choose another coordinate.");
			else // if the user clicks on a number, it will clear all adjacent squares if at least 1 is flagged
				revealAdjacentCells(r,c);
		}
	}
	
	public static void checkIfGameOver() { // finished method
		int count = 0;
		for (int row = 0; row < gridSize; row++) {
			for (int col = 0; col < gridSize; col++) {
				if (unrevealedGrid[row][col].equals("F") || unrevealedGrid[row][col].equals("?"))
					count++;
				if (unrevealedGrid[row][col].equals("B"))
					lose();
			}
		}
		if (count == numBombs)
			win();
	}
	
	public static void revealZeros(int row, int col) { // finished method
		unrevealedGrid[row][col] = " ";
		if (revealedGrid[row][col] == -1)
			return;
		if (revealedGrid[row][col] != 0) {
			unrevealedGrid[row][col] = Integer.toString(revealedGrid[row][col]);
			return;
		}
		for (int i = row-1; i <= row+1; i++) {
			for (int j = col-1; j <= col+1; j++) {
				if (i < 0 || i >= unrevealedGrid.length || j < 0 || j >= unrevealedGrid[i].length)
				        continue;
				if (unrevealedGrid[i][j].equals("?"))
					revealZeros(i,j);
			}
		}
		return;
	}
	
	public static void revealAdjacentCells(int row, int col) { // finished method
		ArrayList<String> temp = new ArrayList<String>();
		for (int i = row-1; i <= row+1; i++) {
			for (int j = col-1; j <= col+1; j++) {
				if (i < 0 || i >= unrevealedGrid.length || j < 0 || j >= unrevealedGrid[i].length)
				        continue;
				temp.add(unrevealedGrid[i][j]);
				// first check that at least 1 adjacent square is flagged
				// then, when clearing, check if the adjacent square is flagged -> if it's flagged, skip over it
			}
		}
		if (temp.contains("F")) {
			for (int i = row-1; i <= row+1; i++) {
				for (int j = col-1; j <= col+1; j++) {
					if (i < 0 || i >= unrevealedGrid.length || j < 0 || j >= unrevealedGrid[i].length)
					        continue;
					if (unrevealedGrid[i][j].equals("F"))
						unrevealedGrid[i][j] = "F";
					else if (revealedGrid[i][j] == 0)
						revealZeros(i,j);
					else
						unrevealedGrid[i][j] = Integer.toString(revealedGrid[i][j]);
				}
			}
		}
		else
			System.out.println("Make sure at least 1 adjacent cell is flagged. Try again.");
		return;
	}
	
	public static void lose() { // finished method
		running = false;
		playing = true;
		gameOverMessage = "Game over! You lose!";
	}
	
	public static void win() { // finished method
		running = false;
		playing = true;
		gameOverMessage = "Congratulations! You win!";
	}	
}