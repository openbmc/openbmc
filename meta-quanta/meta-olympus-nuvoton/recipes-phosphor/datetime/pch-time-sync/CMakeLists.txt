cmake_minimum_required (VERSION 3.5 FATAL_ERROR)
project (pch-time-sync CXX)
set (CMAKE_CXX_STANDARD 17)
set (CMAKE_CXX_STANDARD_REQUIRED ON)
set (CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fno-rtti")
set (CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -fno-rtti")

include_directories (${CMAKE_CURRENT_SOURCE_DIR})

# boost support
find_package (Boost REQUIRED)
# pkg_check_modules(Boost boost REQUIRED)
include_directories (${Boost_INCLUDE_DIRS})
add_definitions (-DBOOST_ERROR_CODE_HEADER_ONLY)
add_definitions (-DBOOST_SYSTEM_NO_DEPRECATED)
add_definitions (-DBOOST_ALL_NO_LIB)
add_definitions (-DBOOST_NO_RTTI)
add_definitions (-DBOOST_NO_TYPEID)
add_definitions (-DBOOST_ASIO_DISABLE_THREADS)

# import sdbusplus
find_package (PkgConfig REQUIRED)
pkg_check_modules (SDBUSPLUSPLUS sdbusplus REQUIRED)
include_directories (${SDBUSPLUSPLUS_INCLUDE_DIRS})
link_directories (${SDBUSPLUSPLUS_LIBRARY_DIRS})

# import phosphor-logging
find_package (PkgConfig REQUIRED)
pkg_check_modules (LOGGING phosphor-logging REQUIRED)
include_directories (${LOGGING_INCLUDE_DIRS})
link_directories (${LOGGING_LIBRARY_DIRS})

add_executable (pch-time-sync pch-time-sync.cpp)

target_link_libraries (${PROJECT_NAME} ${Boost_LIBRARIES})
target_link_libraries (${PROJECT_NAME} ${SDBUSPLUSPLUS_LIBRARIES}
                       phosphor_logging)
target_link_libraries (${PROJECT_NAME} i2c)

install (TARGETS pch-time-sync DESTINATION bin)
