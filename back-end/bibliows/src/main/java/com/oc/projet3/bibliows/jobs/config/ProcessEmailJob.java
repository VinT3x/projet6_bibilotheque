package com.oc.projet3.bibliows.jobs.config;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.Scheduled;



@Configuration
    @EnableBatchProcessing
    public class ProcessEmailJob {

        @Autowired
        private JobBuilderFactory jobBuilderFactory;
        @Autowired
        private StepBuilderFactory stepBuilderFactory;

        @Autowired
        private SimpleJobLauncher jobLauncher;

        private static Logger logger = LogManager.getLogger(ProcessEmailJob.class);

        @Bean
        public ResourcelessTransactionManager resourcelessTransactionManager() {
            return new ResourcelessTransactionManager();
        }

        @Bean
        public MapJobRepositoryFactoryBean mapJobRepositoryFactory(ResourcelessTransactionManager txManager)
                throws Exception {

            MapJobRepositoryFactoryBean factory = new MapJobRepositoryFactoryBean(txManager);

            factory.afterPropertiesSet();

            return factory;
        }


        @Scheduled(cron = "${app.email.cron}")
        private void perform() throws Exception {
            logger.info("Job Started at :" + new Date());

            JobParameters param = new JobParametersBuilder().addString("JobID", String.valueOf(System.currentTimeMillis()))
                    .toJobParameters();

            JobExecution execution = jobLauncher.run(sendEmails(), param);

            logger.info("Job finished with status :" + execution.getStatus());

        }

        @Bean
        public Job sendEmails() {
            return jobBuilderFactory.get("sendEmails").incrementer(new RunIdIncrementer()).flow(step1()).end().build();
        }

        @Bean
        public Step step1() {
            logger.info("relance utilisateur par email");
            return stepBuilderFactory.get("step1").tasklet(emailSendingTasklet()).build();
        }

        @Bean
        public EmailSendingTasklet emailSendingTasklet() {
            return new EmailSendingTasklet();
        }

        @Bean
        public SimpleJobLauncher jobLauncher(JobRepository jobRepository) {
            SimpleJobLauncher launcher = new SimpleJobLauncher();
            launcher.setJobRepository(jobRepository);
            return launcher;
        }
    }
