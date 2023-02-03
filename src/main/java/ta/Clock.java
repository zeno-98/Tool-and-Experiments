package ta;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class Clock {
    @Getter
    @Setter
    private String name;

    public Clock copy(){
        return new Clock(name);
    }
}
