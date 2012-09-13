package de.tudresden.cib.vis.configurations;

import cib.mf.schedule.model.activity11.Activity;
import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.data.multimodel.LinkedObject;
import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.scene.VisFactory2D;
import de.tudresden.cib.vis.scene.text.TextBuilder;
import de.tudresden.cib.vis.scene.text.TextFactory;

public class ProgressreportTextConfig extends Configuration<LinkedObject<Activity>, TextFactory.TextLabel, String> {

    public ProgressreportTextConfig(DataAccessor<LinkedObject<Activity>> data) {
        super(data, new TextFactory(), new TextBuilder());
    }

    @Override
    public void config() {
        mapper.addMapping(new PropertyMap<LinkedObject<Activity>, VisFactory2D.Label>() {
            @Override
            protected boolean condition() {
                for (LinkedObject.ResolvedLink link: data.getResolvedLinks()){
                    for(String key: new String[]{"EM5","EM6","EM7","EM8","EM9"}){
                        if(link.getLinkedQto().containsKey(key)) return true;
                    }
                }
                return false;
            }

            @Override
            protected void configure() {
                StringBuilder text = new StringBuilder();
                text.append(data.getKeyObject().getDesc());
                text.append(":\n");
                for(LinkedObject.ResolvedLink link: data.getResolvedLinks()){
                    for(String key: new String[]{"EM5","EM6","EM7","EM8","EM9"}){
                        if(link.getLinkedQto().containsKey(key)) {
                            text.append(link.getLinkedQto().get(key).getResult());
                            text.append(", ");
                        }
                    }
                }
                graphObject.setText(text.toString());
            }
        });
    }


}
