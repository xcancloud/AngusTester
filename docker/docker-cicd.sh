#!/bin/sh

# === Docker Image Builder and Publisher ===
# This script builds a Docker image and pushes it to a registry
# Usage: Ensure Docker is running and user has registry permissions

# Terminate immediately on any command failure
set -e

# Validate essential prerequisites
if [ ! -f "Dockerfile" ]; then
    echo "ERROR: Dockerfile not found in current directory. Aborting." >&2
    exit 1
fi

# Construct file/directory names from build parameters
APP_PACKAGE_FILE=@hump.name@-@editionName@-@project.version@
RELEASE_FILE="${APP_PACKAGE_FILE}.zip"
RELEASE_DIR=${APP_PACKAGE_FILE}

# Check if release zip exists and handle extraction
if [ -f "$RELEASE_FILE" ]; then
    echo "Release file found: $RELEASE_FILE"

    # Clean existing directory if present
    if [ -d "$RELEASE_DIR" ]; then
        echo "Cleaning existing release directory"
        rm -rf "$RELEASE_DIR"
    fi

    # Create new directory and extract zip contents
    echo "Extracting to: $RELEASE_DIR"
    unzip -qo "$RELEASE_FILE" -d "$RELEASE_DIR"

    # Navigate into release directory for Docker operations
    echo "Working in directory: $(pwd)"
else
    echo "Release file not found: $RELEASE_FILE"
    echo "Proceeding with current directory contents"
fi

cp -rp $RELEASE_DIR/conf $RELEASE_DIR/default-conf
cp -rp $RELEASE_DIR/plugins $RELEASE_DIR/default-plugins
rm -rf $RELEASE_DIR/conf $RELEASE_DIR/plugins
mkdir -p $RELEASE_DIR/conf $RELEASE_DIR/plugins

# Configure image parameters
IMAGE_NAME0="xcancloud/@hump.name@-@editionName@"  # Registry image identifier
IMAGE_NAME=$(echo "$IMAGE_NAME0" | tr '[:upper:]' '[:lower:]')
IMAGE_VERSION="@project.version@"                 # Semantic version from build

# Build Docker image with current directory context
echo "building: $IMAGE_NAME:$IMAGE_VERSION"
docker build -f ./Dockerfile -t "$IMAGE_NAME:$IMAGE_VERSION" .

# Push version-tagged image to registry
echo "Publishing: $IMAGE_NAME:$IMAGE_VERSION"
docker push "$IMAGE_NAME:$IMAGE_VERSION"

# Create and push 'latest' tag for most recent version
echo "Publishing: $IMAGE_NAME:latest"
docker tag "$IMAGE_NAME:$IMAGE_VERSION" "$IMAGE_NAME:latest"
docker push "$IMAGE_NAME:latest"

echo "Success: image published with version [$IMAGE_VERSION] and 'latest' tag"
