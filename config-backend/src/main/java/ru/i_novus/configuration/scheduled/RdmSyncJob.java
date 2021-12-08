package ru.i_novus.configuration.scheduled;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.inovus.ms.rdm.sync.rest.RdmSyncRest;

@Component
public class RdmSyncJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(RdmSyncRest.class);

    private RdmSyncRest getRdmSyncRest(JobExecutionContext context) {
        String key = RdmSyncRest.class.getSimpleName();
        try {
            return (RdmSyncRest) context.getScheduler().getContext().get(key);
        } catch (SchedulerException e) {
            logger.error("cannot get " + key + "property", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void execute(JobExecutionContext context) {
        logger.info("rdm sync is started");
        getRdmSyncRest(context).update();
        logger.info("rdm sync is completed");
    }
}
