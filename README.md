## How to integrate into your app?
Step 1. To clone this project, open your terminal or cmd: <br>
cd folder/to/clone-into/ <br>
git clone https://github.com/PAChain/Android.git

Step 2. Add the dependency: <br>
Open project build.gradle, add:
```Java
ext.kotlin_version = '1.4.0-rc'
```

```Java
dependencies {
	classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
	classpath 'com.github.dcendents:android-maven-gradle-plugin:1.5'
}
```

Step 3. Import the library to your project; <br>

Step 4. Modify "Server api url": <br>
In the path: /app/src/main/java/com/pachain/android/config/Config.java <br>

## How to use the library?
```Java
Intent intent = new Intent(this, PAChainRegisterActivity.class);
startActivity(intent);
```

## License
Copyright 2020 PAChain

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
```
http://www.apache.org/licenses/LICENSE-2.0
```
Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
