package nl.gjosse.main.SQL;

public enum SCHEMA {

	MySQL,
	SQLite;

	@Override
	public String toString(){
		return this.toString().toUpperCase();
	}

}