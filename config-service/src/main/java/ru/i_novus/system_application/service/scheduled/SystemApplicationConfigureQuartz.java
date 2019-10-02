package ru.i_novus.system_application.service.scheduled;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.inovus.ms.rdm.sync.rest.RdmSyncRest;

import java.util.Map;

import static org.quartz.CronScheduleBuilder.cronSchedule;

@Configuration
@EnableScheduling
@DependsOn("liquibase")
public class SystemApplicationConfigureQuartz {

    @Value("${rdm.sync.cron}")
    private String cronFrequency;

    @Autowired
    private RdmSyncRest rdmSyncRest;


    @Bean
    public JobDetail jobDetail() {
        return JobBuilder.newJob().ofType(RdmSyncJob.class)
                .storeDurably()
                .withIdentity("Rdm_Sync_Job_Detail")
                .withDescription("Synchronisation with RDM")
                .usingJobData(new JobDataMap())
                .build();
    }

    @Bean
    public Trigger trigger(JobDetail job) {
        return TriggerBuilder.newTrigger().forJob(job)
                .withIdentity("Rdm_Sync_Trigger")
                .withDescription("Trigger for Rdm sync job")
                .withSchedule(cronSchedule(cronFrequency))
                .build();
    }

    @Bean
    public SchedulerFactoryBeanCustomizer schedulerContextCustomizer() {
        return schedulerFactoryBean -> schedulerFactoryBean.setSchedulerContextAsMap(Map.of(RdmSyncRest.class.getSimpleName(), rdmSyncRest));
    }

}