cmake_minimum_required (VERSION 3.6)

project (ampere-power-cap CXX)

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

# ampere-power-cap
add_executable (ampere-power-cap ampere-power-cap.cpp)
target_link_libraries (ampere-power-cap sdbusplus systemd)
install (TARGETS ampere-power-cap DESTINATION bin)

# service file
set (
    SERVICE_FILES
    ${PROJECT_SOURCE_DIR}/xyz.openbmc_project.AmpPowerCap.service
)
install (FILES ${SERVICE_FILES} DESTINATION /lib/systemd/system/)
