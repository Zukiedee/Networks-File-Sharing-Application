public class CoinRow {
	static int[] F;
	public static void main (String[] args) {
		int[] C = {5,1,2,10,6,2};
		
		F = new int[C.length];
		System.out.println(CoinRow(C));
	}
	
	public static int CoinRow(int[] coins) {
		F[0] =  0;
		F[1] = coins[1];
		
		for (int i = 2; i<coins.length; i++){
			F[i] = max(coins[i] + F[i-2], F[i-1]);
		}
		return F[coins.length-1];
	}
	
	public static int max(int a, int b) {
		if (a<b)
			return b;
		return a;
	}
}
