package cz.muni.fi.pa165.hrs.client.security;

/**
 * System user role {@code GrantedAuthority} constants
 *
 * @author      Andrej Kuročenko <kurocenko@mail.muni.cz>
 */
public class CustomRole {

    /** Anonymous user */
    public static final String ANONYMOUS = "ANONYMOUS";

    /** Logged user */
    public static final String LOGGED = "LOGGED";

    /** Logged user with administration privileges */
    public static final String ADMIN = "ADMIN";

}
