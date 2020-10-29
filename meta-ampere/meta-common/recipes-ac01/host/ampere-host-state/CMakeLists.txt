cmake_minimum_required (VERSION 3.6)

project (ampere-host-state CXX)

set (CMAKE_CXX_STANDARD 17)
set (CMAKE_CXX_STANDARD_REQUIRED ON)

# boost
find_package (Boost 1.66 REQUIRED)
include_directories (${BOOST_SRC_DIR})
add_definitions (-DBOOST_ERROR_CODE_HEADER_ONLY)
add_definitions (-DBOOST_SYSTEM_NO_DEPRECATED)
add_definitions (-DBOOST_ALL_NO_LIB)
add_definitions (-DBOOST_NO_RTTI)
add_definitions (-DBOOST_NO_TYPEID)
add_definitions (-DBOOST_ASIO_DISABLE_THREADS)

# ampere-host-state
add_executable (ampere-host-state ampere-host-state.cpp)
target_link_libraries (ampere-host-state systemd sdbusplus -lsystemd gpiodcxx)
install (TARGETS ampere-host-state DESTINATION bin)
set (CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fno-rtti")
set (CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -fno-rtti")

# service file
set (
    SERVICE_FILES
    ${PROJECT_SOURCE_DIR}/xyz.openbmc_project.AmpHostState.service
)
install (FILES ${SERVICE_FILES} DESTINATION /lib/systemd/system/)

