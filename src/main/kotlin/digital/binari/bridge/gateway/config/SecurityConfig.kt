package digital.binari.bridge.gateway.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@EnableWebFluxSecurity
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .authorizeExchange { exchanges ->
                exchanges
                    .pathMatchers("/actuator/health", "/actuator/info", "/actuator/health/**").permitAll()
                    .pathMatchers("/actuator/prometheus").permitAll()
                    .pathMatchers("/fallback/**").permitAll()
                    .pathMatchers("/gateway/admin/**").permitAll()
                    .anyExchange().permitAll()
            }
            .build()
    }
}
