package com.android.mvpauth.mvp.models;

import com.android.mvpauth.data.managers.DataManager;
import com.android.mvpauth.di.DaggerService;
import com.android.mvpauth.di.components.DaggerModelComponent;
import com.android.mvpauth.di.components.ModelComponent;
import com.android.mvpauth.di.modules.ModelModule;
import com.birbit.android.jobqueue.JobManager;

import javax.inject.Inject;

public abstract class AbstractModel {

    @Inject
    DataManager mDataManager;
    @Inject
    JobManager mJobManager;

    public AbstractModel() {

        ModelComponent component = DaggerService.getComponent(ModelComponent.class);
        if (component == null){
            component = createDaggerComponent();
            DaggerService.registerComponent(ModelComponent.class, component);
        }
        component.inject(this);
    }

    private ModelComponent createDaggerComponent() {
        return DaggerModelComponent.builder()
                .modelModule(new ModelModule())
                .build();
    }
}
