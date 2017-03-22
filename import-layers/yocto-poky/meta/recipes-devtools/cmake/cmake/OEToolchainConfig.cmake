set( CMAKE_SYSTEM_NAME Linux )
set( CMAKE_C_FLAGS $ENV{CFLAGS} CACHE STRING "" FORCE )
set( CMAKE_CXX_FLAGS $ENV{CXXFLAGS}  CACHE STRING "" FORCE )
set( CMAKE ASM_FLAGS ${CMAKE_C_FLAGS} CACHE STRING "" FORCE )
set( CMAKE_LDFLAGS_FLAGS ${CMAKE_CXX_FLAGS} CACHE STRING "" FORCE )

set( CMAKE_FIND_ROOT_PATH $ENV{OECORE_TARGET_SYSROOT} $ENV{OECORE_NATIVE_SYSROOT} )
set( CMAKE_FIND_ROOT_PATH_MODE_PROGRAM NEVER )
set( CMAKE_FIND_ROOT_PATH_MODE_LIBRARY ONLY )
set( CMAKE_FIND_ROOT_PATH_MODE_INCLUDE ONLY )
set( CMAKE_FIND_ROOT_PATH_MODE_PACKAGE ONLY )

# Set CMAKE_SYSTEM_PROCESSOR from the sysroot name (assuming processor-distro-os).
if ($ENV{SDKTARGETSYSROOT} MATCHES "/sysroots/([a-zA-Z0-9_-]+)-.+-.+")
  set(CMAKE_SYSTEM_PROCESSOR ${CMAKE_MATCH_1})
endif()

# Include the toolchain configuration subscripts
file( GLOB toolchain_config_files "${CMAKE_TOOLCHAIN_FILE}.d/*.cmake" )
foreach(config ${toolchain_config_files})
    include(${config})
endforeach()
