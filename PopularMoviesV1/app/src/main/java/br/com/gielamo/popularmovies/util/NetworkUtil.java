package br.com.gielamo.popularmovies.util;

import android.net.Uri;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public final class NetworkUtil {
    private NetworkUtil() {
    }

    public static String getURIContent(Uri uri) throws IOException {
        String content = null;

        URLConnection connection = openConnection(uri);

        if (connection != null) {
            connection.connect();

            content = IOUtils.toString(connection.getInputStream(), "UTF-8");
        }

        return content;
    }

    private static URLConnection openConnection(Uri uri) throws IOException {

        URL url = new URL(uri.toString());

        return url.openConnection();
    }
}
