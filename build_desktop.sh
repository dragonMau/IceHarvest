set -e
./gradlew build
cp ./build/libs/IceHarvestDesktop.jar "/mnt/d/SteamLibrary/steamapps/common/Mindustry/saves/mods/"
echo
echo "copied successfully"
date