package io.github.honoriuss.blossom;

import io.github.honoriuss.blossom.interfaces.ITrackingObjectMapper;
import io.github.honoriuss.blossom.interfaces.ITrackingParameterRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = {TrackingObjectMapperImpl.class})
class TrackingObjectMapperImplTest {
    private final String defaultColName = "colName";

    @Autowired
    private ITrackingObjectMapper<String> trackingObjectMapperImpl;
    @MockBean
    private ITrackingParameterRegistry trackingParameterRegistry;

    private static class TestClass {
        public String test;
    }

    @Test
    public void createMessageStringTest(){
        var expected = "{\"test\":\"test\"}";
        var args = new Object[]{"test"};
        var parameterNames = new String[]{"test"};
        var res = trackingObjectMapperImpl.mapParameters(args, parameterNames);

        Assertions.assertNotNull(res);
        Assertions.assertEquals(expected.length(), res.length());
        Assertions.assertEquals(expected, res);
    }

    @Test
    public void createMessageStringTest2(){
        var expected = "{\"colName\":\"test2\",\"test\":\"test\"}";
        var args = new Object[]{"test", "test2"};
        var parameterNames = new String[]{"test", defaultColName};
        var res = trackingObjectMapperImpl.mapParameters(args, parameterNames);

        Assertions.assertNotNull(res);
        Assertions.assertEquals(expected.length(), res.length());
        Assertions.assertEquals(expected, res);
    }

    @Test
    public void createMessageStringTest3(){
        var expected = "{\"colName\":\"test2\",\"test\":\"test\",\"colName0\":\"test0\"}";
        var args = new Object[]{"test", "test2", "test0"};
        var parameterNames = new String[]{"test", defaultColName, defaultColName + 0};
        var res = trackingObjectMapperImpl.mapParameters(args, parameterNames);

        Assertions.assertNotNull(res);
        Assertions.assertEquals(expected.length(), res.length());
        Assertions.assertEquals(expected, res);
    }

    @Test
    public void createMessageStringTest4(){
        var expected = new TestClass();
        expected.test = "gurr";
        var res = trackingObjectMapperImpl.mapResult(expected);

        Assertions.assertNotNull(res);
        Assertions.assertEquals("{\"test\":\"gurr\"}", res);
    }
}
