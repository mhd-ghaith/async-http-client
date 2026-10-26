import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Aggregator {

    private WebClient webClient;

    public Aggregator() {
        this.webClient = new WebClient();
    }

    /* send task to list of workers */
    public List<String> sendTasksToWorkers(List<String> workersAddresses, List<String> tasks) {
        return sendTasksToWorkers(workersAddresses, tasks, null);
    }

    /* send task to list of workers, with a custom header on every request */
    public List<String> sendTasksToWorkers(List<String> workersAddresses, List<String> tasks, String headers) {
        if (workersAddresses.size() != tasks.size()) {
            throw new IllegalArgumentException(
                    "Number of workers (" + workersAddresses.size()
                            + ") does not match number of tasks (" + tasks.size() + ")");
        }

        // 1) Fire ALL requests first, without waiting for any response
        List<CompletableFuture<String>> futures = new ArrayList<>();
        for (int i = 0; i < workersAddresses.size(); i++) {
            byte[] payload = tasks.get(i).getBytes(StandardCharsets.UTF_8);
            futures.add(webClient.sendTask(workersAddresses.get(i), payload, headers));
        }

        // 2) Then collect the results in the same order the tasks were sent
        List<String> results = new ArrayList<>();
        for (CompletableFuture<String> future : futures) {
            results.add(future.join());
        }
        return results;
    }
}