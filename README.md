# android-java-sample-app

Replace clientId and baseURL

``` java
package api.login.java.sample;

import android.app.Application;

import login.api.LoginApi;

public class ApplicationData extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //Create clientId and baseURL from developer console under Native Integration
        final String clientId = "<enter client api key here>";
        final String baseURL = "<enter base url here>";
        LoginApi.client().configure(this,clientId,baseURL);

    }
}
```
