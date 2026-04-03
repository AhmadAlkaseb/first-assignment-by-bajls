package dk.fakeinfo;

import dk.fakeinfo.model.PostalCode;
import dk.fakeinfo.repository.PostalCodeRepository;
import java.lang.reflect.Proxy;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
class TestApplicationConfig {

    @Bean
    @Primary
    PostalCodeRepository postalCodeRepository() {
        PostalCode postalCode = new PostalCode("2100", "Kobenhavn O");

        return (PostalCodeRepository) Proxy.newProxyInstance(
                PostalCodeRepository.class.getClassLoader(),
                new Class<?>[]{PostalCodeRepository.class},
                (proxy, method, args) -> {
                    if ("findRandomPostalCode".equals(method.getName())) {
                        return Optional.of(postalCode);
                    }
                    if ("hashCode".equals(method.getName())) {
                        return System.identityHashCode(proxy);
                    }
                    if ("equals".equals(method.getName())) {
                        return proxy == args[0];
                    }
                    if ("toString".equals(method.getName())) {
                        return "TestPostalCodeRepository";
                    }
                    throw new UnsupportedOperationException(
                            "Method not supported in test profile: " + method.getName()
                    );
                }
        );
    }
}
