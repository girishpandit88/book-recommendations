package access;

import access.impl.BookRecommendationAccessImpl;
import access.impl.TitanGraphProvider;
import com.esotericsoftware.kryo.Kryo;
import com.google.inject.AbstractModule;
import com.thinkaurelius.titan.core.TitanGraph;

public class AccessModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(TitanGraph.class).toProvider(TitanGraphProvider.class).asEagerSingleton();
        Kryo kryo = new Kryo();
        kryo.register(Book.class);
        bind(Kryo.class).toInstance(kryo);
        bind(BookRecommendationAccess.class).to(BookRecommendationAccessImpl.class).asEagerSingleton();
    }
}
