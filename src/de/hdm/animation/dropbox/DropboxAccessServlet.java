package de.hdm.animation.dropbox;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxSessionStore;
import com.dropbox.core.DbxStandardSessionStore;
import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.util.LangUtil;

import de.hdm.animation.Common;

public abstract class DropboxAccessServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /*
     * The AppInfo file contains the key and the app secret of the HdM file
     * sharing app.
     */
    private static final String appInfoFileName = "appInfo.xml";
    private static Properties appProps = new Properties();
    private static final String key = "m4oul02qsxvye2f";
    private static final String secret = "u7a8t3gi31o0r5l";

    

    public void init() throws ServletException {
        super.init();
        File appInfoFile = new File(System.getenv("appdata") +  "/HdMSharingApp/" + appInfoFileName);
        if (!appInfoFile.exists()) {
            System.out.println("file not found: " + appInfoFile.getAbsolutePath() + ". Using hard coded data.");
            appProps.put("key", key);
            appProps.put("secret", secret);
            Common.saveXMLFile(appInfoFile, appProps);
        } else {
            Common.loadXMLFile(appInfoFile, appProps);
        }
    }
    DbxSessionStore getSessionStore(HttpServletRequest request) {
        // Select a spot in the session for DbxWebAuth to store the CSRF token.
        return new DbxStandardSessionStore(request.getSession(true), "dropbox-auth-csrf-token");
    }

    DbxWebAuth getWebAuth(HttpServletRequest request) {
        return new DbxWebAuth(getRequestConfig(request), getDbxAppInfo());
    }

    String getRedirectUri(HttpServletRequest request) {
        return getUrl(request, "/DirectoryAnimation/StoreAccessTokenLocally");
    }

    DbxRequestConfig getRequestConfig(HttpServletRequest request) {
        return DbxRequestConfig.newBuilder("HdMSharingApp").withUserLocaleFrom(request.getLocale()).build();
    }

    String getUrl(HttpServletRequest request, String path) {
        URL requestUrl;
        try {
            requestUrl = new URL(request.getRequestURL().toString());
            return new URL(requestUrl, path).toExternalForm();
        } catch (MalformedURLException ex) {
            throw LangUtil.mkAssert("Bad URL", ex);
        }
    }

    // Read app info file (contains app key and app secret)
    DbxAppInfo getDbxAppInfo() {
        return new DbxAppInfo(appProps.getProperty("key"), appProps.getProperty("secret"));
    }

}
