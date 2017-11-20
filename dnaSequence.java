
public class dnaSequence implements Comparable<dnaSequence>{
	//class to store sequence information
	private String queryID;
	private String queryString;
	private int indexQuery;
	private String databaseID;
	private String databaseString;
	private int indexDatabase;
	private int score;

	public int compareTo(dnaSequence compareSequence) {

		int compareScore = ((dnaSequence) compareSequence).getScore();

		return compareScore - this.score;

	}

	public String getQueryID() {
		return queryID;
	}

	public void setQueryID(String queryID) {
		this.queryID = queryID;
	}

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public int getIndexQuery() {
		return indexQuery;
	}

	public void setIndexQuery(int indexQuery) {
		this.indexQuery = indexQuery;
	}

	public String getDatabaseID() {
		return databaseID;
	}

	public void setDatabaseID(String databaseID) {
		this.databaseID = databaseID;
	}

	public String getDatabaseString() {
		return databaseString;
	}

	public void setDatabaseString(String databaseString) {
		this.databaseString = databaseString;
	}

	public int getIndexDatabase() {
		return indexDatabase;
	}

	public void setIndexDatabase(int indexDatabase) {
		this.indexDatabase = indexDatabase;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}


}
