package configurations;

import cib.lib.gaeb.model.gaeb.TgItem;
import data.DataAccessor;
import data.EMFIfcAccessor;
import data.MultiModelAccessor;
import mapping.Mapper;
import mapping.PropertyMap;
import visualization.*;

import java.util.Collection;

public class IfcGaebColored3DMapper {

    private Mapper mapper;

    IfcGaebColored3DMapper(){
        DataAccessor data = new MultiModelAccessor(this.getClass().getResource("/carport"));
        VisBuilder builder = new Java3dBuilder();
        VisFactory3D visFactory3D = new Java3dFactory();
        mapper = new Mapper(data, visFactory3D, builder);
    }
    
    public static void main(String[] args){
        IfcGaebColored3DMapper self = new IfcGaebColored3DMapper();
        self.config();
    }

    private void config() {
        mapper.addMapping(new PropertyMap<MultiModelAccessor.LinkedObject<EMFIfcAccessor.EngineEObject>, VisFactory3D.Polyeder>() {
            @Override
            protected void configure() {
                EMFIfcAccessor.Geometry geometry = data.getKeyObject().getGeometry();
                graphObject.setVertizes(geometry.vertizes);
                graphObject.setNormals(geometry.normals);
                Collection<MultiModelAccessor.ResolvedLink> gaebLinks = data.getResolvedLinks();
                int price = 0;
                for(MultiModelAccessor.ResolvedLink link: gaebLinks){
                    TgItem gaeb = link.getLinkedBoQ().values().iterator().next(); // TODO: implement getFirstLinkedBoQ ...
                }
                graphObject.setColor(1,1,1);
            }
        });
    }
}
