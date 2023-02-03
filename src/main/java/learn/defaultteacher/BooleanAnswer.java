package learn.defaultteacher;

import learn.frame.Answer;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BooleanAnswer implements Answer {
    private boolean accept;
    private boolean invalid;

    public boolean include(BooleanAnswer o) {
        if (o.invalid) return true;

        return !invalid && accept == o.isAccept();

    }
}
