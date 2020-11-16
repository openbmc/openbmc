cmake_minimum_required (VERSION 3.6)
project (ampere-host-error-monitor CXX)
include (ExternalProject)
set (CMAKE_CXX_STANDARD 17)
set (CMAKE_CXX_STANDARD_REQUIRED ON)

add_executable (ampere-host-error-monitor ampere-host-error-monitor.cpp)
target_include_directories (ampere-host-error-monitor PRIVATE ${CMAKE_SOURCE_DIR})
target_link_libraries (ampere-host-error-monitor systemd sdbusplus -lsystemd gpiodcxx)
include_directories (${CMAKE_CURRENT_SOURCE_DIR}/include)

target_compile_definitions (
    ampere-host-error-monitor PRIVATE
)

install (TARGETS ampere-host-error-monitor
         RUNTIME DESTINATION bin
         LIBRARY DESTINATION lib
         ARCHIVE DESTINATION lib/static)

find_package (Boost 1.66 REQUIRED)
include_directories (${BOOST_SRC_DIR})

add_definitions (-DBOOST_ERROR_CODE_HEADER_ONLY)
add_definitions (-DBOOST_SYSTEM_NO_DEPRECATED)
add_definitions (-DBOOST_ALL_NO_LIB)
add_definitions (-DBOOST_NO_RTTI)
add_definitions (-DBOOST_NO_TYPEID)
add_definitions (-DBOOST_ASIO_DISABLE_THREADS)

include_directories (${CMAKE_CURRENT_SOURCE_DIR}/include)
set (CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS}")
set (CMAKE_C_FLAGS "${CMAKE_C_FLAGS}")

# service file
set (
    SERVICE_FILES
    ${PROJECT_SOURCE_DIR}/xyz.openbmc_project.ampere_host_error_monitor.service
)
install (FILES ${SERVICE_FILES} DESTINATION /lib/systemd/system/)
