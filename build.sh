#!/bin/bash
# Build Mindustry mod and copy output to /sdcard

set -e  # exit if any command fails

# Go to project directory
cd "$HOME/projects/IceHarvest" || { echo "Project not found"; exit 1; }

# Add Android build-tools to PATH
export PATH=$PATH:$HOME/Android/Sdk/build-tools/35.0.0/

# Ensure gradlew is executable
chmod +x gradlew

# Resolve d8 (optional but recommended)
d8 --version

# Run deploy
./gradlew deploy

# Copy build artifacts to /sdcard, overwrite if exists
DEST="/sdcard/Documents/java/projects/IceHarvest"
mkdir -p "$DEST"
cp -f ./build/libs/* "$DEST/"

echo "Build successful! Artifacts copied to $DEST"
