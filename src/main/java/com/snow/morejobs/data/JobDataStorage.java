package com.snow.morejobs.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.snow.morejobs.jobs.JobType;
import net.minecraft.entity.player.ServerPlayerEntity;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.*;

public class JobDataStorage {

    private static final File DATA_FOLDER = new File("data/morejobs/");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<UUID, JobDataStorage> CACHE = new HashMap<>();

    private UUID uuid;
    private final Map<String, Integer> xpMap = new HashMap<>();
    private final Set<String> unlockedJobs = new HashSet<>();
    private final Set<String> activeJobs = new HashSet<>();

    private JobDataStorage(UUID uuid) {
        this.uuid = uuid;
    }

    public static JobDataStorage get(ServerPlayerEntity player) {
        return get(player.getUUID());
    }

    public static JobDataStorage get(UUID uuid) {
        return CACHE.computeIfAbsent(uuid, JobDataStorage::load);
    }

    private static JobDataStorage load(UUID uuid) {
        try {
            if (!DATA_FOLDER.exists()) DATA_FOLDER.mkdirs();
            File file = new File(DATA_FOLDER, uuid + ".json");
            if (!file.exists()) return new JobDataStorage(uuid);

            try (FileReader reader = new FileReader(file)) {
                Type type = new TypeToken<JobDataStorage>() {}.getType();
                JobDataStorage data = GSON.fromJson(reader, type);
                data.uuidFix(uuid);
                return data;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new JobDataStorage(uuid);
        }
    }

    public void save() {
        try {
            File file = new File(DATA_FOLDER, uuid + ".json");
            try (FileWriter writer = new FileWriter(file)) {
                GSON.toJson(this, writer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uuidFix(UUID fixed) {
        this.uuid = fixed;
    }

    // === MÉTHODES PUBLIQUES SIMPLIFIÉES ===

    public void addXp(JobType job, int amount) {
        String jobName = job.getName();
        int current = xpMap.getOrDefault(jobName, 0);
        xpMap.put(jobName, current + amount);
    }

    public int getXp(JobType job) {
        return xpMap.getOrDefault(job.getName(), 0);
    }

    public void unlockJob(JobType job) {
        unlockedJobs.add(job.getName());
    }

    public boolean isUnlocked(JobType job) {
        return !job.isSecret() || unlockedJobs.contains(job.getName());
    }

    public void setActive(JobType job) {
        if (job.isSecret()) {
            activeJobs.add(job.getName());
        } else {
            activeJobs.removeIf(name -> !JobType.fromName(name).isSecret());
            activeJobs.add(job.getName());
        }
    }

    public boolean canAdd(JobType job) {
        if (job.isSecret()) return true;
        return activeJobs.stream().noneMatch(name -> !JobType.fromName(name).isSecret());
    }

    public void removeActive(JobType job) {
        activeJobs.remove(job.getName());
    }

    public boolean hasActive(JobType job) {
        return activeJobs.contains(job.getName());
    }

    public Set<String> getUnlockedJobs() {
        return unlockedJobs;
    }

    public Set<String> getActiveJobs() {
        return activeJobs;
    }

    public UUID getUuid() {
        return uuid;
    }


    public boolean hasJob(JobType job) {
        return unlockedJobs.contains(job.getName()) || activeJobs.contains(job.getName());
    }

    public void addJob(JobType job) {
        unlockJob(job);
        setActive(job);
        save();
    }
}
