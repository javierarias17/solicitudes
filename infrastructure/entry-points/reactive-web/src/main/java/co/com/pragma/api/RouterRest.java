package co.com.pragma.api;

import co.com.pragma.api.config.ApplicationPath;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class RouterRest {
    private final ApplicationPath applicationPath;
    private final Handler handler;

    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST(applicationPath.getApplication()), handler::listenSaveApplication);
    }
}
