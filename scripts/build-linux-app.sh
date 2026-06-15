#!/usr/bin/env bash
set -euo pipefail

# Gera um aplicativo Linux clicável usando jpackage.
# Saída principal:
#   build/dist/CalculadoraTrelicas/bin/CalculadoraTrelicas
#   build/dist/CalculadoraTrelicas-linux.zip

APP_NAME="CalculadoraTrelicas"
BUILD_DIR="build"
CLASSES_DIR="$BUILD_DIR/classes"
JAR_DIR="$BUILD_DIR/jar"
DIST_DIR="$BUILD_DIR/dist"
JAR_FILE="$JAR_DIR/$APP_NAME.jar"
ZIP_FILE="$DIST_DIR/$APP_NAME-linux.zip"

rm -rf "$CLASSES_DIR" "$JAR_DIR" "$DIST_DIR"
mkdir -p "$CLASSES_DIR" "$JAR_DIR" "$DIST_DIR"

javac -d "$CLASSES_DIR" \
  app/Main.java \
  UserInterface/*.java \
  UserInterface3D/*.java \
  model/*.java \
  model3d/*.java \
  solver/*.java \
  solver3d/*.java \
  enums/*.java

jar cfe "$JAR_FILE" Main -C "$CLASSES_DIR" .

jpackage \
  --type app-image \
  --name "$APP_NAME" \
  --input "$JAR_DIR" \
  --main-jar "$APP_NAME.jar" \
  --main-class Main \
  --dest "$DIST_DIR" \
  --java-options "-Dfile.encoding=UTF-8"

(
  cd "$DIST_DIR"
  zip -r "$APP_NAME-linux.zip" "$APP_NAME"
)

echo "Aplicativo gerado em: $DIST_DIR/$APP_NAME"
echo "Execute com: $DIST_DIR/$APP_NAME/bin/$APP_NAME"
echo "ZIP gerado em: $ZIP_FILE"
