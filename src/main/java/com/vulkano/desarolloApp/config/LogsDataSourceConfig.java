package com.vulkano.desarolloApp.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "logsEntityManagerFactory",
        transactionManagerRef = "logsTransactionManager",
        basePackages = {"com.vulkano.desarolloApp.repository.logs"}
)
public class LogsDataSourceConfig {

    @Bean
    @ConfigurationProperties("app.datasource.logs")
    public DataSourceProperties logsDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "logsDataSource")
    public DataSource logsDataSource() {
        return logsDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean(name = "logsEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean logsEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("logsDataSource") DataSource dataSource) {

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        properties.put("hibernate.show_sql", "true");

        return builder
                .dataSource(dataSource)
                .packages("com.vulkano.desarolloApp.models.logs")
                .properties(properties)
                .persistenceUnit("logsPU")
                .build();
    }

    @Bean(name = "logsTransactionManager")
    public PlatformTransactionManager logsTransactionManager(
            @Qualifier("logsEntityManagerFactory") LocalContainerEntityManagerFactoryBean logsEntityManagerFactory) {
        return new JpaTransactionManager(logsEntityManagerFactory.getObject());
    }
}