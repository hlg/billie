package de.tudresden.cib.vis.data.bimserver;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * PluginMananager which doesn't use the dynamic plugin loading methods of the TNO Bimserver
 *
 * @author Helga Tauscher
 */
public class SimplePluginManager extends org.bimserver.plugins.PluginManager {

    @Override
    public String getCompleteClassPath() {
        URL[] allUrls = ((URLClassLoader) getClass().getClassLoader()).getURLs();
        String[] allPAths = new String[allUrls.length];
        for (int i = 0; i < allUrls.length; i++) {
            allPAths[i] = allUrls[i].getPath();
        }

        return StringUtils.join(allPAths, File.pathSeparator);
    }

}
