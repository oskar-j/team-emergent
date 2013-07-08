package NetworkGenerator;

public class NodeAgent {

    private String label;
   
    public NodeAgent(String label) {
        this.setLabel(label);
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}