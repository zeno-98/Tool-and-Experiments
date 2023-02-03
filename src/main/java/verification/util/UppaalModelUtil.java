package verification.util;


import verification.uppaal.model.UppaalLocation;
import verification.uppaal.model.builder.UppaalLocationBuilder;

public class UppaalModelUtil {

    public static UppaalLocation buildUppaalLocation(String modelName, String locationName){
        return new UppaalLocationBuilder().setName(locationName)
                .setId(modelName+locationName)
                .createLocation();
    }

    public static UppaalLocation buildUppaalLocation(String modelName, String locationName, String text){
        return new UppaalLocationBuilder().setName(locationName)
                .setId(modelName+locationName)
                .setInvariantLabel(text)
                .createLocation();
    }

    public static UppaalLocation buildCommittedUppaalLocation(String modelName, String locationName) {
        return new UppaalLocationBuilder().setName(locationName)
                .setCommitted(true)
                .setId(modelName + locationName)
                .createLocation();
    }

    public static UppaalLocation buildCommittedUppaalLocation(String modelName, String locationName, String text) {
        return new UppaalLocationBuilder().setName(locationName)
                .setCommitted(true)
                .setId(modelName + locationName)
                .setInvariantLabel(text)
                .createLocation();
    }

    public static UppaalLocation buildUrgentUppaalLocation(String modelName, String locationName) {
        return new UppaalLocationBuilder().setName(locationName)
                .setUrgent(true)
                .setId(modelName + locationName)
                .createLocation();
    }
}


















