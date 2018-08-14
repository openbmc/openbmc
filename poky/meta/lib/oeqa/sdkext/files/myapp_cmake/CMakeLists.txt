cmake_minimum_required (VERSION 2.6)
project (myapp)
# The version number.
set (myapp_VERSION_MAJOR 1)
set (myapp_VERSION_MINOR 0)

# add the executable
add_executable (myapp myapp.c)

install(TARGETS myapp
        RUNTIME DESTINATION bin)
