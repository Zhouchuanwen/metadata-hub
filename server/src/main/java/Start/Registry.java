package Start;

import Database.DatabaseProvider;
import GraphQL.GraphQLDataFetchers;
import GraphQL.GraphQLProvider;
import JerseyServer.JerseyServer;
import lombok.Getter;

import java.io.IOException;

//TODO maybe seperate registry and creation of its elements
public class Registry
{
    @Getter private final DatabaseProvider databaseProvider;
    @Getter private final GraphQLDataFetchers graphQLDataFetchers;
    @Getter private final GraphQLProvider graphQLProvider;
    @Getter private final JerseyServer jerseyServer;

    public Registry() throws IOException
    {
        databaseProvider = new DatabaseProvider();
        graphQLDataFetchers = new GraphQLDataFetchers(databaseProvider);
        graphQLProvider = new GraphQLProvider(graphQLDataFetchers, databaseProvider);
        jerseyServer = new JerseyServer(graphQLProvider.init().getGraphQL());
        jerseyServer.start();
    }
}