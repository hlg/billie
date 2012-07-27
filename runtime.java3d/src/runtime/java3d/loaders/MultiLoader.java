package runtime.java3d.loaders;

import com.sun.j3d.loaders.Loader;

import javax.media.j3d.ViewSpecificGroup;
import java.util.Collection;

public interface MultiLoader extends Loader {
    public Collection<ViewSpecificGroup> getSubScenes();
}
