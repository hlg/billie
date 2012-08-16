package org.bimserver.ifcemf;

import org.bimserver.plugins.PluginManager;
import org.eclipse.core.runtime.FileLocator;
import org.osgi.framework.FrameworkUtil;

import java.io.IOException;

public class OsgiPluginManager extends PluginManager {
    @Override
    public String getCompleteClassPath() {
        try {
            return FileLocator.getBundleFile(FrameworkUtil.getBundle(getClass())).getCanonicalPath();
        } catch (IOException e) {
            throw new RuntimeException("Bimserver Plugins not loaded", e);
        }
    }
}
