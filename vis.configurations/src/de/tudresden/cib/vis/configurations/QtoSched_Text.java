package de.tudresden.cib.vis.configurations;

import cib.mf.schedule.model.activity11.Activity;
import de.tudresden.cib.vis.data.multimodel.LinkedObject;
import de.tudresden.cib.vis.filter.Condition;
import de.tudresden.cib.vis.mapping.Configuration;
import de.tudresden.cib.vis.mapping.Mapper;
import de.tudresden.cib.vis.mapping.PropertyMap;
import de.tudresden.cib.vis.scene.VisFactory2D;

import java.util.Map;

public class QtoSched_Text<S> extends Configuration<LinkedObject<Activity>, S> {

    private final String[] LM_IDS;
    private final String QTO_ID;

    public QtoSched_Text(Mapper<LinkedObject<Activity>, ?, S> mapper, String[] LM_IDS, String QTO_ID) {
        super(mapper);
        this.LM_IDS = LM_IDS;
        this.QTO_ID = QTO_ID;
    }

    @Override
    public void config() {
        mapper.addMapping(new Condition<LinkedObject<Activity>>() {
            @Override
            public boolean matches(LinkedObject<Activity> data) {
                for (LinkedObject.ResolvedLink link : data.getResolvedLinks()) {
                    for (String key : LM_IDS) {
                        if (link.getLinkedQto().containsKey(key)) return true;
                    }
                }
                return false;
            }
        }, new PropertyMap<LinkedObject<Activity>, VisFactory2D.Label>() {

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
