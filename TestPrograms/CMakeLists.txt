cmake_minimum_required(VERSION 3.6)
project(TestPrograms)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

set(SOURCE_FILES pipeline/simple_pipeline.c)
add_executable(TestPrograms ${SOURCE_FILES})