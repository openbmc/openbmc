cmake_minimum_required(VERSION 3.5)
project(wdat)

add_executable(wdat
    wdat.cpp
)

include_directories (
    ${CMAKE_CURRENT_SOURCE_DIR}
)

install(TARGETS wdat DESTINATION sbin)
