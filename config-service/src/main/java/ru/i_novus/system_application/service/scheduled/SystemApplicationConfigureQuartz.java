package ru.i_novus.system_application.service.scheduled;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import ru.inovus.ms.rdm.sync.rest.RdmSyncRest;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }

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
    public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource, JobDetail jobDetail, Trigger trigger)
            throws IOException {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setDataSource(dataSource);
        factory.setJobDetails(jobDetail);
        factory.setQuartzProperties(quartzProperties());
        factory.setOverwriteExistingJobs(true);
        factory.setAutoStartup(true);
        factory.setTriggers(trigger);

        Map<String, Object> jobBeans = new HashMap<>();
        jobBeans.put(RdmSyncRest.class.getSimpleName(), rdmSyncRest);
        factory.setSchedulerContextAsMap(jobBeans);

        return factory;
    }

}