cmake_minimum_required(VERSION 3.18.1)

# Set common C++ properties for all targets in this directory and subdirectories
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(OpenCV_DIR "${CMAKE_CURRENT_SOURCE_DIR}/opencv2/sdk/native/jni")

add_subdirectory(armneon2x86sse)
add_subdirectory(eigen)
add_subdirectory(protobuf)
add_subdirectory(caffe)
add_subdirectory(libyuv)
add_subdirectory(crossplatform)

# Define the main JNI library
add_library(data SHARED CardRecognizer-jni.cpp)

# Link the JNI library against its dependencies
target_link_libraries(data
        PRIVATE
        crossplatform
        libyuv
        log # Android logging library
        z)  # zlib

# Add specific linker options to the 'data' library
# This is useful for certain hardware or OS versions, e.g., older Android versions on Tegra devices.
target_link_options(data PRIVATE "-Wl,-z,max-page-size=16384")
