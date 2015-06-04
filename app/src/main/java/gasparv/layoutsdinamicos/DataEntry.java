package gasparv.layoutsdinamicos;

import java.io.Serializable;
/**
 * Created by GasparV on 14/05/2015.
 */
public class DataEntry implements Serializable{
    private String id;
    private String name;
    public DataEntry(){}
    public DataEntry(String id, String name) {
        this.id = id;
        this.name = name;

    }
    public String getid() { return id; }
    public void setid(String id) {
        this.id = id;
    }
    public String getname() {
        return name;
    }
    public void setname(String name) {
        this.name = name;
    }
}
