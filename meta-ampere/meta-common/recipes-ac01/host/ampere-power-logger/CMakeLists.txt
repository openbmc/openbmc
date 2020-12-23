cmake_minimum_required (VERSION 3.6)
project (ampere-power-loggerCXX)
include (ExternalProject)
set (CMAKE_CXX_STANDARD 17)
set (CMAKE_CXX_STANDARD_REQUIRED ON)

add_executable (ampere-power-logger src/ampere-power-logger.cpp)

target_include_directories (ampere-power-logger  PRIVATE ${CMAKE_SOURCE_DIR})
target_link_libraries (ampere-power-logger systemd sdbusplus -lsystemd gpiodcxx)
include_directories (${CMAKE_CURRENT_SOURCE_DIR}/include)

target_compile_definitions (
    ampere-power-logger  PRIVATE
)

install (TARGETS ampere-power-logger
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

set (
    SERVICE_FILES
    ${PROJECT_SOURCE_DIR}/service_files/xyz.openbmc_project.power_logger.service
)

install (FILES ${SERVICE_FILES} DESTINATION /lib/systemd/system/)

