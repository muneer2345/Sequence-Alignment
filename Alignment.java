import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Alignment {
	//variables to store alignment information
	private int gap_Penalty;
	private String stringQ;
	private String stringD;
	private int[][] result;
	private int score;
	private String[] AlignString;
	private int index1;
	private int index2;
	private int startIndex;

	public int getGap_Penalty() {
		return gap_Penalty;
	}
	public void setGap_Penalty(int gap_Penalty) {
		this.gap_Penalty = gap_Penalty;
	}
	public String getStringQ() {
		return stringQ;
	}
	public void setStringQ(String stringQ) {
		this.stringQ = stringQ;
	}
	public int[][] getResult() {
		return result;
	}
	public void setResult(int[][] result) {
		this.result = result;
	}
	public String getStringD() {
		return stringD;
	}
	public void setStringD(String stringD) {
		this.stringD = stringD;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public String[] getAlignString() {
		return AlignString;
	}
	public void setAlignString(String[] alignString) {
		AlignString = alignString;
	}
	public int getIndex2() {
		return index2;
	}
	public void setIndex2(int index2) {
		this.index2 = index2;
	}
	public int getIndex1() {
		return index1;
	}
	public void setIndex1(int index1) {
		this.index1 = index1;
	}
	public int getStartIndex() {
		return startIndex;
	}
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	private int max(int a, int b, int c) {
		return Math.max(Math.max(a, b), c);
	}

	private int max(int a, int b, int c,int d) {
		return Math.max(Math.max(Math.max(a, b), c),d);
	}
	public List<dnaSequence> run(HashMap<String, String> query_table, HashMap<String, String> database_table, String alphabet, int[][] scoring, int gap,int method){
		List<dnaSequence> sequenceInf = new ArrayList<>();
		//start alignment
		for(String queryKey : query_table.keySet()){
			//Instant start = Instant.now();

			for(String databaseKey : database_table.keySet()){

				dnaSequence sequence = new dnaSequence();
				sequence.setQueryID(queryKey);
				sequence.setDatabaseID(databaseKey);

				if(method==1)
				{
					sequenceInf.add(globalAlignedSequence(query_table.get(queryKey), database_table.get(databaseKey), scoring, gap, sequence, alphabet));
				}
				else if(method==2)
				{
					sequenceInf.add(localAlignedSequence(query_table.get(queryKey), database_table.get(databaseKey), scoring, gap, sequence, alphabet));
				}
				else if(method==3)
				{
					sequenceInf.add(doveTailAlignedSequence(query_table.get(queryKey), database_table.get(databaseKey), scoring, gap, sequence, alphabet));

				}

			}
			//Timing Information
			//Instant end = Instant.now();
			//Duration timeElapsed = Duration.between(start, end);
			//System.out.print(query_table.get(queryKey).length()+" ");
			//System.out.print(timeElapsed.toMillis());
			//	System.out.println();

		}
		return sequenceInf;
	}
	private dnaSequence globalAlignedSequence(String query, String database, int[][] scoringMatrix, int gap, dnaSequence sequence, String alphabet) {
		int solution[][] = generateAlignmentMatrix(query, database, scoringMatrix, gap, alphabet);
		sequence.setScore(solution[solution.length-1][solution[0].length-1]);
		sequence = findPath(solution, query,  database,sequence, scoringMatrix, alphabet, gap);
		return sequence;
	}
	private dnaSequence localAlignedSequence(String query, String database, int[][] scoringMatrix, int gap, dnaSequence sequence, String alphabet) {
		int solution[][] = generateAlignmentMatrixL(query, database, scoringMatrix, gap, alphabet);
		sequence.setScore(solution[this.index1][this.index2]);
		sequence = findPathLorD(solution, query,  database,sequence, scoringMatrix, alphabet, gap, this.index1, this.index2);
		return sequence;
	}

	private dnaSequence doveTailAlignedSequence(String query, String database, int[][] scoringMatrix, int gap, dnaSequence sequence, String alphabet) {
		int solution[][] = generateAlignmentMatrixD(query, database, scoringMatrix, gap, alphabet);
		int query_length=query.length();
		int database_length=database.length();
		int max=Integer.MIN_VALUE;
		for(int i=0;i<query_length+1;i++)
		{
			if(solution[i][database_length] >max)
			{
				max=solution[i][database_length];
				this.index1=i;
				this.index2=database_length;
			}
		}
		for(int j=0;j<database_length+1;j++)
		{
			if(solution[query_length][j] >max)
			{
				max=solution[query_length][j];
				this.index1=query_length;
				this.index2=j;
			}	
		}	





		sequence.setScore(solution[this.index1][this.index2]);
		sequence = findPathLorD(solution, query,  database,sequence, scoringMatrix, alphabet, gap, this.index1, this.index2);
		return sequence;
	}


	//generate alignment matrix for global

	public int[][] generateAlignmentMatrix(String stringQ, String stringD, int[][] scoringMatrix, int gap_Penalty, String alphabet){
		char query[] = stringQ.toCharArray();
		char database[] = stringD.toCharArray();
		int solution[][] = new int[query.length+1][database.length+1];
		solution[0][0] = 0;
		for (int i = 1; i < database.length+1; i++) {
			solution[0][i] = solution[0][i-1] + gap_Penalty;
		}
		for (int i = 1; i < query.length+1; i++) {
			solution[i][0] = solution[i-1][0] + gap_Penalty;
		}

		for (int i = 1; i < query.length+1; i++) {
			for (int j = 1; j < database.length+1; j++) {

				int match_or_mismatch_value = scoringMatrix[alphabet.toLowerCase().indexOf(query[i-1])][alphabet.toLowerCase().indexOf(database[j-1])];
				solution[i][j] = max(solution[i][j-1] + gap_Penalty, solution[i-1][j] + gap_Penalty, solution[i-1][j-1] + match_or_mismatch_value);
			}
		}
		return solution;	
	}

	//generate alignment for Local
	public int[][] generateAlignmentMatrixL(String stringQ, String stringD, int[][] scoringMatrix, int gap_Penalty, String alphabet){
		char query[] = stringQ.toCharArray();
		char database[] = stringD.toCharArray();
		int solution[][] = new int[query.length+1][database.length+1];
		solution[0][0] = 0;
		for (int i = 1; i < database.length+1; i++) {
			solution[0][i] = 0;
		}
		for (int i = 1; i < query.length+1; i++) {
			solution[i][0] = 0;
		}
		int max=Integer.MIN_VALUE;
		for (int i = 1; i < query.length+1; i++) {
			for (int j = 1; j < database.length+1; j++) {

				int match_or_mismatch_value = scoringMatrix[alphabet.toLowerCase().indexOf(query[i-1])][alphabet.toLowerCase().indexOf(database[j-1])];
				solution[i][j] = max(0,solution[i][j-1] + gap_Penalty, solution[i-1][j] + gap_Penalty, solution[i-1][j-1] + match_or_mismatch_value);
				if(solution[i][j]>max)
				{
					max=solution[i][j];
					this.index1=i;
					this.index2=j;
				}
			}
		}
		return solution;	
	}
	//generate alignment for Dovetail
	public int[][] generateAlignmentMatrixD(String stringQ, String stringD, int[][] scoringMatrix, int gap_Penalty, String alphabet){
		char query[] = stringQ.toCharArray();
		char database[] = stringD.toCharArray();
		int solution[][] = new int[query.length+1][database.length+1];
		solution[0][0] = 0;
		for (int i = 1; i < database.length+1; i++) {
			solution[0][i] = 0;
		}
		for (int i = 1; i < query.length+1; i++) {
			solution[i][0] = 0;
		}

		for (int i = 1; i < query.length+1; i++) {
			for (int j = 1; j < database.length+1; j++) {

				int match_or_mismatch_value = scoringMatrix[alphabet.toLowerCase().indexOf(query[i-1])][alphabet.toLowerCase().indexOf(database[j-1])];
				solution[i][j] = max(solution[i][j-1] + gap_Penalty, solution[i-1][j] + gap_Penalty, solution[i-1][j-1] + match_or_mismatch_value);
			}
		}
		//  System.out.println(Arrays.deepToString(solution));
		return solution;	
	}
	//find path for Global
	private dnaSequence findPath(int[][] solution, String query, String database,dnaSequence seq, int[][] scoringMatrix, String alphabet, int gap) {
		StringBuilder string1 = new StringBuilder();
		StringBuilder string2 = new StringBuilder();
		char stringA[] = query.toCharArray();
		char stringB[] = database.toCharArray();
		int i = solution.length - 1;
		int j = solution[0].length - 1;
		int match_or_mismatch_value;
		while (i != 0  && j != 0) {
			match_or_mismatch_value = scoringMatrix[alphabet.toLowerCase().indexOf(stringA[i-1])][alphabet.toLowerCase().indexOf(stringB[j-1])];
			if (solution[i-1][j-1] == solution[i][j] - match_or_mismatch_value) {
				string1.append(stringA[i-1]);
				string2.append(stringB[j-1]);
				i -= 1;
				j -= 1;
			} else if (solution[i][j-1] == solution[i][j] - gap) {
				string1.append(".");
				string2.append(stringB[j-1]);
				j -= 1;
			} else {
				string1.append(stringA[i-1]);
				string2.append(".");
				i -= 1;
			}
		}

		if (i == 0) {
			for (int k = 0; k < j; k++) {
				string1.append(".");
				string2.append(stringB[j-k-1]);
			}
		} else {
			for (int k = 0; k < i; k++) {
				string1.append(stringA[i-k-1]);
				string2.append(".");
			}
		}

		seq.setQueryString(string1.reverse().toString());  
		seq.setDatabaseString(string2.reverse().toString());
		seq.setIndexQuery(0);
		seq.setIndexDatabase(0);
		return seq;
	}
	//find path for Local or Dovetail
	private dnaSequence findPathLorD(int[][] solution, String query, String database, dnaSequence seq, int[][] scoringMatrix, String alphabet, int gap, int i, int j) {
		StringBuilder string1 = new StringBuilder();
		StringBuilder string2 = new StringBuilder();
		char stringA[] = query.toCharArray();
		char stringB[] = database.toCharArray();
		int match_or_mismatch_value;
		while (i != 0  && j != 0 && solution[i][j] != 0) {

			match_or_mismatch_value = scoringMatrix[alphabet.toLowerCase().indexOf(stringA[i-1])][alphabet.toLowerCase().indexOf(stringB[j-1])];

			if (solution[i-1][j-1] == solution[i][j] - match_or_mismatch_value) {
				string1.append(stringA[i-1]);
				string2.append(stringB[j-1]);
				i -= 1;
				j -= 1;
			} else if (solution[i][j-1] == solution[i][j] - gap) {
				string1.append(".");
				string2.append(stringB[j-1]);
				j -= 1;
			} else {
				string1.append(stringA[i-1]);
				string2.append(".");
				i -= 1;
			}
		}


		seq.setQueryString(string1.reverse().toString());
		seq.setDatabaseString(string2.reverse().toString());
		seq.setIndexQuery(i);
		seq.setIndexDatabase(j);
		return seq;
	}


}
