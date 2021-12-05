package ru.maslin.springapp.entity.local;

import lombok.*;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import ru.maslin.springapp.entity.Answer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class LocalQuestion {

    private Map<Long, Answer> clientMapAnswers = new HashMap();
    private ArrayList<Long> clientListAnswers = new ArrayList<>();

}
