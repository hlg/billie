package de.tudresden.cib.vis.configurations;

import cib.mf.schedule.model.activity11.Activity;
import de.tudresden.cib.vis.data.DataAccessor;
import de.tudresden.cib.vis.data.multimodel.LinkedObject;
import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.scene.VisFactory2D;
import de.tudresden.cib.vis.scene.text.TextBuilder;
import de.tudresden.cib.vis.scene.text.TextFactory;

import java.util.Map;

public class ProgressreportTextConfig extends Configuration<LinkedObject<Activity>, TextFactory.TextLabel, String> {

    private final String[] LM_IDS;
    private final String QTO_ID;

    public ProgressreportTextConfig(DataAccessor<LinkedObject<Activity>> data, String[] lm_ids, String qto_id) {
        super(data, new TextFactory(), new TextBuilder());
        this.LM_IDS = lm_ids;
        this.QTO_ID = qto_id;
    }

    @Override
    public void config() {
        mapper.addMapping(new PropertyMap<LinkedObject<Activity>, VisFactory2D.Label>() {

            @Override
            protected boolean condition() {
                for (LinkedObject.ResolvedLink link: data.getResolvedLinks()){
                    for(String key: LM_IDS){
                        if(link.getLinkedQto().containsKey(key)) return true;
                    }
                }
                return false;
            }

            @Override
            protected void configure() {
                StringBuilder text = new StringBuilder();
                ActivityHelper activityHelper = new ActivityHelper(data.getKeyObject());
                activityHelper.appendActivityDescription(text);
                Map<String, ActivityHelper.SetActualComparison> activityData = activityHelper.collectAmounts(LM_IDS, QTO_ID, data.getResolvedLinks());
                for (Map.Entry<String, ActivityHelper.SetActualComparison> entry : activityData.entrySet()) {
                    if (!entry.getKey().equals(QTO_ID) && entry.getValue().amount > 0) {
                        text.append("\t");
                        text.append(entry.getKey());
                        text.append(": actual ");
                        text.append((int) (100 * entry.getValue().amount / activityData.get(QTO_ID).amount));
                        text.append("%, expected ");
                        text.append((int) (100 * entry.getValue().time / activityData.get(QTO_ID).time));
                        text.append("%\n");
                    }
                }
                text.append(" of ");
                text.append(activityData.get(QTO_ID).amount);
                text.append(" in total");
                graphObject.setText(text.toString());
            }
        });
    }
}
