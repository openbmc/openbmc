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

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED On)
set(CMAKE_CXX_EXTENSIONS Off)

include(GNUInstallDirs)

# Linking a small library makes the example more useful for testing.
find_package(json-c)

# A simple library linking json-c library found by pkgconfig
add_library(cmake-example-lib cpp-example-lib.cpp cpp-example-lib.hpp)
set_target_properties(cmake-example-lib PROPERTIES 
    VERSION ${PROJECT_VERSION}
    SOVERSION ${PROJECT_VERSION_MAJOR}
)
target_link_libraries(cmake-example-lib PRIVATE json-c::json-c)

install(TARGETS cmake-example-lib
    INCLUDES DESTINATION ${CMAKE_INSTALL_INCLUDEDIR}
    ARCHIVE DESTINATION ${CMAKE_INSTALL_LIBDIR}
    LIBRARY DESTINATION ${CMAKE_INSTALL_LIBDIR}
)

# A simple executable linking the library
add_executable(cmake-example cpp-example.cpp)
target_link_libraries(cmake-example PRIVATE cmake-example-lib)

install(TARGETS cmake-example
    RUNTIME DESTINATION ${CMAKE_INSTALL_BINDIR}
)

# A simple test executable for testing the library
add_executable(test-cmake-example test-cpp-example.cpp)
target_link_libraries(test-cmake-example PRIVATE cmake-example-lib)

if (FAILING_TEST)
    target_compile_definitions(test-cmake-example PRIVATE FAIL_COMPARISON_STR="foo")
endif(FAILING_TEST)

install(TARGETS test-cmake-example
    RUNTIME DESTINATION ${CMAKE_INSTALL_BINDIR}
)

include(CTest)
add_test(NAME test-cmake-example COMMAND test-cmake-example)
