package verification.uppaal.model;


import java.util.LinkedHashMap;
import java.util.Map;

public class Declaration {
    private Map<String,String> map;

    public Map<String, String> getMap() {
        if (map != null){
            return map;
        }
        map = new LinkedHashMap<>();
        return map;
        
    }

    public boolean isMapInitialized() {
        return this.map != null;
    }
    
    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public void put(String key, String value){
        if (map == null){
            map = new LinkedHashMap<>();
        }
        map.put(key, value);
    }


    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String,String> entry : map.entrySet()){
            if(entry.getKey().endsWith("}")){
                sb.append(entry.getValue()).append(" ").append(entry.getKey()).append("\n");
            }else {
                sb.append(entry.getValue()).append(" ").append(entry.getKey()).append(";\n");
            }
        }
        return sb.toString();
    }
}
