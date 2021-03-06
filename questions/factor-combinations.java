import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Numbers can be regarded as product of its factors. For example,

 8 = 2 x 2 x 2;
 = 2 x 4.
 Write a function that takes an integer n and return all possible combinations of its factors.

 Note:
 You may assume that n is always positive.
 Factors should be greater than 1 and less than n.
 Examples:
 input: 1
 output:
    []
 input: 37
 output:
    []
 input: 12
 output:
 [
    [2, 6],
    [2, 2, 3],
    [3, 4]
 ]
 input: 32
 output:
 [
    [2, 16],
    [2, 2, 8],
    [2, 2, 2, 4],
    [2, 2, 2, 2, 2],
    [2, 4, 4],
    [4, 8]
 ]
 */

public class Solution {
    public List<List<Integer>> getFactors(int n) {

        List<List<Integer>> result = new ArrayList<List<Integer>>();
        ArrayList<Integer> factors = new ArrayList<Integer>();
     
        getFactorsHelper(result,factors ,n, n/2);
        return factors;
    }

    public static void getFactorsHelper(List<List<Integer>> result, List<Integer> factors, int n, int largestf){
        if(n==1){
            ArrayList<Integer> f = new ArrayList<Integer>(factors); // Important STEP
            Collections.sort(f);
            if(f.size() >= 1){
                result.add(f);
            }

        }

        for(int i=largestf;i>1 ;i--){
            if(n%i ==0){
                factors.add(i);
                getFactorsHelper(result,factors,n/i,i);
                factors.remove(factors.size() - 1);
            }
        }
    }
}
