package EmergingTeams;

public class Competency {
	
	public Competency(){
		;
	}
	
	public Competency(String name, short id){
		this.name = name;
		this.id = id;
	}

	private String name;
	private short id;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public short getId() {
		return id;
	}
	public void setId(short id) {
		this.id = id;
	}
	
	
	
}
