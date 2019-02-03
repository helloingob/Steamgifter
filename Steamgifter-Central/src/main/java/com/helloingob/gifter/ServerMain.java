package com.helloingob.gifter;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.helloingob.gifter.ErrorLogHandler;
import com.helloingob.gifter.dao.UserDAO;
import com.helloingob.gifter.data.to.AdvUserTO;
import com.helloingob.gifter.enums.Status;
import com.helloingob.gifter.handler.UserHandler;
import com.helloingob.gifter.to.UserTO;
import com.helloingob.gifter.utilities.CentralSettings;
import com.helloingob.gifter.utilities.PollWatchDog;
import com.helloingob.gifter.utilities.SharedSettings;

public class ServerMain {
    private static final Logger logger = LogManager.getLogger(SharedSettings.Logger.DEFAULT);
    private static ExecutorService threadPool;
    private static CompletionService<Status> returnPool;
    private static int returnPoolSize = 0;

    public static void main(String[] args) {
        Gson gson = new Gson();
        String userJson;
        threadPool = Executors.newFixedThreadPool(CentralSettings.Threads.MAX_THREADS);
        returnPool = new ExecutorCompletionService<Status>(threadPool);

        for (UserTO user : new UserDAO().get()) {
            if (user.getIsActive()) {
                userJson = gson.toJson(user);
                returnPool.submit(new UserHandler(gson.fromJson(userJson, AdvUserTO.class)));
                returnPoolSize++;
            }
        }

        try {
            for (int i = 0; i < returnPoolSize; i++) {
                @SuppressWarnings("unused")
                Status status = returnPool.take().get();
            }
        } catch (Exception e) {
            new ErrorLogHandler().error(e, logger);
        }

        PollWatchDog.checkLastPolls();
        threadPool.shutdown();
    }
}