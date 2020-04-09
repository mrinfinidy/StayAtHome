package com.example.stayathome.helper;

import org.threeten.bp.Duration;
import org.threeten.bp.Instant;

public class ChallengeHelper {

    private SharedPreferencesHelper prefHelper;

    public ChallengeHelper(SharedPreferencesHelper prefs){
        this.prefHelper = prefs;
    }

    public boolean hasChallengeEnded(){
        long duration = getTimeInChallenge();
        return duration >= prefHelper.retrieveLong("challenge_duration");
    }

    public long getTimeInChallenge(){
        long challengeStartTime = prefHelper.retrieveLong("challenge_start_time");
        Instant challengeStartInstant = Instant.ofEpochSecond(challengeStartTime);
        return Duration.between(challengeStartInstant, Instant.now()).getSeconds();
    }
} // End class ChallengeHelper
