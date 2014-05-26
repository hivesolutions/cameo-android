package pt.hive.cameo;

/**
 * Abstract class responsible for the handling of remote json request that may
 * or may not require and underlying authentication process.
 *
 * The handling of the authentication should be automatic and the proper panel
 * should be raised upon the invalidation of credentials.
 *
 * @author João Magalhães <joamag@hive.pt>
 */
public class ProxyRequest {

    /**
     * The relative URL path for the request that is going to be performed, this
     * should be JSON based (eg: api/info.json).
     */
    private String path;

    /**
     * Relative path to the URL that is going to be called in case a login
     * operation is required, note that the login action is always going to be
     * show for such operations.
     */
    private String loginPath;

    public ProxyRequest(String path, String loginPath) {
        this.path = path;
        this.loginPath = path;
    }

    /**
     * Starts the loading of the request, this is considered to be the trigger
     * of the remote call operation, the proper callback methods will be called
     * upon the proper loading of the operations.
     */
    public void load() {
    }

    public void showLogin() {
    }
}
