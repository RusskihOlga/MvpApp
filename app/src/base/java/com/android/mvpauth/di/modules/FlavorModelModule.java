package com.android.mvpauth.di.modules;

import com.android.mvpauth.utils.App;
import com.android.mvpauth.utils.ConstantManager;
import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;

import dagger.Module;
import dagger.Provides;

@Module
public class FlavorModelModule {
    @Provides
    JobManager provideJobManager() {
        Configuration configuration = new Configuration.Builder(App.getContext())
                .minConsumerCount(ConstantManager.MIN_CONSUMER_COUNT)
                .maxConsumerCount(ConstantManager.MAX_CONSUMER_COUNT)
                .loadFactor(ConstantManager.LOAD_FACTOR)
                .consumerKeepAlive(ConstantManager.KEEP_ALIVE)
                .build();

        return new JobManager(configuration);
    }
}
