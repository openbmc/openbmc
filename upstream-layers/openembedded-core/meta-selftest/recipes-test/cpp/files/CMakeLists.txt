#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

cmake_minimum_required(VERSION 3.22)

project(cmake-example
  VERSION 1.0.0
  LANGUAGES CXX
)

option(BUILD_SHARED_LIBS "Build using shared libraries" ON)
option(FAILING_TEST "Compile a failing unit test to test the test infrastructure" OFF)
option(WITH_SYSTEMD "Target is managed by systemd: skip the legacy SysV daemonize/pidfile/privilege-drop code" OFF)

set(BINARY_NAME "cmake-example" CACHE STRING "Name of the installed executable and library prefix")
set(TEST_BINARY_NAME "test-cmake-example" CACHE STRING "Name of the installed test executable")

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED On)
set(CMAKE_CXX_EXTENSIONS Off)

include(GNUInstallDirs)

# Define the config file path as a constant
set(CPP_EXAMPLE_CONFIG_PATH "${CMAKE_INSTALL_FULL_SYSCONFDIR}/${BINARY_NAME}.conf")

# Generate config.h from config.h.in
configure_file(config.h.in config.h @ONLY)

# Linking a small library makes the example more useful for testing.
find_package(json-c)

# A simple library linking json-c library found by pkgconfig
add_library(${BINARY_NAME}-lib cpp-example-lib.cpp cpp-example-lib.hpp)
set_target_properties(${BINARY_NAME}-lib PROPERTIES
    VERSION ${PROJECT_VERSION}
    SOVERSION ${PROJECT_VERSION_MAJOR}
)

# Add the build directory to include path for config.h
target_include_directories(${BINARY_NAME}-lib PRIVATE ${CMAKE_CURRENT_BINARY_DIR})

target_link_libraries(${BINARY_NAME}-lib PRIVATE json-c::json-c)

install(TARGETS ${BINARY_NAME}-lib
    INCLUDES DESTINATION ${CMAKE_INSTALL_INCLUDEDIR}
    ARCHIVE DESTINATION ${CMAKE_INSTALL_LIBDIR}
    LIBRARY DESTINATION ${CMAKE_INSTALL_LIBDIR}
)

# A simple executable linking the library
add_executable(${BINARY_NAME} cpp-example.cpp)
target_include_directories(${BINARY_NAME} PRIVATE ${CMAKE_CURRENT_BINARY_DIR})
target_link_libraries(${BINARY_NAME} PRIVATE ${BINARY_NAME}-lib)
if (WITH_SYSTEMD)
    target_compile_definitions(${BINARY_NAME} PRIVATE WITH_SYSTEMD)
else()
    target_sources(${BINARY_NAME} PRIVATE daemonize.cpp daemonize.hpp)
endif(WITH_SYSTEMD)

install(TARGETS ${BINARY_NAME}
    RUNTIME DESTINATION ${CMAKE_INSTALL_BINDIR}
)

# A simple test executable for testing the library
add_executable(${TEST_BINARY_NAME} test-cpp-example.cpp)
target_include_directories(${TEST_BINARY_NAME} PRIVATE ${CMAKE_CURRENT_BINARY_DIR})
target_link_libraries(${TEST_BINARY_NAME} PRIVATE ${BINARY_NAME}-lib)

if (FAILING_TEST)
    target_compile_definitions(${TEST_BINARY_NAME} PRIVATE FAIL_COMPARISON_STR="foo")
endif(FAILING_TEST)

install(TARGETS ${TEST_BINARY_NAME}
    RUNTIME DESTINATION ${CMAKE_INSTALL_BINDIR}
)

include(CTest)
add_test(NAME ${TEST_BINARY_NAME} COMMAND ${TEST_BINARY_NAME})
