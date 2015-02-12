How to use:

First build the project and reference the jar (or project) in your Android project.
The easiest way to build is to use gradle, just run ``./gradlew assemble``
Then in your Android project create the layout for the scan-view which can be as easy as:

```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="fill_parent"
    android:layout_height="fill_parent"
    android:orientation="vertical"
    >

    <FrameLayout
        android:id="@+id/cameraview"
        android:layout_width="fill_parent"
        android:layout_height="fill_parent">

    </FrameLayout>
</FrameLayout>
```

In your AndroidManifest.xml you will need to allow camera-use:

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-feature android:name="android.hardware.camera" />
<uses-feature android:name="android.hardware.camera.autofocus" />
```

Then in your scan-activity you can use it as simply as:

```java
public class ScanActivity extends Activity
{
	private SimpleCameraReader camera;

	private Context context;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.scan_layout);

		try
		{
			context = this;

			// We create a collection of formats we want to use
			Collection<BarcodeFormat> formats = new ArrayList<BarcodeFormat>();

			// Which in this case is EAN codes (European Product codes)
			formats.add(BarcodeFormat.EAN_13);
			formats.add(BarcodeFormat.EAN_8);

			// Initialize camera object with context and formats
			camera = new SimpleCameraReader(this, formats);

			// Here we add the CameraView to our FrameLayout
			((FrameLayout) findViewById(R.id.cameraview)).addView(camera
					.getCameraView());

			// Make the view transparent so widgets/views in front of our cameraview
			// shows
			camera.getCameraView().setBackgroundColor(Color.TRANSPARENT);

			// We add a ResultHandler which will take care of an eventual
			// result or errors
			camera.setResultHandler(new CameraHandler()
			{

				// When decoding completes and we found a barcode
				// this method is called with a Result-object
				public void onDecodeCompleted(Result result)
				{
					Toast.makeText(context, "Found barcode: " + result.getText().toString(), Toast.LENGTH_LONG).show();
				}

				// In case of an error, this method is called
				// We don't really have to do anything here
				public void onError(Exception ex)
				{

				}
			});
		}
		catch (Exception ex)
		{

		}
	}

	public void onResume()
	{
		super.onResume();

		// When the activity resumes/starts
		// we'll start the camera
		try
		{
			camera.start();
		}
		catch (Exception ex)
		{

		}
	}

	public void onPause()
	{
		super.onPause();

		// When the activity pauses/stops
		// stop the camera
		camera.stop();
	}
}
```

To use it as a Gradle-dependency, just add
```groovy
compile('com.github.henningms:simplecamerareader-android:1.0@aar') {
    transitive = true
}
```

To your build.gradle

To use it as a maven dependency, just add
```xml
<dependency>
	<groupId>com.github.henningms</groupId>
	<artifactId>simplecamerareader-android</artifactId>
	<version>1.0</version>
	<type>aar</type>
</dependency>
```

To your pom.

--------------------------------------------------------

Copyright [2012, 2013] [Henning Mosand Stephansen]

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
