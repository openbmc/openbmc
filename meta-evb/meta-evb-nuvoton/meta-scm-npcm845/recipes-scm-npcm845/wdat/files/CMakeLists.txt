cmake_minimum_required(VERSION 3.5)
project(wdat)
set (CMAKE_CXX_STANDARD 20)
set (CMAKE_CXX_STANDARD_REQUIRED ON)

include_directories (${CMAKE_CURRENT_SOURCE_DIR})

# import libsystemd
find_package (PkgConfig REQUIRED)
pkg_check_modules (SYSTEMD libsystemd REQUIRED)

# import sdbusplus
find_package (PkgConfig REQUIRED)
pkg_check_modules (SDBUSPLUSPLUS sdbusplus REQUIRED)

# import phosphor-logging
find_package (PkgConfig REQUIRED)
pkg_check_modules (LOGGING phosphor-logging REQUIRED)

add_executable(wdat
    wdat.cpp
)
target_link_libraries (${PROJECT_NAME} systemd)
target_link_libraries (${PROJECT_NAME} sdbusplus -lstdc++fs)
target_link_libraries (${PROJECT_NAME} phosphor_logging)

include_directories (
    ${CMAKE_CURRENT_SOURCE_DIR}
)

install(TARGETS wdat DESTINATION bin)
