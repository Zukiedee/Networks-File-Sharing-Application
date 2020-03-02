import java.util.Scanner;

class exponent {
	public static void main (String[] args) {
		
		Scanner keys = new Scanner(System.in);
		int base = keys.nextInt();
		int exp = keys.nextInt();
		
		System.out.println("Brute force: " + divide_conquer(base, exp));

		System.out.println("Divide and conquer: " + divide_conquer(base, exp));
	}
	
	static int bruteForce(int base, int exp) {
		int answer = 1;
		
		for (int i = 0; i < exp; i++) {
			answer *= base;
		} 
		return answer;
		
	}
	
	static int divide_conquer(int base, int exp){
		//base case
		if (exp == 0){
			return 1;
		}
		
		else {
			//calculate half of power 
			int half_answer = divide_conquer(base, exp/2);
			int full_answer = half_answer*half_answer;
			
			//if exponent is odd
			if (exp % 2 == 1) full_answer*=base;
			
			return full_answer;
		}
			
	}
}
