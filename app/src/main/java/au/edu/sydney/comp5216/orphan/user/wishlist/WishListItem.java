package au.edu.sydney.comp5216.orphan.user.wishlist;

/**
 * the class WishListItem
 */
public class WishListItem {
    private String id;
    private String name;
    private String description;

    /**
     * the constructor
     * @param id the id of the item
     * @param name the name of the item
     * @param description the description of the item
     */
    public WishListItem(String id, String name, String description) {
        this.id = id;
        this.name = name;
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
     * set the description attribute
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
