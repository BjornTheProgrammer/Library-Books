javac -cp lib/spigot-api-1.19.2-R0.1.jar -d bin/ src/com/library/*
cp src/plugin.yml bin/plugin.yml
jar -cvf Library.jar -C bin .
