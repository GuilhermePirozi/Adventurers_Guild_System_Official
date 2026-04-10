package br.infnet.tp1_guilda.config;

import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;

import java.time.Duration;

@Configuration
public class CacheConfig {

    // Questão 2: o ranking bate numa consulta pesada na view. Se eu ficar dando F5 ou testando no Postman,
    // o banco refaz a mesma coisa várias vezes sem precisar. Por isso eu guardei o resultado no Redis.
    // O @Cacheable está no PainelTaticoService (service, não controller). TTL de 1 dia: depois disso
    // limpa sozinho e busca de novo no banco. O endpoint continua igual (top 10, 15 dias, índice de
    // prontidão); eu só não mexi na view. disableCachingNullValues = não cachear valor nulo.
    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder) -> builder
                .withCacheConfiguration("topMissoes15dias",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofDays(1))
                                .disableCachingNullValues()
                );
    }
}
