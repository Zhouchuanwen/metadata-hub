package GraphQL;

import GraphQL.Fetcher.MainGraphQLDataFetchers;
import GraphQL.Fetcher.MainGraphQLDataFetchersImpl;
import GraphQL.Provider.Impl.MainGraphQLProviderImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import graphql.GraphQL;

import java.io.IOException;

public class GraphQLModule extends AbstractModule
{

    @Override
    protected void configure()
    {
        this.bind(MainGraphQLDataFetchers.class).to(MainGraphQLDataFetchersImpl.class).in(Singleton.class);
    }


    @Provides
    @Singleton
    static GraphQL provideGraphQL(MainGraphQLDataFetchers mainGraphQLDataFetchers) throws IOException
    {
        return new MainGraphQLProviderImpl(mainGraphQLDataFetchers).init().getGraphQL();
    }


}
