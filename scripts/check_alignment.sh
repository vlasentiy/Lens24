#!/bin/bash

# Default to current directory if no argument is provided
TARGET_DIR="."
if [ -n "$1" ]; then
  TARGET_DIR="$1"
fi

if [ ! -d "$TARGET_DIR" ]; then
  echo "Error: Directory '$TARGET_DIR' not found."
  exit 1
fi
HOST_TAG=""
if [[ "$(uname)" == "Darwin" ]]; then
    HOST_TAG="darwin-x86_64"
elif [[ "$(uname)" == "Linux" ]]; then
    HOST_TAG="linux-x86_64"
elif [[ "$(uname -o)" == "Msys" || "$(uname -o)" == "Cygwin" || "$(uname -o)" == "Git" ]]; then
    HOST_TAG="windows-x86_64"
else
    echo "Error: Could not determine host operating system for NDK path."
    exit 1
fi

OBJDUMP_CMD="$NDK_HOME/toolchains/llvm/prebuilt/$HOST_TAG/bin/llvm-objdump"

found_so_files=0
find "$TARGET_DIR" -maxdepth 3 -name "*.so" -print0 | while IFS= read -r -d $'\0' so_file; do
  if [ -f "$so_file" ]; then
    echo ""
    echo "--- Results for: $so_file ---"
    "$OBJDUMP_CMD" -p "$so_file" | grep --color=never 'LOAD'
    found_so_files=$((found_so_files + 1))
  fi
done

if [ "$found_so_files" -eq 0 ]; then
    echo ""
    echo "No .so files found in $TARGET_DIR"
fi
