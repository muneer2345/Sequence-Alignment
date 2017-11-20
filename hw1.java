import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class hw1 {
	static int a[][];
	//parser
	public static HashMap<String,String> fastaParser(String filename){
		HashMap<String, String> map = new HashMap<String, String>();
		String id="";
		StringBuilder string=new StringBuilder();
		try
		{
			BufferedReader text = new BufferedReader(new FileReader(filename+".txt"));
			String line;
			while ((line = text.readLine()) != null)
			{
				line = line.trim();
				if(line.startsWith(">", 0)){
					if(id!="" && string.length()!=0){
						map.put(id, string.toString());
						id="";
						string.setLength(0);
					}
					id = line.split(" ")[0].substring(5);
				}
				else{
					string.append(line);
				}

			}
			map.put(id, string.toString());
			text.close();
		}
		catch (Exception e)
		{
			System.err.format("Exception reading '%s'.", filename);
			e.printStackTrace();
		}
		return map;
	}
	//read from scoring matrix
	private static int[][] readScoringMatrix(String file, String alphabet) {
		// TODO Auto-generated method stub

		a = new int[alphabet.length()][alphabet.length()];
		int i=0;
		try
		{
			BufferedReader readL = new BufferedReader(new FileReader(file+".txt"));
			String line;

			while ((line = readL.readLine()) != null)
			{
				line = line.trim();
				String temp[] = line.split("\\s+");

				for(int j=0;j<alphabet.length();j++){
					a[i][j] = Integer.parseInt(temp[j]);
				}
				i++;

			}
			readL.close();
		}
		catch (Exception e)
		{
			System.err.format("Exception reading '%s'.", file);
			e.printStackTrace();
		}
		return a;
	}


	public static void main(String[] args) throws FileNotFoundException, IOException {

		int methodDetermine=Integer.parseInt(args[0]);
		HashMap<String, String> fasta_query_table = new HashMap<>();
		HashMap<String, String> fasta_database_table = new HashMap<>();
		String alphabet = ((new BufferedReader(new FileReader(args[3]+".txt")).readLine())).trim();
		readScoringMatrix(args[4], alphabet);
		int nearestNeighbours = Integer.parseInt(args[5]); 
		int gap = Integer.parseInt(args[6]);
		fasta_query_table=fastaParser(args[1]);
		fasta_database_table=fastaParser(args[2]);
		List<dnaSequence> resultFromMethod=new ArrayList<>();
		List<dnaSequence> topKResults;
		dnaSequence sol[] = new dnaSequence[fasta_database_table.size()*fasta_query_table.size()];



		if(methodDetermine==1)
		{
			Alignment obj=new Alignment();
			resultFromMethod=obj.run(fasta_query_table, fasta_database_table, alphabet, a, gap,methodDetermine);

		}
		else if(methodDetermine==2)
		{
			Alignment obj=new Alignment();

			resultFromMethod=obj.run(fasta_query_table, fasta_database_table, alphabet, a, gap,methodDetermine);

		}
		else if(methodDetermine==3)
		{
			Alignment obj=new Alignment();

			resultFromMethod=obj.run(fasta_query_table, fasta_database_table, alphabet, a, gap,methodDetermine);

		}

		resultFromMethod.toArray(sol);
		Arrays.sort(sol);
		topKResults = Arrays.asList(sol).subList(0, nearestNeighbours);
		//print solution
		for(dnaSequence sequence: topKResults){
			System.out.println("Score = " + sequence.getScore());
			System.out.println(sequence.getQueryID() + " " + sequence.getIndexQuery() + " " + sequence.getQueryString());
			System.out.println(sequence.getDatabaseID() + " " + sequence.getIndexDatabase() + " " + sequence.getDatabaseString());



		}

	}
}
