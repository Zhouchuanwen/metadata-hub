package JerseyServer.Impl;

import Config.Config;
import JerseyServer.HttpController.*;
import JerseyServer.HttpServer;
import com.google.inject.Inject;
import graphql.GraphQL;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;


public class JerseyServerImpl implements HttpServer
{
    private final GraphQL graphQL;

    private Config config;
    private static URI BASE_URI;
    private final ResourceConfig resourceConfig;
    private final org.glassfish.grizzly.http.server.HttpServer server;
    private boolean isStarted = false;

    @Inject
    public JerseyServerImpl(GraphQL graphQl, Config config,
                            GraphQLController c0, TestConsoleController c1,
                            WebUIController c2, CrawlerAPIProxyController c3,
                            WebUIMetadataHelperController c4, ExportController c5,
                            QueryEditorStorageController c6, WebUICategoriesController c7
    )
    {
        this.config = config;
        BASE_URI = UriBuilder.fromUri("http://" + config.getProperty("server-host") + "/")
            .port(Integer.parseInt(config.getProperty("server-port"))).build();
        this.graphQL = graphQl;

        resourceConfig = new ResourceConfig();
        resourceConfig.register(c0);
        resourceConfig.register(c1);
        resourceConfig.register(c2);
        resourceConfig.register(c3);
        resourceConfig.register(c4);
        resourceConfig.register(c5);
        resourceConfig.register(c6);
        resourceConfig.register(c7);

        resourceConfig.register(ErrorHandler.class);
        this.server = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, resourceConfig, false);
    }

    @Override
    public void start()
    {
        if (isStarted) throw new RuntimeException("Server is/was already started");
        isStarted = true;

        try
        {
            System.out.println("Jersey-Server is starting");

            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    server.shutdownNow();
                }
            }));

            server.start();

            System.out.println("Jersey-Server started\n");
            System.out.println("Listening-Address: " + config.getProperty("server-host") + " | Port: " + config.getProperty("server-port"));
            System.out.println("GRAPHQL-ENDPOINT: http://localhost:" + config.getProperty("server-port") + "/graphql/?query=" + getGraphQLExampleQuery());
            System.out.println("GRAPHQL-TEST-CONSOLE: http://localhost:" + config.getProperty("server-port") + "/testconsole/");
            System.out.println("WEB-GUI: http://localhost:" + config.getProperty("server-port"));

        }
        catch (IOException ex)
        {
            Logger.getLogger(HttpServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void shutdown()
    {
        this.server.shutdown();
    }

    @Override
    public void shutdownNow()
    {
        this.server.shutdownNow();
    }

    private String getGraphQLExampleQuery() throws UnsupportedEncodingException {
        return URLEncoder.encode("query{searchForFileMetadata(limitFetchingSize:1){files{name,type,metadata{name,value}}}}","UTF-8");
    }
}
