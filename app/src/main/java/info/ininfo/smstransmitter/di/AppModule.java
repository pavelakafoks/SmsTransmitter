package info.ininfo.smstransmitter.di;

import android.app.Application;

public class AppModule {

    private Application application;

    public AppModule(Application application) {
        this.application = application;
    }

}
