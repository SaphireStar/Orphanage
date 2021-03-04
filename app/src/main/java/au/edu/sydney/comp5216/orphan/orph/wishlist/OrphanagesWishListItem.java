package au.edu.sydney.comp5216.orphan.orph.wishlist;

/**
 * the class OrphanagesWishListItem
 */
public class OrphanagesWishListItem {
    private String id;
    private String name;
    private String type;
    private String description;

    /**
     * the constructor
     * @param id the id of the item
     * @param name the name of the item
     * @param type the type of the item
     * @param description the description of the item
     */
    public OrphanagesWishListItem(String id, String name, String type, String description) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
    }

    /**
     * get the id of the item
     * @return id
     */
    public String getId() {
        return this.id;
    }

    /**
     * get the name of the item
     * @return name
     */
    public String getName() {
        return this.name;
    }

    /**
     * get the type of the item
     * @return type
     */
    public String getType() {
        return this.type;
    }

    /**
     * get the description of the item
     * @return description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * set the name attribute
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * set the type attribute
     * @param type the type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * set the description attribute
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

}
