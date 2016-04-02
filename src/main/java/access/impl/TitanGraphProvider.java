package access.impl;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.typesafe.config.Config;
import org.apache.commons.configuration.BaseConfiguration;
@Singleton
public class TitanGraphProvider implements Provider<TitanGraph> {
    private static TitanGraph graph;
    private static BaseConfiguration conf;

    @Inject
    public TitanGraphProvider(Config config){
        this.conf = new BaseConfiguration();

        // DynamoDB settings
        conf.setProperty("storage.backend", "com.amazon.titan.diskstorage.dynamodb.DynamoDBStoreManager");

        String accessProvider = config.getString("storage.dynamodb.client.access-provider");

        if (accessProvider.contains("Basic")) {
            // generate access key secret key by reading default credentials
            AWSCredentials awsCredentials= new DefaultAWSCredentialsProviderChain().getCredentials();
            String constructorArgs = awsCredentials.getAWSAccessKeyId()+","+awsCredentials.getAWSSecretKey();
            conf.setProperty("storage.dynamodb.client.credentials.constructor-args", constructorArgs);
        } else {
            conf.setProperty("storage.dynamodb.client.credentials.constructor-args", "");
        }
        conf.setProperty("storage.dynamodb.client.credentials.class-name", accessProvider);
        conf.setProperty("storage.dynamodb.client.endpoint", config.getString("storage.dynamodb.client.endpoint"));
        conf.setProperty("storage.dynamodb.stores.edgestore.data-model", config.getString("storage.dynamodb.stores.edgestore.data-model"));
        conf.setProperty("storage.dynamodb.stores.edgestore.capacity-read", config.getString("storage.dynamodb.stores.edgestore.capacity-read"));
        conf.setProperty("storage.dynamodb.stores.edgestore.read-rate", config.getString("storage.dynamodb.stores.edgestore.capacity-read"));
        conf.setProperty("storage.dynamodb.stores.edgestore.capacity-write", config.getString("storage.dynamodb.stores.edgestore.capacity-write"));
        conf.setProperty("storage.dynamodb.stores.edgestore.write-rate", config.getString("storage.dynamodb.stores.edgestore.capacity-write"));

        conf.setProperty("storage.dynamodb.stores.graphindex.data-model", config.getString("storage.dynamodb.stores.graphindex.data-model"));
        conf.setProperty("storage.dynamodb.stores.graphindex.capacity-read", config.getString("storage.dynamodb.stores.graphindex.capacity-read"));
        conf.setProperty("storage.dynamodb.stores.graphindex.read-rate", config.getString("storage.dynamodb.stores.graphindex.capacity-read"));
        conf.setProperty("storage.dynamodb.stores.graphindex.capacity-write", config.getString("storage.dynamodb.stores.graphindex.capacity-write"));
        conf.setProperty("storage.dynamodb.stores.graphindex.write-rate", config.getString("storage.dynamodb.stores.graphindex.capacity-write"));

        conf.setProperty("storage.dynamodb.prefix", config.getString("storage.dynamodb.prefix"));
        conf.setProperty("storage.dynamodb.client.use-gzip", config.getString("storage.dynamodb.client.use-gzip"));
        conf.setProperty("storage.dynamodb.force-consistent-read", config.getString("storage.dynamodb.force-consistent-read"));
        conf.setProperty("cache.db-cache", config.getString("cache.db-cache"));
        conf.setProperty("cache.db-cache-time", config.getString("cache.db-cache-time"));
        conf.setProperty("cache.db-cache-size", config.getString("cache.db-cache-size"));
        conf.setProperty("query.force-index", config.getString("query.force-index"));
        // Elasticsearch indexing settings
        conf.setProperty("index.search.backend", "elasticsearch");
        conf.setProperty("attributes.allow-all","true");
//        conf.setProperty("attributes.custom.attribute10.attribute-class","core.models.User");
//        conf.setProperty("attributes.custom.attribute10.serializer-class","core.models.User");
        if (config.getBoolean("index.search.elasticsearch.local")) {
            conf.setProperty("index.search.directory", "/tmp/searchindex");
            conf.setProperty("index.search.elasticsearch.client-only", "false");
            conf.setProperty("index.search.elasticsearch.local-mode", "true");
            conf.setProperty("index.search.elasticsearch.interface", "NODE");
        } else {
            conf.setProperty("index.search.hostname", config.getString("index.search.hostname"));
            conf.setProperty("index.search.elasticsearch.interface", "TRANSPORT_CLIENT");
            conf.setProperty("index.search.elasticsearch.client-only", "true");
        }
    }
    @Override
    public TitanGraph get() {
        if(graph==null){
            graph = TitanFactory.open(conf);
        }
        return graph;
    }
}
