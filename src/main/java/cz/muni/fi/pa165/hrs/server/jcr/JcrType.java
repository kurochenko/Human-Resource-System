package cz.muni.fi.pa165.hrs.server.jcr;

/**
 * Custom Jcr Node and Property types used in application.
 * @see /resources/jackrabbit/hrsTypes.cnd
 * 
 * @author Andrej Kuročenko kurochenko at mail muni cz
 */
public enum JcrType {

    /* NODE TYPES */
    /** Root node of application repository (not same as {@code session.getRootNode()}) */
    NODE_HRS("hrs"),

    /** Node which contains all user nodes, this node is child of {@code NODE_HRS}*/
    NODE_USERS("users"),

    /** Node which represents certain user account, this node is child of {@code NODE_USERS} */
    NODE_USER("user"),

    /** Node which represents users CV in PDF format, this node is child of {@code NODE_USER} */
    NODE_CV_PDF("pdf"),

    /** Node which represents users CV in ODT format, this node is child of {@code NODE_USER} */
    NODE_CV_ODT("odt"),

    /** Node which contains all profession nodes, this node is child of {@code NODE_HRS}*/
    NODE_PROFESSIONS("professions"),

    /** Node which represents certain profession, this node is child of {@code NODE_PROFESSIONS} */
    NODE_PROFESSION("profession"),


    /* PROPERTY TYPES */
    /** Property of {@code NODE_USER} which represents hash of users password */
    PROP_PASSWORD("password"),

    /** Property of {@code NODE_USER} which represents users email address */
    PROP_EMAIL("email"),

    /** Property of {@code NODE_USER} and {@code NODE_PROFESSION} which represents users name or professions name */
    PROP_NAME("name"),

    /** Property of {@code NODE_USER} which represents administration privileges of user. */
    PROP_PRIVILEGED("privileged"),

    /** Property of {@code NODE_USER} which represents profession of user. Its also reference to {@code NODE_PROFESSION} node */
    PROP_PROFESSION("userProfession"),

    /** Property of {@code NODE_CV_PDF} and {@code NODE_CV_ODT} nodes which represents plain text content of given files */
    PROP_CONTENT("content"),

    /** Mixin type for {@code nt:file} node type, adds ability to hold {@code PROP_CONTENT} property */
    MIXIN_HASCONTENT("hascontent");


    /** Node type namespace */
    private final String NS = "hrs:";

    /** Node or attribute type name */
    private String type;

    
    JcrType(String type) {
        if (type == null) {
            throw new IllegalArgumentException("Type is null");
        }
        this.type = type;    
    }

    /**
     * Returns node or property type with namespace
     * @return node or property type with namespace
     */
    public String getType() {
        return NS + type;
    }

    /**
     * Returns node or property type with namespace
     * @return node or property type with namespaceM
     */
    @Override
    public String toString() {
        return NS + type;    
    }
}
