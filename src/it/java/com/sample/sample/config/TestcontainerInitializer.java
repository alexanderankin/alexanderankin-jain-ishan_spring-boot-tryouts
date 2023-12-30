package com.sample.sample.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.testcontainers.consul.ConsulContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.images.PullPolicy;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestcontainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    public static final String KAFKA_DOCKER_IMAGE = "confluentinc/cp-kafka";
    public static final String KAFKA_DOCKER_IMAGE_VERSION = "5.2.7-1";
    public static final String CONSUL_DOCKER_IMAGE = "consul";
    public static final String CONSUL_DOCKER_IMAGE_VERSION = "1.5.3";
    public static final String VAULT_DOCKER_IMAGE = "vault";
    public static final String VAULT_DOCKER_IMAGE_VERSION = "1.10.7";
    public static final String MYSQL_DOCKER_IMAGE = "mysql";
    public static final String MYSQL_DOCKER_IMAGE_VERSION = "5.7.32";

    private static final MySQLContainer<?> mySQLContainer;
    private static final KafkaContainer kafkaContainer;
    // private static final VaultContainer<?> vaultContainer;
    private static final ConsulContainer consulContainer;

    static {
        // noinspection resource
        mySQLContainer = new MySQLContainer<>(DockerImageName.parse(MYSQL_DOCKER_IMAGE)
                .withTag(MYSQL_DOCKER_IMAGE_VERSION))
                .withImagePullPolicy(PullPolicy.defaultPolicy())
                .withReuse(true);

        kafkaContainer = new KafkaContainer(
                DockerImageName.parse(KAFKA_DOCKER_IMAGE).withTag(KAFKA_DOCKER_IMAGE_VERSION))
                .withReuse(true);

        consulContainer =
                new ConsulContainer(DockerImageName.parse(CONSUL_DOCKER_IMAGE).withTag(CONSUL_DOCKER_IMAGE_VERSION))
                        // .waitingFor(new HttpWaitStrategy().forPort(8500)
                        //         .withStartupTimeout(Duration.ofSeconds(5)))
                        .withReuse(true);

//        vaultContainer =
//                new VaultContainer<>(DockerImageName.parse(VAULT_DOCKER_IMAGE).withTag(VAULT_DOCKER_IMAGE_VERSION))
//                        .withVaultToken("test-root-token")
//                        .withReuse(true)
//                        .withClasspathResourceMapping("testdata/feature_flags.json",
//                                "feature_flags.json",
//                                BindMode.READ_ONLY)
//                        .withSecretInVault("secret/apps/" + "sampleapp", "feature=true")
//                        .withInitCommand("login test-root-token")
//                        .withInitCommand("secrets enable -version=2 kv")
//                        .withInitCommand("secrets enable -path=secrets kv")
//                        .withInitCommand("kv put -format=json secrets/apps/global/features @feature_flags.json");
//        Startables.deepStart(vaultContainer, consulContainer, kafkaContainer, mySQLContainer).join();
        Startables.deepStart(consulContainer, kafkaContainer, mySQLContainer).join();
    }

    public static String getKafkaContainerBootstrapServer() {
        return kafkaContainer.getBootstrapServers();
    }

    @Override
    public void initialize(final ConfigurableApplicationContext applicationContext) {
        applicationContext.getEnvironment().getPropertySources().addFirst(
                new MapPropertySource("mysqlContainer", Map.of(
                        "jaja.kafka.bootstrap.servers", kafkaContainer.getBootstrapServers(),
//                        "spring.cloud.vault.port", vaultContainer.getFirstMappedPort(),
                        "spring.cloud.consul.port", consulContainer.getFirstMappedPort(),
                        "spring.datasource.url",
                        mySQLContainer.getJdbcUrl()+"jdbc:mysql://" + mySQLContainer.getHost() + ":" + mySQLContainer.getFirstMappedPort() +
                                "/tryout?createDatabaseIfNotExist=true&user=root&password=test",
//                        "spring.datasource.username", mySQLContainer.getUsername(),
//                        "spring.datasource.password", mySQLContainer.getPassword(),
                        "spring.datasource.driver-class-name", mySQLContainer.getDriverClassName()
                ))
        );
    }
}
