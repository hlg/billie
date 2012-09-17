package de.tudresden.cib.vis.configurations;

import cib.mf.schedule.model.activity11.Activity;
import de.tudresden.cib.vis.data.multimodel.LinkedObject;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ActivityHelper {


    private Activity activity;

    ActivityHelper(Activity activity){
        this.activity = activity;
    }

    Map<String, SetActualComparison> collectAmounts(String[] LM_IDS, String QTO_ID, Collection<LinkedObject.ResolvedLink> resolvedLinks) {
        Date start = activity.getActivityData().getStart().getDate().toGregorianCalendar().getTime();
        Date end = activity.getActivityData().getEnd().getDate().toGregorianCalendar().getTime();
        Interval activityPeriod= new Interval(start.getTime(), end.getTime());
        long overallTime = (end.getTime()-start.getTime())/(1000*60*60*24);
        Map<String, SetActualComparison> amounts = new HashMap<String, SetActualComparison>();
        amounts.put(QTO_ID, new SetActualComparison(overallTime));
        long passedDays = 0;
        for(int month = 5; month<=9; month++){
            Interval billingPeriod = new Interval(new DateTime(2012, month, 1, 0, 0), new DateTime(2012, month+1, 1, 0, 0));
            if (activityPeriod.overlaps(billingPeriod)){
                passedDays += new Duration(activityPeriod.overlap(billingPeriod)).getStandardDays();
            }
            amounts.put(String.format("FM%d", month), new SetActualComparison(passedDays));
        }
        double done = 0;
        for(String key: LM_IDS){
            for(LinkedObject.ResolvedLink link: resolvedLinks){
                if(link.getLinkedQto().containsKey(key)) {
                    done += link.getLinkedQto().get(key).getResult();
                }
            }
            amounts.get(key).amount = done;
        }
        for(LinkedObject.ResolvedLink link: resolvedLinks){
            amounts.get(QTO_ID).amount += link.getLinkedQto().get(QTO_ID).getResult();
        }
        return amounts;
    }

    void appendActivityDescription(StringBuilder text) {
        text.append(extractActivityDescription());
        text.append(":\n");
    }

    String extractActivityDescription(){
        Activity curr = activity;
        StringBuilder sb = new StringBuilder(curr.getDesc());
        while (curr.eContainer() instanceof Activity) {
            curr = (Activity) curr.eContainer();
            sb.insert(0, " / ");
            sb.insert(0, curr.getDesc());
        }
        return sb.toString();
    }

    class SetActualComparison {
        long time = 0;
        double amount = 0;

        public SetActualComparison(long time) {
            this.time = time;
        }
    }
}
