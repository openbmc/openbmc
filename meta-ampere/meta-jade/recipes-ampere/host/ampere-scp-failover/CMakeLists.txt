cmake_minimum_required (VERSION 3.6)
project (ampere-scp-failover CXX)
include (ExternalProject)
set (CMAKE_CXX_STANDARD 17)
set (CMAKE_CXX_STANDARD_REQUIRED ON)

add_executable (ampere-scp-failover scp-failover.cpp)

target_include_directories (ampere-scp-failover PRIVATE ${CMAKE_SOURCE_DIR})

target_link_libraries (ampere-scp-failover systemd sdbusplus -lsystemd gpiodcxx)

include_directories (${CMAKE_CURRENT_SOURCE_DIR}/include)

target_compile_definitions (
    ampere-scp-failover PRIVATE
)

install (TARGETS ampere-scp-failover
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

set (
    SERVICE_FILES
    ${PROJECT_SOURCE_DIR}/ampere-scp-failover.service
)
install (FILES ${SERVICE_FILES} DESTINATION /lib/systemd/system/)

