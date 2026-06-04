# AlignAppiumFramework Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a dual-platform (Android + iOS) Appium automation framework in Java + TestNG with full parallel execution, Allure + ExtentReports dual reporting, video recording, and 16 utility classes.

**Architecture:** Maven multi-module project (`core` → `android`/`ios` → `tests`). `core` owns driver lifecycle, config, all utilities, and listeners. Platform modules own capabilities and page object implementations. `PageFactory` in `tests` routes to correct implementation. `ThreadLocal<AppiumDriver>` in `DriverManager` enables thread-safe parallel execution.

**Tech Stack:** Java 17, Appium java-client 9.2.3, TestNG 7.9.0, Allure 2.27.0, ExtentReports 5.1.1, SLF4J 2.0.13 + Log4j2 2.23.1, Lombok 1.18.32, Mockito 5.11.0

---

## File Map

```
AlignAppiumFramework/
├── pom.xml
├── core/
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/align/
│       │   ├── driver/        DriverManager.java, DriverFactory.java, ServerManager.java
│       │   ├── config/        ConfigLoader.java, DeviceConfig.java
│       │   ├── exceptions/    DriverInitException.java, PageException.java, AppNotFoundException.java
│       │   ├── pages/         BasePage.java
│       │   ├── listeners/     TestListener.java, DriverListener.java
│       │   ├── reporting/     ExtentManager.java
│       │   └── utils/         AppResolver.java, WaitUtils.java, GestureUtils.java,
│       │                      KeyboardUtils.java, AlertUtils.java, PermissionUtils.java,
│       │                      AppUtils.java, DeviceUtils.java, NetworkUtils.java,
│       │                      ClipboardUtils.java, FileUtils.java, BiometricUtils.java,
│       │                      DeepLinkUtils.java, ContextUtils.java, ElementUtils.java,
│       │                      NotificationUtils.java, ScreenshotUtils.java, VideoUtils.java
│       └── test/java/com/align/
│           ├── driver/        DriverManagerTest.java
│           ├── config/        ConfigLoaderTest.java
│           └── utils/         AppResolverTest.java
├── android/
│   ├── pom.xml
│   └── src/main/java/com/align/android/
│       ├── driver/    AndroidDriverFactory.java
│       ├── capabilities/ AndroidCapabilities.java
│       └── pages/     AndroidLoginPage.java
├── ios/
│   ├── pom.xml
│   └── src/main/java/com/align/ios/
│       ├── driver/    IOSDriverFactory.java
│       ├── capabilities/ IOSCapabilities.java
│       └── pages/     IOSLoginPage.java
└── tests/
    ├── pom.xml
    └── src/
        ├── main/java/com/align/
        │   ├── factory/   PageFactory.java
        │   └── pages/     LoginPage.java (interface)
        └── test/
            ├── java/com/align/tests/
            │   ├── BaseTest.java
            │   └── LoginTest.java
            └── resources/
                ├── config/    config.properties, android-local.properties,
                │              ios-local.properties, cloud.properties
                └── suites/    android-suite.xml, ios-suite.xml, parallel-suite.xml
```

---

## Task 1: Maven Project Scaffold

**Files:**
- Create: `pom.xml` (parent)
- Create: `core/pom.xml`
- Create: `android/pom.xml`
- Create: `ios/pom.xml`
- Create: `tests/pom.xml`

- [ ] **Step 1: Create parent POM**

```xml
<!-- pom.xml -->
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.align</groupId>
    <artifactId>align-appium-framework</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>pom</packaging>
    <name>AlignAppiumFramework</name>

    <modules>
        <module>core</module>
        <module>android</module>
        <module>ios</module>
        <module>tests</module>
    </modules>

    <properties>
        <java.version>17</java.version>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <appium.version>9.2.3</appium.version>
        <testng.version>7.9.0</testng.version>
        <allure.version>2.27.0</allure.version>
        <extentreports.version>5.1.1</extentreports.version>
        <slf4j.version>2.0.13</slf4j.version>
        <log4j.version>2.23.1</log4j.version>
        <lombok.version>1.18.32</lombok.version>
        <mockito.version>5.11.0</mockito.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>io.appium</groupId>
                <artifactId>java-client</artifactId>
                <version>${appium.version}</version>
            </dependency>
            <dependency>
                <groupId>org.testng</groupId>
                <artifactId>testng</artifactId>
                <version>${testng.version}</version>
            </dependency>
            <dependency>
                <groupId>io.qameta.allure</groupId>
                <artifactId>allure-testng</artifactId>
                <version>${allure.version}</version>
            </dependency>
            <dependency>
                <groupId>com.aventstack</groupId>
                <artifactId>extentreports</artifactId>
                <version>${extentreports.version}</version>
            </dependency>
            <dependency>
                <groupId>org.slf4j</groupId>
                <artifactId>slf4j-api</artifactId>
                <version>${slf4j.version}</version>
            </dependency>
            <dependency>
                <groupId>org.apache.logging.log4j</groupId>
                <artifactId>log4j-slf4j2-impl</artifactId>
                <version>${log4j.version}</version>
            </dependency>
            <dependency>
                <groupId>org.apache.logging.log4j</groupId>
                <artifactId>log4j-core</artifactId>
                <version>${log4j.version}</version>
            </dependency>
            <dependency>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <version>${lombok.version}</version>
                <scope>provided</scope>
            </dependency>
            <dependency>
                <groupId>org.mockito</groupId>
                <artifactId>mockito-core</artifactId>
                <version>${mockito.version}</version>
                <scope>test</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <pluginManagement>
            <plugins>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-compiler-plugin</artifactId>
                    <version>3.13.0</version>
                    <configuration>
                        <source>17</source>
                        <target>17</target>
                        <annotationProcessorPaths>
                            <path>
                                <groupId>org.projectlombok</groupId>
                                <artifactId>lombok</artifactId>
                                <version>${lombok.version}</version>
                            </path>
                        </annotationProcessorPaths>
                    </configuration>
                </plugin>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-surefire-plugin</artifactId>
                    <version>3.2.5</version>
                    <configuration>
                        <suiteXmlFiles>
                            <suiteXmlFile>${suite}</suiteXmlFile>
                        </suiteXmlFiles>
                        <systemPropertyVariables>
                            <platform>${platform}</platform>
                            <env>${env}</env>
                            <app.path>${app.path}</app.path>
                            <video.recording.enabled>${video.recording.enabled}</video.recording.enabled>
                        </systemPropertyVariables>
                        <argLine>-javaagent:${settings.localRepository}/org/aspectj/aspectjweaver/1.9.22/aspectjweaver-1.9.22.jar</argLine>
                    </configuration>
                    <dependencies>
                        <dependency>
                            <groupId>org.aspectj</groupId>
                            <artifactId>aspectjweaver</artifactId>
                            <version>1.9.22</version>
                        </dependency>
                    </dependencies>
                </plugin>
                <plugin>
                    <groupId>io.qameta.allure</groupId>
                    <artifactId>allure-maven</artifactId>
                    <version>2.12.0</version>
                    <configuration>
                        <reportVersion>${allure.version}</reportVersion>
                    </configuration>
                </plugin>
            </plugins>
        </pluginManagement>
    </build>
</project>
```

- [ ] **Step 2: Create core/pom.xml**

```xml
<!-- core/pom.xml -->
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.align</groupId>
        <artifactId>align-appium-framework</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>core</artifactId>
    <name>AlignAppiumFramework - Core</name>

    <dependencies>
        <dependency><groupId>io.appium</groupId><artifactId>java-client</artifactId></dependency>
        <dependency><groupId>org.testng</groupId><artifactId>testng</artifactId></dependency>
        <dependency><groupId>io.qameta.allure</groupId><artifactId>allure-testng</artifactId></dependency>
        <dependency><groupId>com.aventstack</groupId><artifactId>extentreports</artifactId></dependency>
        <dependency><groupId>org.slf4j</groupId><artifactId>slf4j-api</artifactId></dependency>
        <dependency><groupId>org.apache.logging.log4j</groupId><artifactId>log4j-slf4j2-impl</artifactId></dependency>
        <dependency><groupId>org.apache.logging.log4j</groupId><artifactId>log4j-core</artifactId></dependency>
        <dependency><groupId>org.projectlombok</groupId><artifactId>lombok</artifactId></dependency>
        <dependency><groupId>org.mockito</groupId><artifactId>mockito-core</artifactId></dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 3: Create android/pom.xml**

```xml
<!-- android/pom.xml -->
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.align</groupId>
        <artifactId>align-appium-framework</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>android</artifactId>
    <name>AlignAppiumFramework - Android</name>

    <dependencies>
        <dependency>
            <groupId>com.align</groupId>
            <artifactId>core</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>
</project>
```

- [ ] **Step 4: Create ios/pom.xml**

```xml
<!-- ios/pom.xml -->
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.align</groupId>
        <artifactId>align-appium-framework</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>ios</artifactId>
    <name>AlignAppiumFramework - iOS</name>

    <dependencies>
        <dependency>
            <groupId>com.align</groupId>
            <artifactId>core</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>
</project>
```

- [ ] **Step 5: Create tests/pom.xml**

```xml
<!-- tests/pom.xml -->
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.align</groupId>
        <artifactId>align-appium-framework</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>tests</artifactId>
    <name>AlignAppiumFramework - Tests</name>

    <properties>
        <platform>android</platform>
        <env>local</env>
        <app.path></app.path>
        <video.recording.enabled>false</video.recording.enabled>
        <suite>src/test/resources/suites/android-suite.xml</suite>
    </properties>

    <dependencies>
        <dependency>
            <groupId>com.align</groupId>
            <artifactId>android</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>com.align</groupId>
            <artifactId>ios</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
            </plugin>
            <plugin>
                <groupId>io.qameta.allure</groupId>
                <artifactId>allure-maven</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 6: Create directory structure**

```bash
mkdir -p core/src/main/java/com/align/{driver,config,exceptions,pages,listeners,reporting,utils}
mkdir -p core/src/main/resources
mkdir -p core/src/test/java/com/align/{driver,config,utils}
mkdir -p android/src/main/java/com/align/android/{driver,capabilities,pages}
mkdir -p ios/src/main/java/com/align/ios/{driver,capabilities,pages}
mkdir -p tests/src/main/java/com/align/{factory,pages}
mkdir -p tests/src/test/java/com/align/tests
mkdir -p tests/src/test/resources/{config,suites}
```

- [ ] **Step 7: Verify Maven compiles**

```bash
mvn compile -pl core
```
Expected: `BUILD SUCCESS`

- [ ] **Step 8: Commit**

```bash
git add pom.xml core/pom.xml android/pom.xml ios/pom.xml tests/pom.xml
git commit -m "build: set up Maven multi-module project scaffold"
```

---

## Task 2: Custom Exceptions

**Files:**
- Create: `core/src/main/java/com/align/exceptions/DriverInitException.java`
- Create: `core/src/main/java/com/align/exceptions/PageException.java`
- Create: `core/src/main/java/com/align/exceptions/AppNotFoundException.java`

- [ ] **Step 1: Create DriverInitException**

```java
// core/src/main/java/com/align/exceptions/DriverInitException.java
package com.align.exceptions;

public class DriverInitException extends RuntimeException {
    public DriverInitException(String message) {
        super(message);
    }

    public DriverInitException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

- [ ] **Step 2: Create PageException**

```java
// core/src/main/java/com/align/exceptions/PageException.java
package com.align.exceptions;

public class PageException extends RuntimeException {
    private final String pageName;
    private final String locator;

    public PageException(String pageName, String locator, String message) {
        super(String.format("[%s] Element '%s': %s", pageName, locator, message));
        this.pageName = pageName;
        this.locator = locator;
    }

    public PageException(String pageName, String locator, String message, Throwable cause) {
        super(String.format("[%s] Element '%s': %s", pageName, locator, message), cause);
        this.pageName = pageName;
        this.locator = locator;
    }

    public String getPageName() { return pageName; }
    public String getLocator() { return locator; }
}
```

- [ ] **Step 3: Create AppNotFoundException**

```java
// core/src/main/java/com/align/exceptions/AppNotFoundException.java
package com.align.exceptions;

public class AppNotFoundException extends RuntimeException {
    public AppNotFoundException(String appPath) {
        super("App binary not found at path: " + appPath + ". Verify app.path config or -Dapp.path flag.");
    }
}
```

- [ ] **Step 4: Compile**

```bash
mvn compile -pl core
```
Expected: `BUILD SUCCESS`

- [ ] **Step 5: Commit**

```bash
git add core/src/main/java/com/align/exceptions/
git commit -m "feat(core): add custom exceptions DriverInitException, PageException, AppNotFoundException"
```

---

## Task 3: ConfigLoader + Properties Files

**Files:**
- Create: `core/src/main/java/com/align/config/ConfigLoader.java`
- Create: `core/src/test/java/com/align/config/ConfigLoaderTest.java`
- Create: `tests/src/test/resources/config/config.properties`
- Create: `tests/src/test/resources/config/android-local.properties`
- Create: `tests/src/test/resources/config/ios-local.properties`
- Create: `tests/src/test/resources/config/cloud.properties`

- [ ] **Step 1: Write failing test**

```java
// core/src/test/java/com/align/config/ConfigLoaderTest.java
package com.align.config;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class ConfigLoaderTest {

    @Test
    public void returnsDefaultWhenKeyMissing() {
        System.clearProperty("test.key");
        ConfigLoader loader = new ConfigLoader("config-test.properties");
        assertEquals(loader.getProperty("test.key", "default"), "default");
    }

    @Test
    public void systemPropertyOverridesFile() {
        System.setProperty("test.override", "from-system");
        ConfigLoader loader = new ConfigLoader("config-test.properties");
        assertEquals(loader.getProperty("test.override", "default"), "from-system");
        System.clearProperty("test.override");
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

```bash
mvn test -pl core -Dtest=ConfigLoaderTest
```
Expected: FAIL — `ConfigLoader` does not exist

- [ ] **Step 3: Create ConfigLoader**

```java
// core/src/main/java/com/align/config/ConfigLoader.java
package com.align.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static final Logger log = LoggerFactory.getLogger(ConfigLoader.class);
    private static ConfigLoader instance;
    private final Properties props = new Properties();

    public ConfigLoader(String fileName) {
        loadFile(fileName);
    }

    private ConfigLoader() {
        loadFile("config.properties");
        String platform = getProperty("platform", "android");
        String env = getProperty("env", "local");
        loadFile(platform + "-" + env + ".properties");
    }

    public static synchronized ConfigLoader getInstance() {
        if (instance == null) {
            instance = new ConfigLoader();
        }
        return instance;
    }

    private void loadFile(String fileName) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("config/" + fileName)) {
            if (is != null) {
                props.load(is);
                log.debug("Loaded config file: {}", fileName);
            } else {
                log.warn("Config file not found on classpath: config/{}", fileName);
            }
        } catch (IOException e) {
            log.error("Failed to load config file: {}", fileName, e);
        }
    }

    public String getProperty(String key, String defaultValue) {
        // Resolution: CLI -D > env var > properties file > default
        String value = System.getProperty(key);
        if (value != null) return value;

        String envKey = key.toUpperCase().replace(".", "_");
        value = System.getenv(envKey);
        if (value != null) return value;

        return props.getProperty(key, defaultValue);
    }

    public String getProperty(String key) {
        return getProperty(key, "");
    }

    public boolean getBooleanProperty(String key, boolean defaultValue) {
        return Boolean.parseBoolean(getProperty(key, String.valueOf(defaultValue)));
    }

    public int getIntProperty(String key, int defaultValue) {
        try {
            return Integer.parseInt(getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
```

- [ ] **Step 4: Create test config file**

```
# core/src/test/resources/config/config-test.properties
# empty — used only for ConfigLoaderTest
```

- [ ] **Step 5: Run test to verify it passes**

```bash
mvn test -pl core -Dtest=ConfigLoaderTest
```
Expected: `Tests run: 2, Failures: 0`

- [ ] **Step 6: Create config properties files**

```properties
# tests/src/test/resources/config/config.properties
platform=android
env=local
retry.count=1
video.recording.enabled=false
video.save.on.pass=false
auto.start.server=false
default.wait.timeout=10
```

```properties
# tests/src/test/resources/config/android-local.properties
device.name=Android Emulator
device.udid=
platform.version=
app.path=
```

```properties
# tests/src/test/resources/config/ios-local.properties
device.name=iPhone 15
device.udid=
platform.version=
app.path=
```

```properties
# tests/src/test/resources/config/cloud.properties
cloud.url=https://hub-cloud.browserstack.com/wd/hub
cloud.key=
app.path=
```

- [ ] **Step 7: Commit**

```bash
git add core/src/main/java/com/align/config/ConfigLoader.java \
        core/src/test/java/com/align/config/ConfigLoaderTest.java \
        core/src/test/resources/ \
        tests/src/test/resources/config/
git commit -m "feat(core): add ConfigLoader with priority resolution (CLI > env > file > default)"
```

---

## Task 4: DeviceConfig POJO

**Files:**
- Create: `core/src/main/java/com/align/config/DeviceConfig.java`

- [ ] **Step 1: Create DeviceConfig**

```java
// core/src/main/java/com/align/config/DeviceConfig.java
package com.align.config;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeviceConfig {
    private String platform;
    private String deviceName;
    private String udid;
    private String appPath;
    private String platformVersion;
    private String executionEnv;
    private String cloudUrl;
    private String cloudKey;
    private boolean autoStartServer;

    public static DeviceConfig fromConfig() {
        ConfigLoader cfg = ConfigLoader.getInstance();
        return DeviceConfig.builder()
                .platform(cfg.getProperty("platform", "android"))
                .deviceName(cfg.getProperty("device.name", ""))
                .udid(cfg.getProperty("device.udid", ""))
                .appPath(cfg.getProperty("app.path", ""))
                .platformVersion(cfg.getProperty("platform.version", ""))
                .executionEnv(cfg.getProperty("env", "local"))
                .cloudUrl(cfg.getProperty("cloud.url", ""))
                .cloudKey(cfg.getProperty("cloud.key", ""))
                .autoStartServer(cfg.getBooleanProperty("auto.start.server", false))
                .build();
    }

    public boolean isAndroid() {
        return "android".equalsIgnoreCase(platform);
    }

    public boolean isIOS() {
        return "ios".equalsIgnoreCase(platform);
    }

    public boolean isCloud() {
        return "cloud".equalsIgnoreCase(executionEnv);
    }
}
```

- [ ] **Step 2: Compile**

```bash
mvn compile -pl core
```
Expected: `BUILD SUCCESS`

- [ ] **Step 3: Commit**

```bash
git add core/src/main/java/com/align/config/DeviceConfig.java
git commit -m "feat(core): add DeviceConfig POJO with Lombok builder"
```

---

## Task 5: AppResolver

**Files:**
- Create: `core/src/main/java/com/align/utils/AppResolver.java`
- Create: `core/src/test/java/com/align/utils/AppResolverTest.java`

- [ ] **Step 1: Write failing test**

```java
// core/src/test/java/com/align/utils/AppResolverTest.java
package com.align.utils;

import com.align.exceptions.AppNotFoundException;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class AppResolverTest {

    @Test
    public void passesThroughCloudAppId() {
        String result = AppResolver.resolve("bs://abc123");
        assertEquals(result, "bs://abc123");
    }

    @Test
    public void passesThroughSauceLabs() {
        String result = AppResolver.resolve("sauce-storage:app.apk");
        assertEquals(result, "sauce-storage:app.apk");
    }

    @Test
    public void throwsWhenLocalPathMissing() {
        assertThrows(AppNotFoundException.class,
                () -> AppResolver.resolve("/nonexistent/path/app.apk"));
    }

    @Test
    public void returnsEmptyForEmptyInput() {
        assertEquals(AppResolver.resolve(""), "");
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

```bash
mvn test -pl core -Dtest=AppResolverTest
```
Expected: FAIL — `AppResolver` does not exist

- [ ] **Step 3: Create AppResolver**

```java
// core/src/main/java/com/align/utils/AppResolver.java
package com.align.utils;

import com.align.exceptions.AppNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AppResolver {
    private static final Logger log = LoggerFactory.getLogger(AppResolver.class);

    private AppResolver() {}

    public static String resolve(String appPath) {
        if (appPath == null || appPath.isBlank()) return "";

        if (isCloudAppId(appPath)) {
            log.debug("Cloud app ID detected, passing through: {}", appPath);
            return appPath;
        }

        String downloadUrl = System.getProperty("app.url", System.getenv("APP_URL"));
        if (downloadUrl != null && !downloadUrl.isBlank()) {
            return downloadFromUrl(downloadUrl, appPath);
        }

        File appFile = new File(appPath);
        if (!appFile.exists()) {
            throw new AppNotFoundException(appPath);
        }
        log.info("App resolved: {}", appFile.getAbsolutePath());
        return appFile.getAbsolutePath();
    }

    private static boolean isCloudAppId(String path) {
        return path.startsWith("bs://") || path.startsWith("sauce-storage:");
    }

    private static String downloadFromUrl(String url, String targetFileName) {
        try {
            Path targetDir = Paths.get("target", "apps");
            Files.createDirectories(targetDir);
            String fileName = targetFileName.isBlank()
                    ? url.substring(url.lastIndexOf('/') + 1)
                    : new File(targetFileName).getName();
            Path targetPath = targetDir.resolve(fileName);

            log.info("Downloading app from {} to {}", url, targetPath);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();
            client.send(request, HttpResponse.BodyHandlers.ofFile(targetPath));
            log.info("App downloaded: {}", targetPath);
            return targetPath.toAbsolutePath().toString();
        } catch (IOException | InterruptedException e) {
            throw new AppNotFoundException(url + " (download failed: " + e.getMessage() + ")");
        }
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

```bash
mvn test -pl core -Dtest=AppResolverTest
```
Expected: `Tests run: 4, Failures: 0`

- [ ] **Step 5: Commit**

```bash
git add core/src/main/java/com/align/utils/AppResolver.java \
        core/src/test/java/com/align/utils/AppResolverTest.java
git commit -m "feat(core): add AppResolver for local/cloud/download app binary resolution"
```

---

## Task 6: DriverManager

**Files:**
- Create: `core/src/main/java/com/align/driver/DriverManager.java`
- Create: `core/src/test/java/com/align/driver/DriverManagerTest.java`

- [ ] **Step 1: Write failing test**

```java
// core/src/test/java/com/align/driver/DriverManagerTest.java
package com.align.driver;

import com.align.exceptions.DriverInitException;
import io.appium.java_client.AppiumDriver;
import org.mockito.Mockito;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class DriverManagerTest {

    @AfterMethod
    public void cleanup() {
        if (DriverManager.hasDriver()) {
            DriverManager.removeDriver();
        }
    }

    @Test
    public void setAndGetDriver() {
        AppiumDriver mockDriver = Mockito.mock(AppiumDriver.class);
        DriverManager.setDriver(mockDriver);
        assertSame(DriverManager.getDriver(), mockDriver);
    }

    @Test
    public void throwsWhenNoDriverSet() {
        assertThrows(DriverInitException.class, DriverManager::getDriver);
    }

    @Test
    public void hasDriverReturnsFalseWhenNotSet() {
        assertFalse(DriverManager.hasDriver());
    }

    @Test
    public void removeDriverClearsState() {
        AppiumDriver mockDriver = Mockito.mock(AppiumDriver.class);
        DriverManager.setDriver(mockDriver);
        DriverManager.removeDriver();
        assertFalse(DriverManager.hasDriver());
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

```bash
mvn test -pl core -Dtest=DriverManagerTest
```
Expected: FAIL — `DriverManager` does not exist

- [ ] **Step 3: Create DriverManager**

```java
// core/src/main/java/com/align/driver/DriverManager.java
package com.align.driver;

import com.align.exceptions.DriverInitException;
import io.appium.java_client.AppiumDriver;

public class DriverManager {
    private static final ThreadLocal<AppiumDriver> driverThread = new ThreadLocal<>();

    private DriverManager() {}

    public static void setDriver(AppiumDriver driver) {
        driverThread.set(driver);
    }

    public static AppiumDriver getDriver() {
        AppiumDriver driver = driverThread.get();
        if (driver == null) {
            throw new DriverInitException(
                "No AppiumDriver initialized for thread: " + Thread.currentThread().getName());
        }
        return driver;
    }

    public static void removeDriver() {
        driverThread.remove();
    }

    public static boolean hasDriver() {
        return driverThread.get() != null;
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

```bash
mvn test -pl core -Dtest=DriverManagerTest
```
Expected: `Tests run: 4, Failures: 0`

- [ ] **Step 5: Commit**

```bash
git add core/src/main/java/com/align/driver/DriverManager.java \
        core/src/test/java/com/align/driver/DriverManagerTest.java
git commit -m "feat(core): add ThreadLocal DriverManager for parallel-safe driver storage"
```

---

## Task 7: ServerManager

**Files:**
- Create: `core/src/main/java/com/align/driver/ServerManager.java`

- [ ] **Step 1: Create ServerManager**

```java
// core/src/main/java/com/align/driver/ServerManager.java
package com.align.driver;

import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Duration;

public class ServerManager {
    private static final Logger log = LoggerFactory.getLogger(ServerManager.class);
    private static AppiumDriverLocalService service;

    private ServerManager() {}

    public static synchronized void startServer() {
        if (service != null && service.isRunning()) {
            log.info("Appium server already running");
            return;
        }
        AppiumServiceBuilder builder = new AppiumServiceBuilder()
                .withIPAddress("127.0.0.1")
                .usingPort(4723)
                .withTimeout(Duration.ofSeconds(60));

        String appiumPath = System.getenv("APPIUM_PATH");
        if (appiumPath != null && !appiumPath.isBlank()) {
            builder.withAppiumJS(new File(appiumPath));
        }

        service = builder.build();
        service.start();
        log.info("Appium server started at http://127.0.0.1:4723");
    }

    public static synchronized void stopServer() {
        if (service != null && service.isRunning()) {
            service.stop();
            log.info("Appium server stopped");
        }
    }

    public static boolean isRunning() {
        return service != null && service.isRunning();
    }
}
```

- [ ] **Step 2: Compile**

```bash
mvn compile -pl core
```
Expected: `BUILD SUCCESS`

- [ ] **Step 3: Commit**

```bash
git add core/src/main/java/com/align/driver/ServerManager.java
git commit -m "feat(core): add ServerManager for optional local Appium server auto-start"
```

---

## Task 8: Android Capabilities + Driver Factory

**Files:**
- Create: `android/src/main/java/com/align/android/capabilities/AndroidCapabilities.java`
- Create: `android/src/main/java/com/align/android/driver/AndroidDriverFactory.java`

- [ ] **Step 1: Create AndroidCapabilities**

```java
// android/src/main/java/com/align/android/capabilities/AndroidCapabilities.java
package com.align.android.capabilities;

import com.align.config.DeviceConfig;
import io.appium.java_client.android.options.UiAutomator2Options;

import java.util.HashMap;
import java.util.Map;

public class AndroidCapabilities {
    private AndroidCapabilities() {}

    public static UiAutomator2Options build(DeviceConfig config) {
        UiAutomator2Options options = new UiAutomator2Options();

        if (!config.getDeviceName().isEmpty()) {
            options.setDeviceName(config.getDeviceName());
        }
        if (!config.getUdid().isEmpty()) {
            options.setUdid(config.getUdid());
        }
        if (!config.getAppPath().isEmpty()) {
            options.setApp(config.getAppPath());
        }
        if (!config.getPlatformVersion().isEmpty()) {
            options.setPlatformVersion(config.getPlatformVersion());
        }

        options.setNoReset(false);
        options.setAutoGrantPermissions(true);
        options.setCapability("newCommandTimeout", 300);

        if (config.isCloud()) {
            applyCloudCapabilities(options, config);
        }

        return options;
    }

    private static void applyCloudCapabilities(UiAutomator2Options options, DeviceConfig config) {
        // BrowserStack cloud capabilities
        // cloud.key format: "username:accessKey"
        if (!config.getCloudKey().isEmpty() && config.getCloudKey().contains(":")) {
            String[] parts = config.getCloudKey().split(":", 2);
            Map<String, Object> bsOptions = new HashMap<>();
            bsOptions.put("userName", parts[0]);
            bsOptions.put("accessKey", parts[1]);
            bsOptions.put("projectName", "AlignApp");
            bsOptions.put("buildName", System.getenv().getOrDefault("BUILD_NAME", "local-build"));
            options.setCapability("bstack:options", bsOptions);
        }
    }
}
```

- [ ] **Step 2: Create AndroidDriverFactory**

```java
// android/src/main/java/com/align/android/driver/AndroidDriverFactory.java
package com.align.android.driver;

import com.align.config.DeviceConfig;
import com.align.android.capabilities.AndroidCapabilities;
import com.align.exceptions.DriverInitException;
import io.appium.java_client.android.AndroidDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

public class AndroidDriverFactory {
    private static final Logger log = LoggerFactory.getLogger(AndroidDriverFactory.class);

    public AndroidDriver createDriver(DeviceConfig config) {
        try {
            URL serverUrl = resolveServerUrl(config);
            log.info("Creating AndroidDriver — server: {}, device: {}, env: {}",
                    serverUrl, config.getDeviceName(), config.getExecutionEnv());
            return new AndroidDriver(serverUrl, AndroidCapabilities.build(config));
        } catch (MalformedURLException e) {
            throw new DriverInitException("Invalid Appium server URL: " + e.getMessage(), e);
        }
    }

    private URL resolveServerUrl(DeviceConfig config) throws MalformedURLException {
        if (config.isCloud()) {
            if (config.getCloudUrl().isEmpty()) {
                throw new DriverInitException("cloud.url is not configured for cloud execution");
            }
            return new URL(config.getCloudUrl());
        }
        return new URL("http://127.0.0.1:4723");
    }
}
```

- [ ] **Step 3: Compile**

```bash
mvn compile -pl android
```
Expected: `BUILD SUCCESS`

- [ ] **Step 4: Commit**

```bash
git add android/src/main/java/com/align/android/
git commit -m "feat(android): add AndroidCapabilities (UiAutomator2) and AndroidDriverFactory"
```

---

## Task 9: iOS Capabilities + Driver Factory

**Files:**
- Create: `ios/src/main/java/com/align/ios/capabilities/IOSCapabilities.java`
- Create: `ios/src/main/java/com/align/ios/driver/IOSDriverFactory.java`

- [ ] **Step 1: Create IOSCapabilities**

```java
// ios/src/main/java/com/align/ios/capabilities/IOSCapabilities.java
package com.align.ios.capabilities;

import com.align.config.DeviceConfig;
import io.appium.java_client.ios.options.XCUITestOptions;

import java.util.HashMap;
import java.util.Map;

public class IOSCapabilities {
    private IOSCapabilities() {}

    public static XCUITestOptions build(DeviceConfig config) {
        XCUITestOptions options = new XCUITestOptions();

        if (!config.getDeviceName().isEmpty()) {
            options.setDeviceName(config.getDeviceName());
        }
        if (!config.getUdid().isEmpty()) {
            options.setUdid(config.getUdid());
        }
        if (!config.getAppPath().isEmpty()) {
            options.setApp(config.getAppPath());
        }
        if (!config.getPlatformVersion().isEmpty()) {
            options.setPlatformVersion(config.getPlatformVersion());
        }

        options.setNoReset(false);
        options.setAutoAcceptAlerts(false);
        options.setCapability("newCommandTimeout", 300);
        options.setWdaLaunchTimeout(60000);

        if (config.isCloud()) {
            applyCloudCapabilities(options, config);
        }

        return options;
    }

    private static void applyCloudCapabilities(XCUITestOptions options, DeviceConfig config) {
        if (!config.getCloudKey().isEmpty() && config.getCloudKey().contains(":")) {
            String[] parts = config.getCloudKey().split(":", 2);
            Map<String, Object> bsOptions = new HashMap<>();
            bsOptions.put("userName", parts[0]);
            bsOptions.put("accessKey", parts[1]);
            bsOptions.put("projectName", "AlignApp");
            bsOptions.put("buildName", System.getenv().getOrDefault("BUILD_NAME", "local-build"));
            options.setCapability("bstack:options", bsOptions);
        }
    }
}
```

- [ ] **Step 2: Create IOSDriverFactory**

```java
// ios/src/main/java/com/align/ios/driver/IOSDriverFactory.java
package com.align.ios.driver;

import com.align.config.DeviceConfig;
import com.align.ios.capabilities.IOSCapabilities;
import com.align.exceptions.DriverInitException;
import io.appium.java_client.ios.IOSDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

public class IOSDriverFactory {
    private static final Logger log = LoggerFactory.getLogger(IOSDriverFactory.class);

    public IOSDriver createDriver(DeviceConfig config) {
        try {
            URL serverUrl = resolveServerUrl(config);
            log.info("Creating IOSDriver — server: {}, device: {}, env: {}",
                    serverUrl, config.getDeviceName(), config.getExecutionEnv());
            return new IOSDriver(serverUrl, IOSCapabilities.build(config));
        } catch (MalformedURLException e) {
            throw new DriverInitException("Invalid Appium server URL: " + e.getMessage(), e);
        }
    }

    private URL resolveServerUrl(DeviceConfig config) throws MalformedURLException {
        if (config.isCloud()) {
            if (config.getCloudUrl().isEmpty()) {
                throw new DriverInitException("cloud.url is not configured for cloud execution");
            }
            return new URL(config.getCloudUrl());
        }
        return new URL("http://127.0.0.1:4723");
    }
}
```

- [ ] **Step 3: Compile**

```bash
mvn compile -pl ios
```
Expected: `BUILD SUCCESS`

- [ ] **Step 4: Commit**

```bash
git add ios/src/main/java/com/align/ios/
git commit -m "feat(ios): add IOSCapabilities (XCUITest) and IOSDriverFactory"
```

---

## Task 10: DriverFactory (Platform Router)

**Files:**
- Create: `core/src/main/java/com/align/driver/DriverFactory.java`

- [ ] **Step 1: Create DriverFactory**

```java
// core/src/main/java/com/align/driver/DriverFactory.java
package com.align.driver;

import com.align.config.DeviceConfig;
import com.align.exceptions.DriverInitException;
import io.appium.java_client.AppiumDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DriverFactory {
    private static final Logger log = LoggerFactory.getLogger(DriverFactory.class);

    private DriverFactory() {}

    public static AppiumDriver createDriver(DeviceConfig config) {
        if (config.isAndroid()) {
            return createAndroidDriver(config);
        } else if (config.isIOS()) {
            return createIOSDriver(config);
        }
        throw new DriverInitException("Unsupported platform: " + config.getPlatform()
                + ". Supported: android, ios");
    }

    private static AppiumDriver createAndroidDriver(DeviceConfig config) {
        try {
            Class<?> factoryClass = Class.forName("com.align.android.driver.AndroidDriverFactory");
            Object factory = factoryClass.getDeclaredConstructor().newInstance();
            return (AppiumDriver) factoryClass.getMethod("createDriver", DeviceConfig.class)
                    .invoke(factory, config);
        } catch (Exception e) {
            throw new DriverInitException("Failed to create AndroidDriver: " + e.getMessage(), e);
        }
    }

    private static AppiumDriver createIOSDriver(DeviceConfig config) {
        try {
            Class<?> factoryClass = Class.forName("com.align.ios.driver.IOSDriverFactory");
            Object factory = factoryClass.getDeclaredConstructor().newInstance();
            return (AppiumDriver) factoryClass.getMethod("createDriver", DeviceConfig.class)
                    .invoke(factory, config);
        } catch (Exception e) {
            throw new DriverInitException("Failed to create IOSDriver: " + e.getMessage(), e);
        }
    }
}
```

> **Note:** `DriverFactory` uses reflection to avoid a direct compile-time dependency from `core` on `android`/`ios` modules. The `tests` module classpath contains all three modules, so reflection succeeds at runtime. This preserves the dependency direction: `android`/`ios` depend on `core`, not the reverse.

- [ ] **Step 2: Compile**

```bash
mvn compile -pl core
```
Expected: `BUILD SUCCESS`

- [ ] **Step 3: Commit**

```bash
git add core/src/main/java/com/align/driver/DriverFactory.java
git commit -m "feat(core): add DriverFactory platform router using reflection to avoid circular deps"
```

---

## Task 11: BasePage

**Files:**
- Create: `core/src/main/java/com/align/pages/BasePage.java`

- [ ] **Step 1: Create BasePage**

```java
// core/src/main/java/com/align/pages/BasePage.java
package com.align.pages;

import com.align.driver.DriverManager;
import com.align.exceptions.PageException;
import com.align.utils.WaitUtils;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BasePage {
    private static final Logger log = LoggerFactory.getLogger(BasePage.class);
    private static final int DEFAULT_TIMEOUT = 10;

    protected AppiumDriver getDriver() {
        return DriverManager.getDriver();
    }

    protected String getPageName() {
        return getClass().getSimpleName();
    }

    protected WebElement waitForVisible(By locator) {
        return waitForVisible(locator, DEFAULT_TIMEOUT);
    }

    protected WebElement waitForVisible(By locator, int timeoutSeconds) {
        try {
            return WaitUtils.waitForVisible(getDriver(), locator, timeoutSeconds);
        } catch (Exception e) {
            throw new PageException(getPageName(), locator.toString(),
                    "Element not visible after " + timeoutSeconds + "s", e);
        }
    }

    protected void tap(By locator) {
        log.debug("[{}] tap: {}", getPageName(), locator);
        try {
            WaitUtils.waitForClickable(getDriver(), locator, DEFAULT_TIMEOUT).click();
        } catch (Exception e) {
            throw new PageException(getPageName(), locator.toString(), "Not clickable", e);
        }
    }

    protected void sendKeys(By locator, String text) {
        log.debug("[{}] sendKeys: {} = '{}'", getPageName(), locator, text);
        WebElement element = waitForVisible(locator);
        element.clear();
        element.sendKeys(text);
    }

    protected String getText(By locator) {
        return waitForVisible(locator).getText();
    }

    protected boolean isElementVisible(By locator) {
        return WaitUtils.isVisible(getDriver(), locator, 5);
    }

    protected void waitForInvisible(By locator) {
        WaitUtils.waitForInvisible(getDriver(), locator, DEFAULT_TIMEOUT);
    }

    protected void waitForInvisible(By locator, int timeoutSeconds) {
        WaitUtils.waitForInvisible(getDriver(), locator, timeoutSeconds);
    }
}
```

- [ ] **Step 2: Compile**

```bash
mvn compile -pl core
```
Expected: `BUILD SUCCESS`

- [ ] **Step 3: Commit**

```bash
git add core/src/main/java/com/align/pages/BasePage.java
git commit -m "feat(core): add BasePage with wait-wrapped interaction methods"
```

---

## Task 12: WaitUtils

**Files:**
- Create: `core/src/main/java/com/align/utils/WaitUtils.java`

- [ ] **Step 1: Create WaitUtils**

```java
// core/src/main/java/com/align/utils/WaitUtils.java
package com.align.utils;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class WaitUtils {
    private WaitUtils() {}

    public static WebElement waitForVisible(AppiumDriver driver, By locator, int timeoutSeconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement waitForClickable(AppiumDriver driver, By locator, int timeoutSeconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                .until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static boolean waitForInvisible(AppiumDriver driver, By locator, int timeoutSeconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                .until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public static boolean isVisible(AppiumDriver driver, By locator, int timeoutSeconds) {
        try {
            waitForVisible(driver, locator, timeoutSeconds);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static WebElement fluentWait(AppiumDriver driver, By locator,
                                        Duration timeout, Duration pollingInterval) {
        return new FluentWait<>(driver)
                .withTimeout(timeout)
                .pollingEvery(pollingInterval)
                .ignoring(NoSuchElementException.class)
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
}
```

- [ ] **Step 2: Compile**

```bash
mvn compile -pl core
```
Expected: `BUILD SUCCESS`

- [ ] **Step 3: Commit**

```bash
git add core/src/main/java/com/align/utils/WaitUtils.java
git commit -m "feat(core): add WaitUtils — explicit waits, no Thread.sleep"
```

---

## Task 13: GestureUtils

**Files:**
- Create: `core/src/main/java/com/align/utils/GestureUtils.java`

- [ ] **Step 1: Create GestureUtils**

```java
// core/src/main/java/com/align/utils/GestureUtils.java
package com.align.utils;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class GestureUtils {
    private GestureUtils() {}

    private static final int SWIPE_DURATION_MS = 600;
    private static final int MAX_SCROLL_SWIPES = 5;

    public static void swipeUp(AppiumDriver driver) {
        Dimension size = driver.manage().window().getSize();
        int x = size.width / 2;
        swipe(driver, x, (int)(size.height * 0.8), x, (int)(size.height * 0.2));
    }

    public static void swipeDown(AppiumDriver driver) {
        Dimension size = driver.manage().window().getSize();
        int x = size.width / 2;
        swipe(driver, x, (int)(size.height * 0.2), x, (int)(size.height * 0.8));
    }

    public static void swipeLeft(AppiumDriver driver) {
        Dimension size = driver.manage().window().getSize();
        int y = size.height / 2;
        swipe(driver, (int)(size.width * 0.8), y, (int)(size.width * 0.2), y);
    }

    public static void swipeRight(AppiumDriver driver) {
        Dimension size = driver.manage().window().getSize();
        int y = size.height / 2;
        swipe(driver, (int)(size.width * 0.2), y, (int)(size.width * 0.8), y);
    }

    public static void swipe(AppiumDriver driver, int startX, int startY, int endX, int endY) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger1");
        Sequence sequence = new Sequence(finger, 0)
                .addAction(finger.createPointerMove(Duration.ZERO,
                        PointerInput.Origin.viewport(), startX, startY))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerMove(Duration.ofMillis(SWIPE_DURATION_MS),
                        PointerInput.Origin.viewport(), endX, endY))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(List.of(sequence));
    }

    public static void scrollToElement(AppiumDriver driver, By locator) {
        for (int i = 0; i < MAX_SCROLL_SWIPES; i++) {
            if (WaitUtils.isVisible(driver, locator, 2)) return;
            swipeUp(driver);
        }
        throw new RuntimeException("Element not found after " + MAX_SCROLL_SWIPES + " swipes: " + locator);
    }

    public static void scrollToText(AppiumDriver driver, String text) {
        scrollToElement(driver,
                By.xpath("//*[@text='" + text + "' or @label='" + text + "']"));
    }

    public static void longPress(AppiumDriver driver, By locator) {
        WebElement element = WaitUtils.waitForVisible(driver, locator, 10);
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger1");
        Sequence sequence = new Sequence(finger, 0)
                .addAction(finger.createPointerMove(Duration.ZERO,
                        PointerInput.Origin.fromElement(element), 0, 0))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerMove(Duration.ofMillis(1500),
                        PointerInput.Origin.fromElement(element), 0, 0))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(List.of(sequence));
    }

    public static void doubleTap(AppiumDriver driver, By locator) {
        WebElement element = WaitUtils.waitForVisible(driver, locator, 10);
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger1");
        Sequence sequence = new Sequence(finger, 0)
                .addAction(finger.createPointerMove(Duration.ZERO,
                        PointerInput.Origin.fromElement(element), 0, 0))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(List.of(sequence));
    }

    public static void tap(AppiumDriver driver, int x, int y) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger1");
        Sequence sequence = new Sequence(finger, 0)
                .addAction(finger.createPointerMove(Duration.ZERO,
                        PointerInput.Origin.viewport(), x, y))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(List.of(sequence));
    }

    public static void pinchZoomIn(AppiumDriver driver, By locator) {
        WebElement element = WaitUtils.waitForVisible(driver, locator, 10);
        Point center = element.getLocation();
        Dimension size = element.getSize();
        int cx = center.x + size.width / 2;
        int cy = center.y + size.height / 2;

        PointerInput f1 = new PointerInput(PointerInput.Kind.TOUCH, "f1");
        PointerInput f2 = new PointerInput(PointerInput.Kind.TOUCH, "f2");

        Sequence seq1 = new Sequence(f1, 0)
                .addAction(f1.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), cx, cy))
                .addAction(f1.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(f1.createPointerMove(Duration.ofMillis(600),
                        PointerInput.Origin.viewport(), cx - 150, cy))
                .addAction(f1.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        Sequence seq2 = new Sequence(f2, 0)
                .addAction(f2.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), cx, cy))
                .addAction(f2.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(f2.createPointerMove(Duration.ofMillis(600),
                        PointerInput.Origin.viewport(), cx + 150, cy))
                .addAction(f2.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Arrays.asList(seq1, seq2));
    }

    public static void pinchZoomOut(AppiumDriver driver, By locator) {
        WebElement element = WaitUtils.waitForVisible(driver, locator, 10);
        Point center = element.getLocation();
        Dimension size = element.getSize();
        int cx = center.x + size.width / 2;
        int cy = center.y + size.height / 2;

        PointerInput f1 = new PointerInput(PointerInput.Kind.TOUCH, "f1");
        PointerInput f2 = new PointerInput(PointerInput.Kind.TOUCH, "f2");

        Sequence seq1 = new Sequence(f1, 0)
                .addAction(f1.createPointerMove(Duration.ZERO,
                        PointerInput.Origin.viewport(), cx - 150, cy))
                .addAction(f1.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(f1.createPointerMove(Duration.ofMillis(600),
                        PointerInput.Origin.viewport(), cx, cy))
                .addAction(f1.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        Sequence seq2 = new Sequence(f2, 0)
                .addAction(f2.createPointerMove(Duration.ZERO,
                        PointerInput.Origin.viewport(), cx + 150, cy))
                .addAction(f2.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(f2.createPointerMove(Duration.ofMillis(600),
                        PointerInput.Origin.viewport(), cx, cy))
                .addAction(f2.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Arrays.asList(seq1, seq2));
    }

    public static void dragAndDrop(AppiumDriver driver, By source, By target) {
        WebElement srcEl = WaitUtils.waitForVisible(driver, source, 10);
        WebElement tgtEl = WaitUtils.waitForVisible(driver, target, 10);
        Point srcPt = srcEl.getLocation();
        Point tgtPt = tgtEl.getLocation();
        int srcCx = srcPt.x + srcEl.getSize().width / 2;
        int srcCy = srcPt.y + srcEl.getSize().height / 2;
        int tgtCx = tgtPt.x + tgtEl.getSize().width / 2;
        int tgtCy = tgtPt.y + tgtEl.getSize().height / 2;

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger1");
        Sequence sequence = new Sequence(finger, 0)
                .addAction(finger.createPointerMove(Duration.ZERO,
                        PointerInput.Origin.viewport(), srcCx, srcCy))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerMove(Duration.ofMillis(1000),
                        PointerInput.Origin.viewport(), tgtCx, tgtCy))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(List.of(sequence));
    }
}
```

- [ ] **Step 2: Compile**

```bash
mvn compile -pl core
```
Expected: `BUILD SUCCESS`

- [ ] **Step 3: Commit**

```bash
git add core/src/main/java/com/align/utils/GestureUtils.java
git commit -m "feat(core): add GestureUtils with W3C Actions (swipe, scroll, longPress, pinch, drag)"
```

---

## Task 14: KeyboardUtils + AlertUtils

**Files:**
- Create: `core/src/main/java/com/align/utils/KeyboardUtils.java`
- Create: `core/src/main/java/com/align/utils/AlertUtils.java`

- [ ] **Step 1: Create KeyboardUtils**

```java
// core/src/main/java/com/align/utils/KeyboardUtils.java
package com.align.utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeyboardUtils {
    private static final Logger log = LoggerFactory.getLogger(KeyboardUtils.class);

    private KeyboardUtils() {}

    public static void hideKeyboard(AppiumDriver driver) {
        try {
            driver.hideKeyboard();
        } catch (Exception e) {
            log.warn("Could not hide keyboard: {}", e.getMessage());
        }
    }

    public static boolean isKeyboardShown(AppiumDriver driver) {
        try {
            return driver.isKeyboardShown();
        } catch (Exception e) {
            return false;
        }
    }

    public static void pressEnter(AppiumDriver driver) {
        pressAndroidKey(driver, AndroidKey.ENTER);
    }

    public static void pressBack(AppiumDriver driver) {
        pressAndroidKey(driver, AndroidKey.BACK);
    }

    public static void pressHome(AppiumDriver driver) {
        pressAndroidKey(driver, AndroidKey.HOME);
    }

    public static void pressAndroidKey(AppiumDriver driver, AndroidKey key) {
        if (driver instanceof AndroidDriver) {
            ((AndroidDriver) driver).pressKey(new KeyEvent(key));
        } else {
            log.warn("pressAndroidKey called on non-Android driver — skipped");
        }
    }

    public static void typeWithKeyboard(AppiumDriver driver, By locator, String text) {
        WebElement element = WaitUtils.waitForClickable(driver, locator, 10);
        element.click();
        element.clear();
        element.sendKeys(text);
        if (isKeyboardShown(driver)) {
            hideKeyboard(driver);
        }
    }
}
```

- [ ] **Step 2: Create AlertUtils**

```java
// core/src/main/java/com/align/utils/AlertUtils.java
package com.align.utils;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class AlertUtils {
    private static final Logger log = LoggerFactory.getLogger(AlertUtils.class);

    private AlertUtils() {}

    public static void acceptAlert(AppiumDriver driver) {
        waitForAlert(driver);
        driver.switchTo().alert().accept();
        log.debug("Alert accepted");
    }

    public static void dismissAlert(AppiumDriver driver) {
        waitForAlert(driver);
        driver.switchTo().alert().dismiss();
        log.debug("Alert dismissed");
    }

    public static String getAlertText(AppiumDriver driver) {
        waitForAlert(driver);
        return driver.switchTo().alert().getText();
    }

    public static boolean isAlertPresent(AppiumDriver driver) {
        try {
            driver.switchTo().alert();
            return true;
        } catch (NoAlertPresentException e) {
            return false;
        }
    }

    public static void handlePermissionAlert(AppiumDriver driver, String action) {
        // action: "allow" or "deny"
        if (!isAlertPresent(driver)) return;
        if ("allow".equalsIgnoreCase(action)) {
            acceptAlert(driver);
        } else {
            dismissAlert(driver);
        }
    }

    private static void waitForAlert(AppiumDriver driver) {
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.alertIsPresent());
    }
}
```

- [ ] **Step 3: Compile**

```bash
mvn compile -pl core
```
Expected: `BUILD SUCCESS`

- [ ] **Step 4: Commit**

```bash
git add core/src/main/java/com/align/utils/KeyboardUtils.java \
        core/src/main/java/com/align/utils/AlertUtils.java
git commit -m "feat(core): add KeyboardUtils and AlertUtils"
```

---

## Task 15: PermissionUtils + AppUtils

**Files:**
- Create: `core/src/main/java/com/align/utils/PermissionUtils.java`
- Create: `core/src/main/java/com/align/utils/AppUtils.java`

- [ ] **Step 1: Create PermissionUtils**

```java
// core/src/main/java/com/align/utils/PermissionUtils.java
package com.align.utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PermissionUtils {
    private static final Logger log = LoggerFactory.getLogger(PermissionUtils.class);

    // Android permission dialog button resource IDs (AOSP defaults)
    private static final By ALLOW_BUTTON = By.id("com.android.permissioncontroller:id/permission_allow_button");
    private static final By DENY_BUTTON  = By.id("com.android.permissioncontroller:id/permission_deny_button");
    private static final By ALLOW_FOREGROUND = By.id("com.android.permissioncontroller:id/permission_allow_foreground_only_button");

    // iOS permission dialog (XCUITest)
    private static final By IOS_ALLOW = By.xpath("//XCUIElementTypeButton[@name='Allow']");
    private static final By IOS_DENY  = By.xpath("//XCUIElementTypeButton[@name=\"Don't Allow\"]");

    private PermissionUtils() {}

    public static void allowPermission(AppiumDriver driver) {
        if (driver instanceof AndroidDriver) {
            if (WaitUtils.isVisible(driver, ALLOW_BUTTON, 3)) {
                driver.findElement(ALLOW_BUTTON).click();
            } else if (WaitUtils.isVisible(driver, ALLOW_FOREGROUND, 3)) {
                driver.findElement(ALLOW_FOREGROUND).click();
            }
        } else {
            if (WaitUtils.isVisible(driver, IOS_ALLOW, 3)) {
                driver.findElement(IOS_ALLOW).click();
            }
        }
    }

    public static void denyPermission(AppiumDriver driver) {
        if (driver instanceof AndroidDriver) {
            if (WaitUtils.isVisible(driver, DENY_BUTTON, 3)) {
                driver.findElement(DENY_BUTTON).click();
            }
        } else {
            if (WaitUtils.isVisible(driver, IOS_DENY, 3)) {
                driver.findElement(IOS_DENY).click();
            }
        }
    }

    public static void grantPermission(AppiumDriver driver, String packageName, String permission) {
        if (driver instanceof AndroidDriver) {
            String cmd = String.format("pm grant %s %s", packageName, permission);
            ((AndroidDriver) driver).executeScript("mobile: shell", java.util.Map.of("command", cmd));
            log.info("Granted permission {} to {}", permission, packageName);
        } else {
            log.warn("grantPermission via ADB is Android-only");
        }
    }

    public static void resetPermissions(AppiumDriver driver, String packageName) {
        if (driver instanceof AndroidDriver) {
            String cmd = "pm reset-permissions " + packageName;
            ((AndroidDriver) driver).executeScript("mobile: shell", java.util.Map.of("command", cmd));
            log.info("Reset permissions for {}", packageName);
        }
    }
}
```

- [ ] **Step 2: Create AppUtils**

```java
// core/src/main/java/com/align/utils/AppUtils.java
package com.align.utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.InteractsWithApps;
import io.appium.java_client.appmanagement.ApplicationState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class AppUtils {
    private static final Logger log = LoggerFactory.getLogger(AppUtils.class);

    private AppUtils() {}

    public static void installApp(AppiumDriver driver, String appPath) {
        driver.installApp(appPath);
        log.info("App installed: {}", appPath);
    }

    public static void removeApp(AppiumDriver driver, String bundleIdOrPackage) {
        driver.removeApp(bundleIdOrPackage);
        log.info("App removed: {}", bundleIdOrPackage);
    }

    public static boolean isAppInstalled(AppiumDriver driver, String bundleIdOrPackage) {
        return driver.isAppInstalled(bundleIdOrPackage);
    }

    public static void activateApp(AppiumDriver driver, String bundleIdOrPackage) {
        driver.activateApp(bundleIdOrPackage);
        log.info("App activated: {}", bundleIdOrPackage);
    }

    public static void terminateApp(AppiumDriver driver, String bundleIdOrPackage) {
        driver.terminateApp(bundleIdOrPackage);
        log.info("App terminated: {}", bundleIdOrPackage);
    }

    public static ApplicationState getAppState(AppiumDriver driver, String bundleIdOrPackage) {
        return driver.queryAppState(bundleIdOrPackage);
    }

    public static void runAppInBackground(AppiumDriver driver, Duration duration) {
        driver.runAppInBackground(duration);
        log.debug("App ran in background for {}", duration);
    }

    public static void resetApp(AppiumDriver driver, String bundleIdOrPackage) {
        terminateApp(driver, bundleIdOrPackage);
        activateApp(driver, bundleIdOrPackage);
    }
}
```

- [ ] **Step 3: Compile**

```bash
mvn compile -pl core
```
Expected: `BUILD SUCCESS`

- [ ] **Step 4: Commit**

```bash
git add core/src/main/java/com/align/utils/PermissionUtils.java \
        core/src/main/java/com/align/utils/AppUtils.java
git commit -m "feat(core): add PermissionUtils and AppUtils"
```

---

## Task 16: DeviceUtils + NetworkUtils

**Files:**
- Create: `core/src/main/java/com/align/utils/DeviceUtils.java`
- Create: `core/src/main/java/com/align/utils/NetworkUtils.java`

- [ ] **Step 1: Create DeviceUtils**

```java
// core/src/main/java/com/align/utils/DeviceUtils.java
package com.align.utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.DeviceRotation;
import org.openqa.selenium.ScreenOrientation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class DeviceUtils {
    private static final Logger log = LoggerFactory.getLogger(DeviceUtils.class);

    private DeviceUtils() {}

    public static void lockDevice(AppiumDriver driver) {
        driver.lockDevice();
        log.debug("Device locked");
    }

    public static void lockDevice(AppiumDriver driver, Duration duration) {
        driver.lockDevice(duration);
    }

    public static void unlockDevice(AppiumDriver driver) {
        driver.unlockDevice();
        log.debug("Device unlocked");
    }

    public static boolean isDeviceLocked(AppiumDriver driver) {
        return driver.isDeviceLocked();
    }

    public static void rotatePortrait(AppiumDriver driver) {
        driver.rotate(ScreenOrientation.PORTRAIT);
    }

    public static void rotateLandscape(AppiumDriver driver) {
        driver.rotate(ScreenOrientation.LANDSCAPE);
    }

    public static ScreenOrientation getDeviceOrientation(AppiumDriver driver) {
        return driver.getOrientation();
    }

    public static void shake(AppiumDriver driver) {
        if (driver instanceof IOSDriver) {
            ((IOSDriver) driver).shake();
        } else {
            log.warn("shake() is iOS simulator only");
        }
    }

    public static String getDeviceTime(AppiumDriver driver) {
        return driver.getDeviceTime();
    }
}
```

- [ ] **Step 2: Create NetworkUtils**

```java
// core/src/main/java/com/align/utils/NetworkUtils.java
package com.align.utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.connection.ConnectionState;
import io.appium.java_client.android.connection.ConnectionStateBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkUtils {
    private static final Logger log = LoggerFactory.getLogger(NetworkUtils.class);

    private NetworkUtils() {}

    public static void toggleWifi(AppiumDriver driver, boolean enable) {
        requireAndroid(driver, "toggleWifi");
        AndroidDriver android = (AndroidDriver) driver;
        ConnectionState state = enable
                ? new ConnectionStateBuilder().withWiFiEnabled().build()
                : new ConnectionStateBuilder().withWiFiDisabled().build();
        android.setConnection(state);
        log.info("WiFi {}", enable ? "enabled" : "disabled");
    }

    public static void toggleMobileData(AppiumDriver driver, boolean enable) {
        requireAndroid(driver, "toggleMobileData");
        AndroidDriver android = (AndroidDriver) driver;
        ConnectionState state = enable
                ? new ConnectionStateBuilder().withDataEnabled().build()
                : new ConnectionStateBuilder().withDataDisabled().build();
        android.setConnection(state);
        log.info("Mobile data {}", enable ? "enabled" : "disabled");
    }

    public static void toggleAirplaneMode(AppiumDriver driver, boolean enable) {
        requireAndroid(driver, "toggleAirplaneMode");
        AndroidDriver android = (AndroidDriver) driver;
        ConnectionState state = enable
                ? new ConnectionStateBuilder().withAirplaneModeEnabled().build()
                : new ConnectionStateBuilder().withAirplaneModeDisabled().build();
        android.setConnection(state);
        log.info("Airplane mode {}", enable ? "enabled" : "disabled");
    }

    public static ConnectionState getNetworkConnection(AppiumDriver driver) {
        requireAndroid(driver, "getNetworkConnection");
        return ((AndroidDriver) driver).getConnection();
    }

    private static void requireAndroid(AppiumDriver driver, String method) {
        if (!(driver instanceof AndroidDriver)) {
            throw new UnsupportedOperationException(method + "() is Android-only");
        }
    }
}
```

- [ ] **Step 3: Compile**

```bash
mvn compile -pl core
```
Expected: `BUILD SUCCESS`

- [ ] **Step 4: Commit**

```bash
git add core/src/main/java/com/align/utils/DeviceUtils.java \
        core/src/main/java/com/align/utils/NetworkUtils.java
git commit -m "feat(core): add DeviceUtils (lock/rotate/shake) and NetworkUtils (wifi/data/airplane)"
```

---

## Task 17: ClipboardUtils + FileUtils

**Files:**
- Create: `core/src/main/java/com/align/utils/ClipboardUtils.java`
- Create: `core/src/main/java/com/align/utils/FileUtils.java`

- [ ] **Step 1: Create ClipboardUtils**

```java
// core/src/main/java/com/align/utils/ClipboardUtils.java
package com.align.utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ClipboardContentType;

public class ClipboardUtils {
    private ClipboardUtils() {}

    public static void setClipboard(AppiumDriver driver, String text) {
        driver.setClipboardText(text);
    }

    public static String getClipboard(AppiumDriver driver) {
        return driver.getClipboardText();
    }

    public static void clearClipboard(AppiumDriver driver) {
        driver.setClipboardText("");
    }
}
```

- [ ] **Step 2: Create FileUtils**

```java
// core/src/main/java/com/align/utils/FileUtils.java
package com.align.utils;

import io.appium.java_client.AppiumDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {
    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

    private FileUtils() {}

    public static void pushFileToDevice(AppiumDriver driver, String localPath, String devicePath) {
        try {
            byte[] data = Files.readAllBytes(Path.of(localPath));
            driver.pushFile(devicePath, data);
            log.info("Pushed file {} to device path {}", localPath, devicePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to push file: " + localPath, e);
        }
    }

    public static byte[] pullFileFromDevice(AppiumDriver driver, String devicePath) {
        byte[] data = driver.pullFile(devicePath);
        log.info("Pulled file from device path {}", devicePath);
        return data;
    }

    public static void pushMediaFile(AppiumDriver driver, String localPath) {
        // Pushes to /sdcard/Pictures on Android — triggers media scanner
        String fileName = new File(localPath).getName();
        pushFileToDevice(driver, localPath, "/sdcard/Pictures/" + fileName);
    }
}
```

- [ ] **Step 3: Compile**

```bash
mvn compile -pl core
```
Expected: `BUILD SUCCESS`

- [ ] **Step 4: Commit**

```bash
git add core/src/main/java/com/align/utils/ClipboardUtils.java \
        core/src/main/java/com/align/utils/FileUtils.java
git commit -m "feat(core): add ClipboardUtils and FileUtils"
```

---

## Task 18: BiometricUtils + DeepLinkUtils

**Files:**
- Create: `core/src/main/java/com/align/utils/BiometricUtils.java`
- Create: `core/src/main/java/com/align/utils/DeepLinkUtils.java`

- [ ] **Step 1: Create BiometricUtils**

```java
// core/src/main/java/com/align/utils/BiometricUtils.java
package com.align.utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class BiometricUtils {
    private static final Logger log = LoggerFactory.getLogger(BiometricUtils.class);

    private BiometricUtils() {}

    public static void simulateFingerprintSuccess(AppiumDriver driver) {
        requireAndroid(driver, "simulateFingerprintSuccess");
        ((AndroidDriver) driver).fingerPrint(1);
        log.info("Fingerprint success simulated (fingerprintId=1)");
    }

    public static void simulateFingerprintFailure(AppiumDriver driver) {
        requireAndroid(driver, "simulateFingerprintFailure");
        ((AndroidDriver) driver).fingerPrint(2);
        log.info("Fingerprint failure simulated (fingerprintId=2)");
    }

    public static void simulateFaceIdSuccess(AppiumDriver driver) {
        requireIOS(driver, "simulateFaceIdSuccess");
        ((IOSDriver) driver).executeScript("mobile: sendBiometricMatch",
                Map.of("type", "faceId", "match", true));
        log.info("Face ID success simulated");
    }

    public static void simulateFaceIdFailure(AppiumDriver driver) {
        requireIOS(driver, "simulateFaceIdFailure");
        ((IOSDriver) driver).executeScript("mobile: sendBiometricMatch",
                Map.of("type", "faceId", "match", false));
        log.info("Face ID failure simulated");
    }

    private static void requireAndroid(AppiumDriver driver, String method) {
        if (!(driver instanceof AndroidDriver)) {
            throw new UnsupportedOperationException(method + "() is Android emulator only");
        }
    }

    private static void requireIOS(AppiumDriver driver, String method) {
        if (!(driver instanceof IOSDriver)) {
            throw new UnsupportedOperationException(method + "() is iOS simulator only");
        }
    }
}
```

- [ ] **Step 2: Create DeepLinkUtils**

```java
// core/src/main/java/com/align/utils/DeepLinkUtils.java
package com.align.utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class DeepLinkUtils {
    private static final Logger log = LoggerFactory.getLogger(DeepLinkUtils.class);

    private DeepLinkUtils() {}

    public static void openDeepLink(AppiumDriver driver, String url) {
        log.info("Opening deep link: {}", url);
        if (driver instanceof AndroidDriver) {
            // Android: use adb intent
            driver.executeScript("mobile: deepLink", Map.of(
                    "url", url,
                    "package", getPackageName(driver)
            ));
        } else {
            // iOS: openUrl via XCUITest
            driver.executeScript("mobile: openUrl", Map.of("url", url));
        }
    }

    public static void openUniversalLink(AppiumDriver driver, String url) {
        log.info("Opening universal link: {}", url);
        driver.executeScript("mobile: openUrl", Map.of("url", url));
    }

    private static String getPackageName(AppiumDriver driver) {
        try {
            return (String) ((AndroidDriver) driver).getCurrentPackage();
        } catch (Exception e) {
            return "";
        }
    }
}
```

- [ ] **Step 3: Compile**

```bash
mvn compile -pl core
```
Expected: `BUILD SUCCESS`

- [ ] **Step 4: Commit**

```bash
git add core/src/main/java/com/align/utils/BiometricUtils.java \
        core/src/main/java/com/align/utils/DeepLinkUtils.java
git commit -m "feat(core): add BiometricUtils (fingerprint/faceId) and DeepLinkUtils"
```

---

## Task 19: ContextUtils + ElementUtils + NotificationUtils

**Files:**
- Create: `core/src/main/java/com/align/utils/ContextUtils.java`
- Create: `core/src/main/java/com/align/utils/ElementUtils.java`
- Create: `core/src/main/java/com/align/utils/NotificationUtils.java`

- [ ] **Step 1: Create ContextUtils**

```java
// core/src/main/java/com/align/utils/ContextUtils.java
package com.align.utils;

import io.appium.java_client.AppiumDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Set;

public class ContextUtils {
    private static final Logger log = LoggerFactory.getLogger(ContextUtils.class);
    private static final String NATIVE_APP = "NATIVE_APP";

    private ContextUtils() {}

    public static void switchToWebView(AppiumDriver driver) {
        waitForWebViewContext(driver, Duration.ofSeconds(10));
        Set<String> contexts = driver.getContextHandles();
        String webViewContext = contexts.stream()
                .filter(c -> c.startsWith("WEBVIEW"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No WebView context found. Available: " + contexts));
        driver.context(webViewContext);
        log.info("Switched to context: {}", webViewContext);
    }

    public static void switchToNative(AppiumDriver driver) {
        driver.context(NATIVE_APP);
        log.info("Switched to NATIVE_APP context");
    }

    public static List<String> getAvailableContexts(AppiumDriver driver) {
        return List.copyOf(driver.getContextHandles());
    }

    public static String getCurrentContext(AppiumDriver driver) {
        return driver.getContext();
    }

    public static void waitForWebViewContext(AppiumDriver driver, Duration timeout) {
        long deadline = System.currentTimeMillis() + timeout.toMillis();
        while (System.currentTimeMillis() < deadline) {
            boolean found = driver.getContextHandles().stream()
                    .anyMatch(c -> c.startsWith("WEBVIEW"));
            if (found) return;
            try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        }
        throw new RuntimeException("WebView context not available after " + timeout.getSeconds() + "s");
    }
}
```

- [ ] **Step 2: Create ElementUtils**

```java
// core/src/main/java/com/align/utils/ElementUtils.java
package com.align.utils;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

import java.util.List;

public class ElementUtils {
    private ElementUtils() {}

    public static String getAttribute(AppiumDriver driver, By locator, String attribute) {
        return WaitUtils.waitForVisible(driver, locator, 10).getAttribute(attribute);
    }

    public static String getText(AppiumDriver driver, By locator) {
        return WaitUtils.waitForVisible(driver, locator, 10).getText();
    }

    public static boolean isEnabled(AppiumDriver driver, By locator) {
        try {
            return WaitUtils.waitForVisible(driver, locator, 5).isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isSelected(AppiumDriver driver, By locator) {
        try {
            return WaitUtils.waitForVisible(driver, locator, 5).isSelected();
        } catch (Exception e) {
            return false;
        }
    }

    public static void highlightElement(AppiumDriver driver, By locator) {
        WebElement element = WaitUtils.waitForVisible(driver, locator, 10);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].style.border='3px solid red'", element);
    }

    public static Point getElementLocation(AppiumDriver driver, By locator) {
        return WaitUtils.waitForVisible(driver, locator, 10).getLocation();
    }

    public static Dimension getElementSize(AppiumDriver driver, By locator) {
        return WaitUtils.waitForVisible(driver, locator, 10).getSize();
    }

    public static int getElementCount(AppiumDriver driver, By locator) {
        List<WebElement> elements = driver.findElements(locator);
        return elements.size();
    }
}
```

- [ ] **Step 3: Create NotificationUtils**

```java
// core/src/main/java/com/align/utils/NotificationUtils.java
package com.align.utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class NotificationUtils {
    private static final Logger log = LoggerFactory.getLogger(NotificationUtils.class);

    private NotificationUtils() {}

    public static void openNotificationCenter(AppiumDriver driver) {
        requireAndroid(driver, "openNotificationCenter");
        ((AndroidDriver) driver).openNotifications();
        log.info("Notification center opened");
    }

    public static void clearNotifications(AppiumDriver driver) {
        requireAndroid(driver, "clearNotifications");
        ((AndroidDriver) driver).openNotifications();
        By clearAll = By.id("com.android.systemui:id/dismiss_text");
        if (WaitUtils.isVisible(driver, clearAll, 3)) {
            driver.findElement(clearAll).click();
        }
        log.info("Notifications cleared");
    }

    public static List<String> getNotificationText(AppiumDriver driver) {
        requireAndroid(driver, "getNotificationText");
        ((AndroidDriver) driver).openNotifications();
        By notifTitle = By.id("android:id/title");
        List<WebElement> notifications = driver.findElements(notifTitle);
        return notifications.stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    private static void requireAndroid(AppiumDriver driver, String method) {
        if (!(driver instanceof AndroidDriver)) {
            throw new UnsupportedOperationException(method + "() is Android-only");
        }
    }
}
```

- [ ] **Step 4: Compile**

```bash
mvn compile -pl core
```
Expected: `BUILD SUCCESS`

- [ ] **Step 5: Commit**

```bash
git add core/src/main/java/com/align/utils/ContextUtils.java \
        core/src/main/java/com/align/utils/ElementUtils.java \
        core/src/main/java/com/align/utils/NotificationUtils.java
git commit -m "feat(core): add ContextUtils, ElementUtils, NotificationUtils"
```

---

## Task 20: ScreenshotUtils + VideoUtils

**Files:**
- Create: `core/src/main/java/com/align/utils/ScreenshotUtils.java`
- Create: `core/src/main/java/com/align/utils/VideoUtils.java`

- [ ] **Step 1: Create ScreenshotUtils**

```java
// core/src/main/java/com/align/utils/ScreenshotUtils.java
package com.align.utils;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotUtils {
    private static final Logger log = LoggerFactory.getLogger(ScreenshotUtils.class);
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private ScreenshotUtils() {}

    public static byte[] capture(AppiumDriver driver) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    public static Path captureAndSave(AppiumDriver driver, String testName) {
        try {
            byte[] data = capture(driver);
            Path dir = Paths.get("target", "screenshots");
            Files.createDirectories(dir);
            String fileName = testName + "_" + LocalDateTime.now().format(TS) + ".png";
            Path filePath = dir.resolve(fileName);
            Files.write(filePath, data);
            log.info("Screenshot saved: {}", filePath);
            return filePath;
        } catch (IOException e) {
            log.error("Failed to save screenshot", e);
            return null;
        }
    }
}
```

- [ ] **Step 2: Create VideoUtils**

```java
// core/src/main/java/com/align/utils/VideoUtils.java
package com.align.utils;

import com.align.config.ConfigLoader;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.screenrecording.AndroidStartScreenRecordingOptions;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.screenrecording.IOSStartScreenRecordingOptions;
import io.appium.java_client.screenrecording.CanRecordScreen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class VideoUtils {
    private static final Logger log = LoggerFactory.getLogger(VideoUtils.class);
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private VideoUtils() {}

    public static boolean isEnabled() {
        String env = ConfigLoader.getInstance().getProperty("env", "local");
        if ("cloud".equalsIgnoreCase(env)) return false;
        return ConfigLoader.getInstance().getBooleanProperty("video.recording.enabled", false);
    }

    public static void startRecording(AppiumDriver driver) {
        if (!(driver instanceof CanRecordScreen)) {
            log.warn("Driver does not support screen recording");
            return;
        }
        try {
            if (driver instanceof AndroidDriver) {
                ((AndroidDriver) driver).startRecordingScreen(
                        new AndroidStartScreenRecordingOptions()
                                .withBitRate(4_000_000)
                                .withTimeLimit(Duration.ofMinutes(30)));
            } else if (driver instanceof IOSDriver) {
                ((IOSDriver) driver).startRecordingScreen(
                        new IOSStartScreenRecordingOptions()
                                .withVideoQuality(IOSStartScreenRecordingOptions.VideoQuality.MEDIUM)
                                .withTimeLimit(Duration.ofMinutes(30)));
            }
            log.debug("Screen recording started");
        } catch (Exception e) {
            log.warn("Failed to start screen recording: {}", e.getMessage());
        }
    }

    public static Path stopAndSave(AppiumDriver driver, String testName) {
        try {
            String base64Video = ((CanRecordScreen) driver).stopRecordingScreen();
            byte[] videoBytes = Base64.getDecoder().decode(base64Video);
            Path dir = Paths.get("target", "videos");
            Files.createDirectories(dir);
            String fileName = testName + "_" + LocalDateTime.now().format(TS) + ".mp4";
            Path filePath = dir.resolve(fileName);
            Files.write(filePath, videoBytes);
            log.info("Video saved: {}", filePath);
            return filePath;
        } catch (Exception e) {
            log.warn("Failed to save video: {}", e.getMessage());
            return null;
        }
    }

    public static void stopAndDiscard(AppiumDriver driver) {
        try {
            ((CanRecordScreen) driver).stopRecordingScreen();
            log.debug("Screen recording discarded");
        } catch (Exception e) {
            log.warn("Failed to stop screen recording: {}", e.getMessage());
        }
    }
}
```

- [ ] **Step 3: Compile**

```bash
mvn compile -pl core
```
Expected: `BUILD SUCCESS`

- [ ] **Step 4: Commit**

```bash
git add core/src/main/java/com/align/utils/ScreenshotUtils.java \
        core/src/main/java/com/align/utils/VideoUtils.java
git commit -m "feat(core): add ScreenshotUtils and VideoUtils with configurable recording"
```

---

## Task 21: ExtentManager

**Files:**
- Create: `core/src/main/java/com/align/reporting/ExtentManager.java`

- [ ] **Step 1: Create ExtentManager**

```java
// core/src/main/java/com/align/reporting/ExtentManager.java
package com.align.reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtentManager {
    private static final Logger log = LoggerFactory.getLogger(ExtentManager.class);
    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> testThread = new ThreadLocal<>();

    private ExtentManager() {}

    public static synchronized ExtentReports getReports() {
        if (extent == null) {
            ExtentSparkReporter spark = new ExtentSparkReporter("target/extent-reports/report.html");
            spark.config().setTheme(Theme.DARK);
            spark.config().setDocumentTitle("AlignApp Test Report");
            spark.config().setReportName("Mobile Automation Report");

            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("Framework", "AlignAppiumFramework");
            extent.setSystemInfo("Appium", "9.x");
            log.info("ExtentReports initialized at target/extent-reports/report.html");
        }
        return extent;
    }

    public static ExtentTest createTest(String testName, String description) {
        ExtentTest test = getReports().createTest(testName, description);
        testThread.set(test);
        return test;
    }

    public static ExtentTest getTest() {
        return testThread.get();
    }

    public static void removeTest() {
        testThread.remove();
    }

    public static synchronized void flush() {
        if (extent != null) {
            extent.flush();
            log.info("ExtentReports flushed");
        }
    }
}
```

- [ ] **Step 2: Compile**

```bash
mvn compile -pl core
```
Expected: `BUILD SUCCESS`

- [ ] **Step 3: Commit**

```bash
git add core/src/main/java/com/align/reporting/ExtentManager.java
git commit -m "feat(core): add ExtentManager singleton with ThreadLocal ExtentTest"
```

---

## Task 22: TestListener + DriverListener + log4j2.xml

**Files:**
- Create: `core/src/main/java/com/align/listeners/TestListener.java`
- Create: `core/src/main/java/com/align/listeners/DriverListener.java`
- Create: `core/src/main/resources/log4j2.xml`

- [ ] **Step 1: Create log4j2.xml**

```xml
<!-- core/src/main/resources/log4j2.xml -->
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
    <Properties>
        <Property name="LOG_DIR">target/logs</Property>
        <Property name="LOG_LEVEL">${sys:log.level:-INFO}</Property>
    </Properties>

    <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
        </Console>
        <RollingFile name="FileAppender"
                     fileName="${LOG_DIR}/align-${sys:platform:-android}-${date:yyyyMMdd_HHmmss}.log"
                     filePattern="${LOG_DIR}/align-%d{yyyy-MM-dd}.log.gz">
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
            <Policies>
                <TimeBasedTriggeringPolicy/>
                <SizeBasedTriggeringPolicy size="50MB"/>
            </Policies>
        </RollingFile>
    </Appenders>

    <Loggers>
        <Root level="${LOG_LEVEL}">
            <AppenderRef ref="Console"/>
            <AppenderRef ref="FileAppender"/>
        </Root>
        <Logger name="com.align" level="${LOG_LEVEL}" additivity="false">
            <AppenderRef ref="Console"/>
            <AppenderRef ref="FileAppender"/>
        </Logger>
    </Loggers>
</Configuration>
```

- [ ] **Step 2: Create TestListener**

```java
// core/src/main/java/com/align/listeners/TestListener.java
package com.align.listeners;

import com.align.driver.DriverManager;
import com.align.reporting.ExtentManager;
import com.align.utils.ScreenshotUtils;
import com.align.utils.VideoUtils;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import io.qameta.allure.Allure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.util.Base64;

public class TestListener implements ITestListener {
    private static final Logger log = LoggerFactory.getLogger(TestListener.class);

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription();
        log.info("Starting test: {}", testName);
        ExtentManager.createTest(testName, description);
        if (VideoUtils.isEnabled() && DriverManager.hasDriver()) {
            VideoUtils.startRecording(DriverManager.getDriver());
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        log.info("PASS: {}", testName);
        ExtentTest test = ExtentManager.getTest();
        if (test != null) test.pass("Test passed");

        if (VideoUtils.isEnabled() && DriverManager.hasDriver()) {
            boolean saveOnPass = Boolean.parseBoolean(
                    System.getProperty("video.save.on.pass", "false"));
            if (saveOnPass) {
                VideoUtils.stopAndSave(DriverManager.getDriver(), testName);
            } else {
                VideoUtils.stopAndDiscard(DriverManager.getDriver());
            }
        }
        ExtentManager.removeTest();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        log.error("FAIL: {} — {}", testName, result.getThrowable().getMessage());

        if (DriverManager.hasDriver()) {
            // Single screenshot capture, attach to both reporters
            byte[] screenshot = ScreenshotUtils.capture(DriverManager.getDriver());

            // Allure attachment
            Allure.addAttachment("Screenshot on Failure",
                    "image/png", new ByteArrayInputStream(screenshot), "png");

            // Extent attachment
            ExtentTest test = ExtentManager.getTest();
            if (test != null) {
                try {
                    test.fail(result.getThrowable(),
                            MediaEntityBuilder.createScreenCaptureFromBase64String(
                                    Base64.getEncoder().encodeToString(screenshot)).build());
                } catch (Exception e) {
                    log.warn("Failed to attach screenshot to Extent: {}", e.getMessage());
                }
            }

            // Video
            if (VideoUtils.isEnabled()) {
                Path videoPath = VideoUtils.stopAndSave(DriverManager.getDriver(), testName);
                if (videoPath != null && test != null) {
                    test.info("Video: " + videoPath.toAbsolutePath());
                }
            }
        }
        ExtentManager.removeTest();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        log.warn("SKIP: {}", testName);
        ExtentTest test = ExtentManager.getTest();
        if (test != null) {
            test.skip(result.getThrowable() != null
                    ? result.getThrowable().getMessage() : "Skipped");
        }
        ExtentManager.removeTest();
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentManager.flush();
        log.info("Suite finished: passed={}, failed={}, skipped={}",
                context.getPassedTests().size(),
                context.getFailedTests().size(),
                context.getSkippedTests().size());
    }
}
```

- [ ] **Step 3: Create DriverListener**

```java
// core/src/main/java/com/align/listeners/DriverListener.java
package com.align.listeners;

import com.align.driver.DriverManager;
import com.align.utils.ScreenshotUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

public class DriverListener extends TestListenerAdapter {
    private static final Logger log = LoggerFactory.getLogger(DriverListener.class);

    @Override
    public void onTestFailure(ITestResult result) {
        if (DriverManager.hasDriver()) {
            log.debug("DriverListener: capturing screenshot for failed test {}",
                    result.getMethod().getMethodName());
            ScreenshotUtils.captureAndSave(DriverManager.getDriver(),
                    result.getMethod().getMethodName());
        }
    }
}
```

- [ ] **Step 4: Compile**

```bash
mvn compile -pl core
```
Expected: `BUILD SUCCESS`

- [ ] **Step 5: Commit**

```bash
git add core/src/main/resources/log4j2.xml \
        core/src/main/java/com/align/listeners/
git commit -m "feat(core): add TestListener (Allure+Extent+video) and DriverListener, log4j2 config"
```

---

## Task 23: Page Interfaces + PageFactory

**Files:**
- Create: `tests/src/main/java/com/align/pages/LoginPage.java`
- Create: `tests/src/main/java/com/align/factory/PageFactory.java`

- [ ] **Step 1: Create LoginPage interface**

```java
// tests/src/main/java/com/align/pages/LoginPage.java
package com.align.pages;

public interface LoginPage {
    void enterUsername(String username);
    void enterPassword(String password);
    void tapLogin();
    boolean isLoginPageVisible();
    String getErrorMessage();
}
```

- [ ] **Step 2: Create PageFactory**

```java
// tests/src/main/java/com/align/factory/PageFactory.java
package com.align.factory;

import com.align.config.ConfigLoader;
import com.align.pages.LoginPage;

public class PageFactory {
    private PageFactory() {}

    private static String getPlatform() {
        return ConfigLoader.getInstance().getProperty("platform", "android");
    }

    public static LoginPage getLoginPage() {
        return create("LoginPage");
    }

    @SuppressWarnings("unchecked")
    private static <T> T create(String pageName) {
        String platform = getPlatform();
        String className = "com.align." + platform.toLowerCase() + ".pages."
                + platform.substring(0, 1).toUpperCase() + platform.substring(1).toLowerCase()
                + pageName;
        try {
            return (T) Class.forName(className).getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Cannot create page " + className
                    + " for platform: " + platform, e);
        }
    }
}
```

- [ ] **Step 3: Compile**

```bash
mvn compile -pl tests
```
Expected: `BUILD SUCCESS`

- [ ] **Step 4: Commit**

```bash
git add tests/src/main/java/com/align/pages/ \
        tests/src/main/java/com/align/factory/
git commit -m "feat(tests): add LoginPage interface and PageFactory platform router"
```

---

## Task 24: Android + iOS LoginPage Implementations

**Files:**
- Create: `android/src/main/java/com/align/android/pages/AndroidLoginPage.java`
- Create: `ios/src/main/java/com/align/ios/pages/IOSLoginPage.java`

- [ ] **Step 1: Create AndroidLoginPage**

```java
// android/src/main/java/com/align/android/pages/AndroidLoginPage.java
package com.align.android.pages;

import com.align.pages.BasePage;
import com.align.pages.LoginPage;
import org.openqa.selenium.By;

public class AndroidLoginPage extends BasePage implements LoginPage {

    // Locators — update to match actual app resource IDs
    private static final By USERNAME_FIELD  = By.id("com.align.app:id/et_username");
    private static final By PASSWORD_FIELD  = By.id("com.align.app:id/et_password");
    private static final By LOGIN_BUTTON    = By.id("com.align.app:id/btn_login");
    private static final By ERROR_MESSAGE   = By.id("com.align.app:id/tv_error");
    private static final By LOGIN_PAGE_ROOT = By.id("com.align.app:id/login_root");

    @Override
    public void enterUsername(String username) {
        sendKeys(USERNAME_FIELD, username);
    }

    @Override
    public void enterPassword(String password) {
        sendKeys(PASSWORD_FIELD, password);
    }

    @Override
    public void tapLogin() {
        tap(LOGIN_BUTTON);
    }

    @Override
    public boolean isLoginPageVisible() {
        return isElementVisible(LOGIN_PAGE_ROOT);
    }

    @Override
    public String getErrorMessage() {
        return getText(ERROR_MESSAGE);
    }
}
```

- [ ] **Step 2: Create IOSLoginPage**

```java
// ios/src/main/java/com/align/ios/pages/IOSLoginPage.java
package com.align.ios.pages;

import com.align.pages.BasePage;
import com.align.pages.LoginPage;
import org.openqa.selenium.By;

public class IOSLoginPage extends BasePage implements LoginPage {

    // Locators — update to match actual app accessibility IDs
    private static final By USERNAME_FIELD  = By.xpath("//XCUIElementTypeTextField[@name='username']");
    private static final By PASSWORD_FIELD  = By.xpath("//XCUIElementTypeSecureTextField[@name='password']");
    private static final By LOGIN_BUTTON    = By.xpath("//XCUIElementTypeButton[@name='Login']");
    private static final By ERROR_MESSAGE   = By.xpath("//XCUIElementTypeStaticText[@name='error_message']");
    private static final By LOGIN_PAGE_ROOT = By.xpath("//XCUIElementTypeOther[@name='login_screen']");

    @Override
    public void enterUsername(String username) {
        sendKeys(USERNAME_FIELD, username);
    }

    @Override
    public void enterPassword(String password) {
        sendKeys(PASSWORD_FIELD, password);
    }

    @Override
    public void tapLogin() {
        tap(LOGIN_BUTTON);
    }

    @Override
    public boolean isLoginPageVisible() {
        return isElementVisible(LOGIN_PAGE_ROOT);
    }

    @Override
    public String getErrorMessage() {
        return getText(ERROR_MESSAGE);
    }
}
```

- [ ] **Step 3: Compile**

```bash
mvn compile -pl android,ios
```
Expected: `BUILD SUCCESS`

- [ ] **Step 4: Commit**

```bash
git add android/src/main/java/com/align/android/pages/ \
        ios/src/main/java/com/align/ios/pages/
git commit -m "feat: add AndroidLoginPage and IOSLoginPage implementations"
```

---

## Task 25: BaseTest + TestNG Suite XMLs

**Files:**
- Create: `tests/src/test/java/com/align/tests/BaseTest.java`
- Create: `tests/src/test/resources/suites/android-suite.xml`
- Create: `tests/src/test/resources/suites/ios-suite.xml`
- Create: `tests/src/test/resources/suites/parallel-suite.xml`

- [ ] **Step 1: Create BaseTest**

```java
// tests/src/test/java/com/align/tests/BaseTest.java
package com.align.tests;

import com.align.config.ConfigLoader;
import com.align.config.DeviceConfig;
import com.align.driver.DriverFactory;
import com.align.driver.DriverManager;
import com.align.driver.ServerManager;
import com.align.utils.AppResolver;
import io.appium.java_client.AppiumDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;

import java.lang.reflect.Method;

public class BaseTest {
    private static final Logger log = LoggerFactory.getLogger(BaseTest.class);

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        DeviceConfig config = DeviceConfig.fromConfig();
        if (config.isAutoStartServer() && !config.isCloud()) {
            ServerManager.startServer();
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) {
        log.info("Setting up test: {}.{}", getClass().getSimpleName(), method.getName());
        DeviceConfig config = buildDeviceConfig();
        AppiumDriver driver = DriverFactory.createDriver(config);
        DriverManager.setDriver(driver);
        log.info("Driver ready — platform: {}, device: {}", config.getPlatform(), config.getDeviceName());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(Method method) {
        log.info("Tearing down test: {}", method.getName());
        if (DriverManager.hasDriver()) {
            try {
                DriverManager.getDriver().quit();
            } catch (Exception e) {
                log.warn("Driver quit failed: {}", e.getMessage());
            } finally {
                DriverManager.removeDriver();
            }
        }
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        if (ServerManager.isRunning()) {
            ServerManager.stopServer();
        }
    }

    private DeviceConfig buildDeviceConfig() {
        ConfigLoader cfg = ConfigLoader.getInstance();
        String appPath = cfg.getProperty("app.path", "");
        if (!appPath.isEmpty()) {
            appPath = AppResolver.resolve(appPath);
        }
        return DeviceConfig.builder()
                .platform(cfg.getProperty("platform", "android"))
                .deviceName(cfg.getProperty("device.name", ""))
                .udid(cfg.getProperty("device.udid", ""))
                .appPath(appPath)
                .platformVersion(cfg.getProperty("platform.version", ""))
                .executionEnv(cfg.getProperty("env", "local"))
                .cloudUrl(cfg.getProperty("cloud.url", ""))
                .cloudKey(cfg.getProperty("cloud.key", ""))
                .autoStartServer(cfg.getBooleanProperty("auto.start.server", false))
                .build();
    }
}
```

- [ ] **Step 2: Create android-suite.xml**

```xml
<!-- tests/src/test/resources/suites/android-suite.xml -->
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="Android Suite" verbose="1" parallel="methods" thread-count="2">
    <listeners>
        <listener class-name="com.align.listeners.TestListener"/>
        <listener class-name="com.align.listeners.DriverListener"/>
    </listeners>
    <parameter name="platform" value="android"/>
    <test name="Android Tests">
        <classes>
            <class name="com.align.tests.LoginTest"/>
        </classes>
    </test>
</suite>
```

- [ ] **Step 3: Create ios-suite.xml**

```xml
<!-- tests/src/test/resources/suites/ios-suite.xml -->
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="iOS Suite" verbose="1" parallel="methods" thread-count="2">
    <listeners>
        <listener class-name="com.align.listeners.TestListener"/>
        <listener class-name="com.align.listeners.DriverListener"/>
    </listeners>
    <parameter name="platform" value="ios"/>
    <test name="iOS Tests">
        <classes>
            <class name="com.align.tests.LoginTest"/>
        </classes>
    </test>
</suite>
```

- [ ] **Step 4: Create parallel-suite.xml**

```xml
<!-- tests/src/test/resources/suites/parallel-suite.xml -->
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="Parallel Suite" verbose="1" parallel="tests" thread-count="4">
    <listeners>
        <listener class-name="com.align.listeners.TestListener"/>
        <listener class-name="com.align.listeners.DriverListener"/>
    </listeners>

    <test name="Android Tests" parallel="methods" thread-count="2">
        <parameter name="platform" value="android"/>
        <classes>
            <class name="com.align.tests.LoginTest"/>
        </classes>
    </test>

    <test name="iOS Tests" parallel="methods" thread-count="2">
        <parameter name="platform" value="ios"/>
        <classes>
            <class name="com.align.tests.LoginTest"/>
        </classes>
    </test>
</suite>
```

- [ ] **Step 5: Compile tests module**

```bash
mvn compile -pl tests
```
Expected: `BUILD SUCCESS`

- [ ] **Step 6: Commit**

```bash
git add tests/src/test/java/com/align/tests/BaseTest.java \
        tests/src/test/resources/suites/
git commit -m "feat(tests): add BaseTest with driver lifecycle and TestNG suite XMLs"
```

---

## Task 26: Sample LoginTest

**Files:**
- Create: `tests/src/test/java/com/align/tests/LoginTest.java`

- [ ] **Step 1: Create LoginTest**

```java
// tests/src/test/java/com/align/tests/LoginTest.java
package com.align.tests;

import com.align.factory.PageFactory;
import com.align.pages.LoginPage;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    @Test(description = "Valid credentials navigate to home screen")
    @Description("Logs in with valid credentials and verifies navigation to home screen")
    public void validLoginNavigatesToHome() {
        LoginPage loginPage = PageFactory.getLoginPage();
        Assert.assertTrue(loginPage.isLoginPageVisible(), "Login page should be visible");
        performLogin(loginPage, "testuser@align.com", "Password123!");
        // Add assertion for home screen visibility once HomePage interface is created
        // Example: Assert.assertTrue(PageFactory.getHomePage().isHomePageVisible());
    }

    @Test(description = "Invalid credentials show error message")
    @Description("Verifies error message appears for invalid credentials")
    public void invalidCredentialsShowError() {
        LoginPage loginPage = PageFactory.getLoginPage();
        Assert.assertTrue(loginPage.isLoginPageVisible(), "Login page should be visible");
        performLogin(loginPage, "wrong@align.com", "wrongpass");
        String error = loginPage.getErrorMessage();
        Assert.assertFalse(error.isEmpty(), "Error message should be visible");
    }

    @Step("Enter username: {username}, tap login")
    private void performLogin(LoginPage loginPage, String username, String password) {
        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
        loginPage.tapLogin();
    }
}
```

- [ ] **Step 2: Verify tests module compiles fully**

```bash
mvn test-compile -pl tests
```
Expected: `BUILD SUCCESS`

- [ ] **Step 3: Commit**

```bash
git add tests/src/test/java/com/align/tests/LoginTest.java
git commit -m "feat(tests): add LoginTest with valid/invalid credential scenarios"
```

---

## Task 27: GitHub Actions Workflows + Jenkinsfile

**Files:**
- Create: `.github/workflows/android-tests.yml`
- Create: `.github/workflows/ios-tests.yml`
- Create: `Jenkinsfile`

- [ ] **Step 1: Create android-tests.yml**

```yaml
# .github/workflows/android-tests.yml
name: Android Tests

on:
  push:
    branches: [ main, feature/** ]
  pull_request:
    branches: [ main ]
  workflow_dispatch:
    inputs:
      execution_env:
        description: 'Execution environment'
        required: true
        default: 'cloud'
        type: choice
        options: [local, cloud]
      parallel:
        description: 'Run tests in parallel'
        required: false
        default: 'true'
        type: boolean
      video_recording:
        description: 'Enable video recording'
        required: false
        default: 'false'
        type: boolean
      app_path:
        description: 'App binary path or cloud ID (e.g., bs://abc123)'
        required: false
        default: ''
      app_url:
        description: 'URL to download APK from (optional)'
        required: false
        default: ''
      build_name:
        description: 'Build name for cloud reporting'
        required: false
        default: 'GitHub-Actions'

jobs:
  android-tests:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: maven

      - name: Build (skip tests)
        run: mvn install -DskipTests -pl core,android,ios,tests

      - name: Run Android Tests
        env:
          CLOUD_URL: ${{ secrets.BROWSERSTACK_URL }}
          CLOUD_KEY: ${{ secrets.BROWSERSTACK_KEY }}
          BUILD_NAME: ${{ github.event.inputs.build_name || github.sha }}
        run: |
          mvn test -pl tests \
            -Dplatform=android \
            -Denv=${{ github.event.inputs.execution_env || 'cloud' }} \
            -Dsuite=src/test/resources/suites/android-suite.xml \
            -Dvideo.recording.enabled=${{ github.event.inputs.video_recording || 'false' }} \
            -Dapp.path="${{ github.event.inputs.app_path }}" \
            -Dapp.url="${{ github.event.inputs.app_url }}"

      - name: Generate Allure Report
        if: always()
        run: mvn allure:report -pl tests

      - name: Upload Allure Results
        uses: actions/upload-artifact@v4
        if: always()
        with:
          name: allure-results-android
          path: tests/target/allure-results/

      - name: Upload Extent Report
        uses: actions/upload-artifact@v4
        if: always()
        with:
          name: extent-report-android
          path: tests/target/extent-reports/

      - name: Upload Videos
        uses: actions/upload-artifact@v4
        if: failure()
        with:
          name: test-videos-android
          path: tests/target/videos/
```

- [ ] **Step 2: Create ios-tests.yml**

```yaml
# .github/workflows/ios-tests.yml
name: iOS Tests

on:
  push:
    branches: [ main, feature/** ]
  pull_request:
    branches: [ main ]
  workflow_dispatch:
    inputs:
      execution_env:
        description: 'Execution environment'
        required: true
        default: 'cloud'
        type: choice
        options: [local, cloud]
      video_recording:
        description: 'Enable video recording'
        required: false
        default: 'false'
        type: boolean
      app_path:
        description: 'App binary path or cloud ID (e.g., bs://abc123)'
        required: false
        default: ''
      app_url:
        description: 'URL to download IPA from (optional)'
        required: false
        default: ''
      build_name:
        description: 'Build name for cloud reporting'
        required: false
        default: 'GitHub-Actions'

jobs:
  ios-tests:
    runs-on: macos-latest

    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: maven

      - name: Build (skip tests)
        run: mvn install -DskipTests -pl core,android,ios,tests

      - name: Run iOS Tests
        env:
          CLOUD_URL: ${{ secrets.BROWSERSTACK_URL }}
          CLOUD_KEY: ${{ secrets.BROWSERSTACK_KEY }}
          BUILD_NAME: ${{ github.event.inputs.build_name || github.sha }}
        run: |
          mvn test -pl tests \
            -Dplatform=ios \
            -Denv=${{ github.event.inputs.execution_env || 'cloud' }} \
            -Dsuite=src/test/resources/suites/ios-suite.xml \
            -Dvideo.recording.enabled=${{ github.event.inputs.video_recording || 'false' }} \
            -Dapp.path="${{ github.event.inputs.app_path }}" \
            -Dapp.url="${{ github.event.inputs.app_url }}"

      - name: Generate Allure Report
        if: always()
        run: mvn allure:report -pl tests

      - name: Upload Allure Results
        uses: actions/upload-artifact@v4
        if: always()
        with:
          name: allure-results-ios
          path: tests/target/allure-results/

      - name: Upload Extent Report
        uses: actions/upload-artifact@v4
        if: always()
        with:
          name: extent-report-ios
          path: tests/target/extent-reports/

      - name: Upload Videos
        uses: actions/upload-artifact@v4
        if: failure()
        with:
          name: test-videos-ios
          path: tests/target/videos/
```

- [ ] **Step 3: Create Jenkinsfile**

```groovy
// Jenkinsfile
pipeline {
    agent any

    tools {
        jdk 'JDK17'
        maven 'Maven3'
    }

    parameters {
        choice(name: 'PLATFORM', choices: ['android', 'ios', 'both'],
               description: 'Target platform')
        choice(name: 'ENV', choices: ['cloud', 'local'],
               description: 'Execution environment')
        booleanParam(name: 'PARALLEL', defaultValue: true,
                     description: 'Run tests in parallel')
        booleanParam(name: 'VIDEO_RECORDING', defaultValue: false,
                     description: 'Enable video recording')
        string(name: 'APP_PATH', defaultValue: '',
               description: 'App binary path or cloud app ID (bs://...)')
        string(name: 'APP_URL', defaultValue: '',
               description: 'Download URL for app binary (optional)')
        string(name: 'BUILD_NAME', defaultValue: "${env.BUILD_TAG}",
               description: 'Build label for cloud reports')
    }

    environment {
        CLOUD_URL = credentials('browserstack-url')
        CLOUD_KEY = credentials('browserstack-key')
        BUILD_NAME = "${params.BUILD_NAME ?: env.BUILD_TAG}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn install -DskipTests -pl core,android,ios,tests'
            }
        }

        stage('Test') {
            steps {
                script {
                    def suite = params.PARALLEL
                            ? 'src/test/resources/suites/parallel-suite.xml'
                            : "src/test/resources/suites/${params.PLATFORM}-suite.xml"

                    if (params.PLATFORM == 'both' && !params.PARALLEL) {
                        // Sequential: run android then ios
                        runTests('android', 'src/test/resources/suites/android-suite.xml')
                        runTests('ios', 'src/test/resources/suites/ios-suite.xml')
                    } else {
                        runTests(params.PLATFORM, suite)
                    }
                }
            }
        }

        stage('Reports') {
            steps {
                sh 'mvn allure:report -pl tests'
                publishHTML(target: [
                    allowMissing: true,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'tests/target/extent-reports',
                    reportFiles: 'report.html',
                    reportName: 'Extent Report'
                ])
                allure([
                    includeProperties: false,
                    jdk: '',
                    properties: [],
                    reportBuildPolicy: 'ALWAYS',
                    results: [[path: 'tests/target/allure-results']]
                ])
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'tests/target/videos/**/*.mp4',
                             allowEmptyArchive: true
            archiveArtifacts artifacts: 'tests/target/screenshots/**/*.png',
                             allowEmptyArchive: true
            archiveArtifacts artifacts: 'tests/target/logs/**/*.log',
                             allowEmptyArchive: true
        }
    }
}

def runTests(String platform, String suite) {
    sh """
        mvn test -pl tests \\
          -Dplatform=${platform} \\
          -Denv=${params.ENV} \\
          -Dsuite=${suite} \\
          -Dvideo.recording.enabled=${params.VIDEO_RECORDING} \\
          -Dapp.path="${params.APP_PATH}" \\
          -Dapp.url="${params.APP_URL}"
    """
}
```

- [ ] **Step 4: Create .github/workflows directory and commit**

```bash
mkdir -p .github/workflows
git add .github/workflows/android-tests.yml \
        .github/workflows/ios-tests.yml \
        Jenkinsfile
git commit -m "ci: add GitHub Actions workflows and Jenkinsfile for Android + iOS test execution"
```

---

## Task 28: Final Verification

- [ ] **Step 1: Full project compile**

```bash
mvn compile -pl core,android,ios,tests
```
Expected: `BUILD SUCCESS`

- [ ] **Step 2: Run unit tests**

```bash
mvn test -pl core -Dtest="ConfigLoaderTest,DriverManagerTest,AppResolverTest"
```
Expected: `Tests run: 8, Failures: 0, Errors: 0, Skipped: 0`

- [ ] **Step 3: Verify test module compiles**

```bash
mvn test-compile -pl tests
```
Expected: `BUILD SUCCESS`

- [ ] **Step 4: Final commit**

```bash
git add .
git commit -m "chore: finalize framework scaffold — all modules compile, unit tests pass"
```

---

## Running Tests

**Android local:**
```bash
mvn test -pl tests \
  -Dplatform=android \
  -Denv=local \
  -Ddevice.name="Pixel_7_API_33" \
  -Dapp.path=/path/to/align.apk \
  -Dsuite=src/test/resources/suites/android-suite.xml
```

**iOS local:**
```bash
mvn test -pl tests \
  -Dplatform=ios \
  -Denv=local \
  -Ddevice.name="iPhone 15" \
  -Dapp.path=/path/to/align.app \
  -Dsuite=src/test/resources/suites/ios-suite.xml
```

**Both platforms parallel (cloud):**
```bash
mvn test -pl tests \
  -Dplatform=both \
  -Denv=cloud \
  -Dapp.path=bs://your-app-id \
  -Dsuite=src/test/resources/suites/parallel-suite.xml \
  -Dvideo.recording.enabled=false
```

**With video recording:**
```bash
mvn test -pl tests \
  -Dplatform=android \
  -Denv=local \
  -Dvideo.recording.enabled=true \
  -Dvideo.save.on.pass=false \
  -Dsuite=src/test/resources/suites/android-suite.xml
```

---

## Locator Update Guide

When actual app locator IDs are known, update these files:
- `android/src/main/java/com/align/android/pages/AndroidLoginPage.java` — `By.id("com.align.app:id/...")`
- `ios/src/main/java/com/align/ios/pages/IOSLoginPage.java` — `By.xpath("//XCUIElementTypeX[@name='...']")`

Add new page interfaces under `tests/src/main/java/com/align/pages/`, Android impl under `android/.../pages/`, iOS impl under `ios/.../pages/`, register in `PageFactory`.
