package configurations;

import data.DataAccessor;
import data.MultiModelAccessor;
import mapping.Mapper;
import visualization.*;

public class IfcGaebColored3DMapper {

    private Mapper mapper;

    IfcGaebColored3DMapper(){
        DataAccessor data = new MultiModelAccessor();
        VisBuilder builder = new Java3dBuilder();
        VisFactory3D visFactory3D = new Java3dFactory();
        mapper = new Mapper(data, visFactory3D, builder);
    }
    
    public static void main(String[] args){
        IfcGaebColored3DMapper self = new IfcGaebColored3DMapper();
    }
}
