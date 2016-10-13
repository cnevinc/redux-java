package com.android;

import android.content.Context;

import com.redux.ReduxModule;

import dagger.Module;
import dagger.Provides;

@Module(
        includes = ReduxModule.class,

        injects = {
                TodoActivity.class,
                MyAdapter.class
        }
)
public class ApplicationModule {

    private final Context application;

    public ApplicationModule(Context application) {
        this.application = application;
    }

    @Provides Context provideApplicationContext() {
        return application;
    }
}
