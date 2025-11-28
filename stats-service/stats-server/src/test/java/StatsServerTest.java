
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import stats.server.StatsServer;

@SpringBootTest(classes = StatsServer.class)
class StatsServerTest {

    @Test
    void contextLoads() {
    }

    @Test
    void main_ShouldStartApplication() {
        StatsServer.main(new String[]{});
    }
}