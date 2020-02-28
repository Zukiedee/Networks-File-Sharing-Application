import java.util.*;
import java.lang.Math;

public class DP {
	static int[] F;
	
	public static void main (String[] args){
		System.out.println("Enter a number");
		Scanner keys = new Scanner(System.in);
		int number = keys.nextInt();
		
		F = new int[51];
		
		for (int i = 0; i < F.length; i++)
			F[i] = -1;
		
		Fib(number);
	}
	

	public static int Fib(int n){
		ArrayList<Integer> memory = new ArrayList<Integer>(n);
		
		//base case
		if (n<=1) 
			return n;	
		
		if (F[n] != (-1))
			return F[n];
		
		F[n] = Fib(n-1)+Fib(n-2);
		System.out.print(F[n] +" ");
		return Math.abs(F[n]);
	}
}
