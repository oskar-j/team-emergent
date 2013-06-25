/**
 * 
 */
package EmergingTeams;

/**
 * @author Oskar
 *
 */
public class Team {
	
	String name;
	short id;
	
	public Team(){
		
	}
	
	public Team(String name, short id){
		this.name = name;
		this.id = id;
	}
	
	public String toString(){
		return "Team " + id + " " + name;
	}

}
