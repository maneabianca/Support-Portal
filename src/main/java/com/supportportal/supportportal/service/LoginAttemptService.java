package com.supportportal.supportportal.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class LoginAttemptService {

    /**
     *        CACHE:
     *  USER(key)       ATTEMPTS(value)
     *  user1         2
     *  user2         1
     */

    private static final int MAXIMUM_NUMBER_OF_ATTEMPTS = 5;
    private static final int ATTEMPT_INCREMENT = 1;

    private LoadingCache<String, Integer> loginAttemptCache;

    public LoginAttemptService() {
        super();
        loginAttemptCache = CacheBuilder.newBuilder()
                .expireAfterWrite(15, TimeUnit.MINUTES)
                .maximumSize(100)
                .build(new CacheLoader<String, Integer>() { // CacheLoader is used to compute the value stored in the cache
            @Override
            public Integer load(String key) throws Exception {
                return 0;
            }
        });
    }

    /**
     * Removes the entry from the cache
     * @param username the key from the entry
     */
    public void evictUserFromLoginAttemptCache(String username){
        loginAttemptCache.invalidate(username);
    }

    /**
     * Adds the entry (username and number of attempts) to the cache
     * @param username the key from the entry
     * @throws ExecutionException
     */
    public void addUserToLoginAttemptCache(String username) {
        int attempts = 0;
        try {
            attempts = ATTEMPT_INCREMENT + loginAttemptCache.get(username);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        loginAttemptCache.put(username, attempts);
    }

    /**
     * Checks if the number of attempts exceeds the maximum
     * @param username the key from the entry
     * @return
     * @throws ExecutionException
     */
    public boolean hasExceededMaxAttempts(String username) {
        try {
            return loginAttemptCache.get(username) >= MAXIMUM_NUMBER_OF_ATTEMPTS;
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }
}
