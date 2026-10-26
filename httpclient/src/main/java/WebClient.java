import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class WebClient {

    private HttpClient client;

    /* instantiate web client */
    /* Read more about Builder pattern https://en.wikipedia.org/wiki/Builder_pattern*/
    public WebClient() {
        this.client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
    }

    /* send task (post http request) asynchronously */
    public CompletableFuture<String> sendTask(String url, byte[] requestPayload) {
        return sendTask(url, requestPayload, null);
    }

    /* send task (post http request) asynchronously with custom headers*/
    public CompletableFuture<String> sendTask(String url, byte[] requestPayload, String headers) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofByteArray(requestPayload));

        // headers arrives as "Name: value", e.g. "X-Debug: true"
        if (headers != null && !headers.isBlank()) {
            int idx = headers.indexOf(':');
            if (idx <= 0) {
                throw new IllegalArgumentException("Header must be in 'Name: value' format: " + headers);
            }
            builder.header(headers.substring(0, idx).trim(), headers.substring(idx + 1).trim());
        }

        return client.sendAsync(builder.build(), HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
    }
}
