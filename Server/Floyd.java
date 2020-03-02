import java.util.*;

public class Floyd {
	static int[][] p; 
	
	static final int N = 4;
	
	public static void main (String[] args) {
		Scanner keys = new Scanner(System.in);
		
		int[][] m = {{0,5,999,999},{50,0,15,5},{30,999,0,15},{15,999,5,0}};		
		
		System.out.println("Printing the matrix");
		printMatrix((m));
		
		p = new int [N][N];
		
		System.out.println("Shortest path");
		printMatrix(floyd(m));
	}
	
	public static int[][] floyd(int[][] M) {
		for (int k = 0; k < N; k++) {
			for (int i = 0; i < N; i++) {
				for (int j = 0; j < N; j++) {
					if (M[i][k] + M[k][j] < M[i][j]) {
						M[i][j] = M[i][k] + M[k][j];
						p[i][j] = k;
					}	
				}
			}
		}
		return M;
	}
	
	public static void printMatrix(int[][] m) {
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				System.out.print(m[i][j] +" ");
			}
			System.out.println();
		}
	}
	
	
}
