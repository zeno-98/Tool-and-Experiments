package learn.classificationtree;

import learn.frame.Answer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetAnswer implements Answer {
    private List<Boolean> resetList;
    private boolean accept;
    private boolean hasInvalid;

    public ResetAnswer(List<Boolean> resetList, boolean accept) {
        this.resetList = resetList;
        this.accept = accept;
        if (resetList.size() >= 1 && resetList.get(resetList.size() - 1) == null) {
            hasInvalid = true;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResetAnswer)) return false;

        ResetAnswer that = (ResetAnswer) o;

        if (isAccept() != that.isAccept()) return false;
        if (resetList.size() != that.resetList.size()) return false;
        for (int i = 0; i < resetList.size(); i++) {
            if (resetList.get(i) != that.resetList.get(i)) return false;
        }
        return true;
    }

    /**
     * 判断两个ResetAnswer的包含关系，
     */
    public boolean include(ResetAnswer o) {
        if (!o.hasInvalid) {
            return this.equals(o);
        }
        for (int i = 0; i < resetList.size(); i++) {
            // nrnr include nr#
            if (i >= o.resetList.size()) {
                return true;
            }

            // nrnr include nrn#
            if (o.resetList.get(i) == null) {
                return true;
            }

            if (resetList.get(i) != o.resetList.get(i)) {
                return false;
            }
        }
        //  +  nr#
        return resetList.size() == 0 && o.resetList.size() == 1;

    }

    @Override
    public int hashCode() {
        int result = getResetList().hashCode();
        if (!hasInvalid) {
            result = 31 * result + (isAccept() ? 1 : 0);
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        getResetList().forEach(e -> {
            if (e == null) {
                sb.append("# ");
            } else {
                sb.append(e ? "r" : "n");
            }
        });
        if (!hasInvalid) {
            sb.append(isAccept() ? "+" : "-");
        }
        return sb.toString();
    }

    public int size(){
        return resetList.size()+1;
    }
}
