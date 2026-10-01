package org.example.support;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration(proxyBeanMethods = false)
public class MongoTestConfig {
    @Bean(destroyMethod = "shutdown")
    MongoServer mongoServer() {
        MongoServer server = new MongoServer(new MemoryBackend());
        server.bind("127.0.0.1", 0);
        return server;
    }

    @Bean(destroyMethod = "close")
    MongoClient mongoClient(MongoServer server) {
        return MongoClients.create(server.getConnectionString());
    }
}
