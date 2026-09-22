package com.github.alexthe666.iceandfire.client.model.util;

import com.github.alexthe666.citadel.client.model.TabulaModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

import java.util.HashMap;


/**
 * A library containing all animations for all dragons. Contains methods for registering and retrieving models
 */
public class DragonAnimationsLibrary {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final HashMap<String, TabulaModel<?>> models = new HashMap<>();
    private static String toKey(IEnumDragonPoses p, IEnumDragonModelTypes m) {
        return p.getPose() + m.getModelType();
    }

    public static TabulaModel<?> getModel(IEnumDragonPoses pose, IEnumDragonModelTypes modelType) {
        TabulaModel<?> result = models.get(toKey(pose, modelType));
        if(result == null)
            LOGGER.error("No model defined for " + pose.getPose() + modelType.getModelType() + " have you registered your animations?");
        return result;
    }

    /**
     * Loads a single model and registers it into the library, uses the default modID of IceandFire. <br/>
     * Use {@link #registerSingle(IEnumDragonPoses, IEnumDragonModelTypes, String)} if you are an addon author.
     * @param pose
     * @param modelType
     * @see #register(IEnumDragonPoses[], IEnumDragonModelTypes[])
     */
    public static void registerSingle(IEnumDragonPoses pose, IEnumDragonModelTypes modelType) {
        registerSingle(pose, modelType, "iceandfire");
    }

    /**
     * Loads a set of models and registers it into the library, uses the default modID of IceandFire. <br/>
     * Use {@link #register(IEnumDragonPoses[], IEnumDragonModelTypes[], String)} if you are an addon author.
     * @param poses
     * @param modelTypes
     * @see #registerSingle(IEnumDragonPoses, IEnumDragonModelTypes)
     */
    public static void register(IEnumDragonPoses[] poses, IEnumDragonModelTypes[] modelTypes) {
        register(poses, modelTypes, "iceandfire");
    }

    /**
     * Loads a set of models and registers it into the library. Tabula models are loaded from the filesystem, as such: <br/>
     * /assets/<b>[modID]</b>/models/tabula/<b>[modelType]</b>dragon/<b>[modeltype]</b>dragon_<b>[pose]</b>.tbl
     * @param poses
     * @param modelTypes
     * @param modID
     * @see #registerSingle(IEnumDragonPoses, IEnumDragonModelTypes, String)
     */
    public static void register(IEnumDragonPoses[] poses, IEnumDragonModelTypes[] modelTypes, String modID) {
        for(IEnumDragonPoses p : poses)
            for(IEnumDragonModelTypes m : modelTypes)
                registerSingle(p, m, modID);
        // Second pass: SWIM_POSE is registered before SWIM4, so missing fire
        // files cannot alias until every present pose is loaded.
        for(IEnumDragonPoses p : poses)
            for(IEnumDragonModelTypes m : modelTypes)
                aliasMissingPose(p, m);
    }

    /**
     * Loads a single model and registers it into the library.  Tabula models are loaded from the filesystem, as such: <br/>
     * /assets/<b>[modID]</b>/models/tabula/<b>[modelType]</b>dragon/<b>[modeltype]</b>dragon_<b>[pose]</b>.tbl
     * @param pose
     * @param modelType
     * @param modID
     * @see #register(IEnumDragonPoses[], IEnumDragonModelTypes[], String)
     */
    public static void registerSingle(IEnumDragonPoses pose, IEnumDragonModelTypes modelType, String modID) {
        //Load model
        TabulaModel<?> result;
        String location = "/assets/" + modID + "/models/tabula/" + modelType.getModelType() + "dragon/" + modelType.getModelType() + "dragon_" + pose.getPose() + ".tbl";
        try{
            result = new TabulaModel<>(TabulaModelHandlerHelper.loadTabulaModel(location));
        }
        catch(IOException | NullPointerException e) {
            if (!isExpectedMissingFireSwim(pose, modelType)) {
                LOGGER.warn("Could not load " + location + ": " + e.getMessage());
            }
            return;
        }

        //put model
        models.put(toKey(pose, modelType), result);
    }

    /**
     * Fire dragons never shipped a {@code Swimming} tabula file. 26.1 also
     * lowercases pack paths, so the missing file logs as
     * {@code firedragon_swimming.tbl}. Reuse Swim4 rather than leaving a null
     * slot. Swim5 is a real pose; a missing file still falls back to Swim4.
     */
    private static boolean isExpectedMissingFireSwim(IEnumDragonPoses pose, IEnumDragonModelTypes modelType) {
        return "fire".equals(modelType.getModelType())
            && pose instanceof EnumDragonPoses dragonPose
            && dragonPose == EnumDragonPoses.SWIM_POSE;
    }

    private static void aliasMissingPose(IEnumDragonPoses pose, IEnumDragonModelTypes modelType) {
        if (models.containsKey(toKey(pose, modelType)) || !(pose instanceof EnumDragonPoses dragonPose)) {
            return;
        }
        EnumDragonPoses fallback = switch (dragonPose) {
            case SWIM_POSE, SWIM5 -> EnumDragonPoses.SWIM4;
            default -> null;
        };
        if (fallback == null) {
            return;
        }
        TabulaModel<?> source = models.get(toKey(fallback, modelType));
        if (source == null) {
            return;
        }
        models.put(toKey(pose, modelType), source);
    }

    /**
     * Creates or replaces a set of entries in the library with a set references to other entries allowing for shared poses and reduced memory usage.
     * @param poses
     * @param modelSource
     * @param modelDestinations
     * @see #registerReference(IEnumDragonPoses, IEnumDragonModelTypes, IEnumDragonModelTypes)
     */
    public static void registerReferences(IEnumDragonPoses[] poses, IEnumDragonModelTypes modelSource, IEnumDragonModelTypes[] modelDestinations) {
        for(int i = 0; i < poses.length; i++) {
            registerReference(poses[i], modelSource, modelDestinations[i]);
        }
    }

    /**
     * Creates or replaces an entry in the library with a reference to another entry allowing for shared poses and reduced memory usage.
     * @param pose
     * @param modelSource
     * @param modelDestination
     * @see #registerReferences(IEnumDragonPoses[], IEnumDragonModelTypes, IEnumDragonModelTypes[])
     */
    public static void registerReference(IEnumDragonPoses pose, IEnumDragonModelTypes modelSource, IEnumDragonModelTypes modelDestination) {
        TabulaModel<?> source = getModel(pose, modelSource);
        String     destKey =   toKey(pose, modelDestination);

        if(source == null)
            return;

        if(models.containsKey(destKey))
            LOGGER.info(
                    "Overriding existing model '" + destKey +
                    "' with reference to '"       + toKey(pose, modelSource));

        models.put(destKey, source);
    }

}
