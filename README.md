# Tracking Spring
## Getting started
1. Import the newest version of Tracking-Spring in your maven Spring project (pom.xml):
````xml
 <properties>
    ...
    <tracking-spring.version>0.0.1</tracking-spring.version>
</properties>
    
<dependencies>
    ...
    <dependency>
        <groupId>io.github.h0n0riuss</groupId>
        <artifactId>Tracking-Spring</artifactId>
        <version>${tracking-spring.version}</version>
    </dependency>
</dependencies>
````

2. Create configuration and scan this basePackages to activate aspect (example):
````java
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "io.github.honoriuss.tracking")
public class TrackingConfig {
}
````

3. Use @annotation to track parameters:
````java
@PostMapping("login")
@TrackParameters(parameterNames = {"user"})
public String login(@RequestBody UserSignUpDto user) {
    return homeService.login(user.username(), user.password());
}
````

## Notes
- if no ITrackingHandler bean/component/service is implemented, all parameters are written on console.
- if no "parameterNames" are written, "colName" + 0,1,... is used

## Optional:
1. Implement own ITrackingHandler (example):
````java
import io.github.honoriuss.tracking.interfaces.ITrackingHandler;
import org.springframework.stereotype.Service;

@Service
public class OwnTracking implements ITrackingHandler {

    @Override
    public void handleTracking(String message) {
        System.out.println(message);
    }
}
````
