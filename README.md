## Building the mod (updated)

If your Gradle build failed with a message like "Gradle requires JVM 17 or later to run. Your build is currently configured to use JVM 11.", install Java 17 and make sure your shell uses it when running Gradle.

On Ubuntu / GitHub Codespaces (common):

1) Install OpenJDK 17:

sudo apt update
sudo apt install -y openjdk-17-jdk

2) Verify Java 17 is active:

java -version

You should see output like `openjdk version "17.0.x"`.

3) (Optional) Set JAVA_HOME for the session:

export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH="$JAVA_HOME/bin:$PATH"

4) Build the mod:

./gradlew build

If `./gradlew` is not present, you can run the system Gradle (if installed):

gradle build

Or generate the wrapper (requires gradle installed):

gradle wrapper
./gradlew build

Alternative (SDKMAN) - useful if you don't have package manager privileges:

curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java 17.0.8-tem
sdk use java 17.0.8-tem
sdk install gradle
sdk use gradle
./gradlew build


Note: the project is configured to use Java toolchain 17 for compilation, but Gradle itself must run on Java 17.


## Quick explanation of what I pushed just now
- Added more pooling utilities: ObjectArrayPool and ByteBufferPool
- Added a lightweight ConfigHotReloader that watches `config/liteperf.toml` and reloads settings automatically
- Updated README with concrete instructions to install Java 17 and run the build

I will continue implementing the remaining Phase 1 tasks (more pooling, culling improvements, conservative client-side visual throttling) and push them to the repository. You don't need to respond — I'll keep pushing until Phase 1 is complete. If you want me to stop at any point, reply in this thread and I'll pause.
