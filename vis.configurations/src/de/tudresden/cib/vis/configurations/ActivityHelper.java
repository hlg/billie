package de.tudresden.cib.vis.configurations;

import cib.mf.schedule.model.activity11.Activity;
import cib.mf.schedule.model.activity11.Timestamp;
import de.tudresden.cib.vis.data.multimodel.LinkedObject;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Interval;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ActivityHelper {


    private Activity activity;

    public ActivityHelper(Activity activity){
        this.activity = activity;
    }

    Map<String, SetActualComparison> collectAmounts(String[] LM_IDS, String QTO_ID, Collection<LinkedObject.ResolvedLink> resolvedLinks) {
        DateTime start = new DateTime(activity.getActivityData().getStart().getDate().toGregorianCalendar());
        DateTime end = new DateTime(activity.getActivityData().getEnd().getDate().toGregorianCalendar()).plus(Days.days(1));
        Interval activityPeriod= new Interval(start, end);
        int overallTime = Days.daysBetween(start, end).getDays();
        Map<String, SetActualComparison> amounts = new HashMap<String, SetActualComparison>();
        amounts.put(QTO_ID, new SetActualComparison(overallTime));
        long passedDays = 0;
        for(int month = 4; month<=8; month++){
            Interval billingPeriod = new Interval(new DateTime(2012, month, 1, 0, 0), new DateTime(2012, month+1, 1, 0, 0));
            if (activityPeriod.overlaps(billingPeriod)){
                passedDays += Days.daysIn(activityPeriod.overlap(billingPeriod)).getDays();
            }
            amounts.put(String.format("FM%d", month+1), new SetActualComparison(passedDays));
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

    public String extractActivityDescription(){
        Activity curr = activity;
        StringBuilder sb = new StringBuilder(curr.getDesc());
        while (curr.eContainer() instanceof Activity) {
            curr = (Activity) curr.eContainer();
            sb.insert(0, " / ");
            sb.insert(0, curr.getDesc());
        }
        return sb.toString();
    }

    public DateTime getStartDate() {
        return new DateTime(activity.getActivityData().getStart().getDate().toGregorianCalendar());
    }

    public DateTime getEndDate() {
        return new DateTime(activity.getActivityData().getEnd().getDate().toGregorianCalendar()).plus(Days.days(1));
    }

    public long getStartDateInMillis() {    // TODO move to ActivityHelper
        Timestamp timeStamp = activity.getActivityData().getStart();
        long dateMillis = timeStamp.getDate().toGregorianCalendar().getTimeInMillis();
        long timeMillis = timeStamp.getTime().toGregorianCalendar().getTimeInMillis();
        return dateMillis + timeMillis;
    }

    class SetActualComparison {
        long time = 0;
        double amount = 0;

        public SetActualComparison(long time) {
            this.time = time;
        }
    }
}
