cmake_minimum_required (VERSION 3.6)
project (tempevent_log CXX)
include (ExternalProject)
set (CMAKE_CXX_STANDARD 17)
set (CMAKE_CXX_STANDARD_REQUIRED ON)

add_executable (tempevent_log src/tempevent_log.cpp)

target_include_directories (tempevent_log PRIVATE ${CMAKE_SOURCE_DIR})

target_link_libraries (tempevent_log systemd sdbusplus -lsystemd gpiodcxx)

include_directories (${CMAKE_CURRENT_SOURCE_DIR}/include)

target_compile_definitions (
    tempevent_log PRIVATE
)

install (TARGETS tempevent_log
         RUNTIME DESTINATION bin
         LIBRARY DESTINATION lib
         ARCHIVE DESTINATION lib/static)

find_package (Boost 1.66 REQUIRED)
include_directories (${BOOST_SRC_DIR})

add_definitions (-DBOOST_ERROR_CODE_HEADER_ONLY)
add_definitions (-DBOOST_SYSTEM_NO_DEPRECATED)
add_definitions (-DBOOST_ALL_NO_LIB)
add_definitions (-DBOOST_NO_TYPEID)
add_definitions (-DBOOST_ASIO_DISABLE_THREADS)

include_directories (${CMAKE_CURRENT_SOURCE_DIR}/include)
