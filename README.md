# Tracking Spring
## Getting started
1. Import the newest version of tracking-blossom in your maven Spring project (pom.xml):
````xml
 <properties>
    ...
    <tracking-blossom.version>0.2.7</tracking-blossom.version>
</properties>
    
<dependencies>
    ...
    <dependency>
        <groupId>io.github.h0n0riuss</groupId>
        <artifactId>tracking-blossom</artifactId>
        <version>${tracking-blossom.version}</version>
    </dependency>
</dependencies>
````

2. Create configuration and scan this basePackages to activate aspect (example):

````java
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "io.github.honoriuss.blossom")
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
- if no ITrackingObjectMapper bean/component/service is implemented, all parameters are mapped as JsonString.
- if no "parameterNames" are written, "defaultColumnName" + 0,1,... is used

## Optional (for customization):
1. Implement own ITrackingHandler<T> (example):

````java
import io.github.honoriuss.blossom.interfaces.ITrackingHandler;
import org.springframework.stereotype.Service;

@Service
public class OwnTrackingHandler implements ITrackingHandler<String> {

    @Override
    public void handleTracking(String message) {
        System.out.println(message);
    }
}
````

2. Implement own ITrackingObjectMapper<T> (example):  
Should match own ITrackingHandler<T>

````java
import io.github.honoriuss.blossom.interfaces.ITrackingObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class OwnTrackingMapper implements ITrackingObjectMapper<String> {

    @Override
    public String mapParameters(Object[] args, String[] parameterNames) {
        //implement own parameter mapping
    }

    @Override
    public String mapResult(Object obj) {
        //own mapping
    }
}
````

````java
import io.github.honoriuss.blossom.interfaces.ITrackingParameterProvider;
import org.springframework.stereotype.Service;

@Service
public class OwnTrackingProvider implements ITrackingParameterProvider {

    @Override
    public void addBaseParameters(HashMap<String, Object> parameterHashMap) {
        //parameterHashMap.put("key", "value");
    }
}
````

````java
import io.github.honoriuss.blossom.interfaces.ITrackingFilter;
import org.springframework.stereotype.Service;

@Service
public class OwnTrackingFilter implements ITrackingFilter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //add your filter logic here --> its Spring context
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
````
