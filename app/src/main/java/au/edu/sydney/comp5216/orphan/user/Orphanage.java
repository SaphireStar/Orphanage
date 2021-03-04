package au.edu.sydney.comp5216.orphan.user;

import java.util.HashMap;
import java.util.Map;

/** Orphanage domain class
 * @Author: Yiqing Yang
 */
public class Orphanage {

    // id
    private String id;

    // name
    private String name;

    // address
    private String address;

    // image url
    private String image;

    // description
    private String description;

    private String documentId;

    public String getId(){return id;}
    public void setId(String id){
        this.id = id;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getDescription(){
        return description;
    }
    public void setDescription(String description){
        this.description = description;
    }

    public String getAddress(){
        return address;
    }
    public void setAddress(String address){this.address = address;}

    public String getImage(){
        return image;
    }
    public void setImage(String image){this.image = image;}

    public String getDocumentId(){return documentId;}
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    /**
     * Maps the information to a json style
     */
    public Map<String, Object> toFireBaseFormat(){
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("name", name);
        map.put("address", address);
        map.put("description", description);
        map.put("image", image);
        return map;
    }
}
