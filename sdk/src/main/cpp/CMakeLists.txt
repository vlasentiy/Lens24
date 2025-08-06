cmake_minimum_required(VERSION 3.4.1)
set(CMAKE_ANDROID_STL_TYPE system)

#add_link_options(-Wl,-z,max-page-size=16384 -Wl,-z,common-page-size=16384)
set(CMAKE_VERBOSE_MAKEFILE  ON)
set(OpenCV_DIR "${CMAKE_CURRENT_SOURCE_DIR}/opencv2/sdk/native/jni")

add_library(data SHARED CardRecognizer-jni.cpp)
target_link_options(data PRIVATE
        "-Wl,-z,max-page-size=16384"
        "-Wl,-z,common-page-size=16384"
        "-Wl,--verbose"
)

#target_include_directories(data PRIVATE
#        ${CMAKE_CURRENT_SOURCE_DIR}/crossplatform/
#        ${CMAKE_CURRENT_SOURCE_DIR}/libyuv/
#)

add_subdirectory(armneon2x86sse)
add_subdirectory(eigen)
add_subdirectory(protobuf)
add_subdirectory(caffe)
add_subdirectory(libyuv)
add_subdirectory(crossplatform)

target_link_libraries(data
        crossplatform
        libyuv
        log
        z
)
