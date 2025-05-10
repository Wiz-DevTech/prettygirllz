package com.wizdevtech.logistics.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class JpaConfig {

    @Autowired
    private Environment env;

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan(
                "com.wizdevtech.logistics.core.model",
                "com.wizdevtech.logistics.infrastructure.entity"  // Corrected package path
        );
        em.setJpaVendorAdapter(jpaVendorAdapter());

        // Add Hibernate properties from application properties with defaults
        Properties jpaProperties = new Properties();

        // SQL logging properties
        jpaProperties.put("hibernate.show_sql",
                env.getProperty("spring.jpa.show-sql", "true"));
        jpaProperties.put("hibernate.format_sql",
                env.getProperty("spring.jpa.properties.hibernate.format_sql", "true"));

        // Schema generation strategy
        jpaProperties.put("hibernate.hbm2ddl.auto",
                env.getProperty("spring.jpa.hibernate.ddl-auto", "update"));

        // Database dialect - critical for SQL generation
        jpaProperties.put("hibernate.dialect",
                env.getProperty("spring.jpa.properties.hibernate.dialect", "org.hibernate.dialect.H2Dialect"));

        // Performance settings
        jpaProperties.put("hibernate.jdbc.batch_size",
                env.getProperty("spring.jpa.properties.hibernate.jdbc.batch_size", "10"));
        jpaProperties.put("hibernate.order_inserts",
                env.getProperty("spring.jpa.properties.hibernate.order_inserts", "true"));

        // Important for testing - disable contextual LOB creation
        jpaProperties.put("hibernate.temp.use_jdbc_metadata_defaults",
                env.getProperty("spring.jpa.properties.hibernate.temp.use_jdbc_metadata_defaults", "false"));

        em.setJpaProperties(jpaProperties);

        return em;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setShowSql(true);
        adapter.setGenerateDdl(true);  // Enable DDL generation for testing
        adapter.setDatabase(Database.H2);  // Explicitly set database type
        adapter.setDatabasePlatform("org.hibernate.dialect.H2Dialect");  // Set dialect directly on adapter
        return adapter;
    }

    @Bean
    public String jpaConfigTest() {
        return "JPA Config is active";
    }
}