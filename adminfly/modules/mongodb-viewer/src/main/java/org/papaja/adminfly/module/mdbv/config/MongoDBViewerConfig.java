package org.papaja.adminfly.module.mdbv.config;

import com.mongodb.MongoClient;
import org.papaja.adminfly.module.mdbv.common.converter.DateTimeConverter;
import org.papaja.adminfly.module.mdbv.common.converter.RawJsonConverter;
import org.papaja.adminfly.module.mdbv.common.holder.SourceIdHolder;
import org.papaja.adminfly.module.mdbv.common.manager.MongoDatabaseManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;
import static java.util.Objects.requireNonNull;

@SuppressWarnings({"unused"})
@Configuration
@PropertySource(value = {
        "classpath:properties/module/mdbv/database.properties",
        "classpath:properties/module/mdbv/database.private.properties",
        "classpath:properties/module/mdbv/module.properties",
}, ignoreResourceNotFound = true)
public class MongoDBViewerConfig {

    protected Environment environment;

    public @Autowired
    MongoDBViewerConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public SourceIdHolder getCollectionDetector() {
        return new SourceIdHolder(environment.getProperty("module.mdbv.session.currentCollection"));
    }

    public @Bean
    MongoDatabaseManager mongoDatabaseManager() {
        return new MongoDatabaseManager(mongoClient());
    }

    @Bean
    public MongoClient mongoClient() {
        return new MongoClient(
                requireNonNull(environment.getProperty("mdbv.connection.host")),
                parseInt(requireNonNull(environment.getProperty("mdbv.connection.port")))
        );
    }

    public @Bean
    MongoCustomConversions customConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();

        converters.add(new RawJsonConverter());
        converters.add(new DateTimeConverter());

        return new MongoCustomConversions(converters);
    }

}
