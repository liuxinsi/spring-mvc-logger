import com.lxs.sml.filter.LoggingFilter;
import com.lxs.sml.filter.LoggingFormat;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author akalxs@gmail.com
 */
@Controller
@EnableAutoConfiguration
public class Test {
    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    String test(@RequestParam(name = "a") String a) {
        return "test:" + a;
    }

    @RequestMapping(value = "/test2", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    Map<String, String> test2(@RequestBody User u) {
        Map<String, String> m = new HashMap<>();
        m.put("code", "123456");
        m.put("name", u.getName());
        return m;
    }

    @Bean
    public Filter filter() {
        return new com.lxs.sml.filter.LoggingFilter();
    }

    public static void main(String[] args) {
        SpringApplication.run(Test.class, args);
    }


}

class User {
    public String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
