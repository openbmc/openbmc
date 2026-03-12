SUMMARY = "Civetweb embedded web server"
HOMEPAGE = "https://github.com/civetweb/civetweb"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=e5f28949b2d9ec1f4da8bb00aff8b6d4"

SRCREV = "b6ef58f4c4c7fbe90fd1065bccf45b143345f1a6"
PV .= "+git"
SRC_URI = "git://github.com/civetweb/civetweb.git;branch=master;protocol=https \
           file://0001-Unittest-Link-librt-and-libm-using-l-option.patch \
           "

CVE_STATUS[CVE-2025-55763] = "fixed-version: The vulnerability is fixed in the used revision"
CVE_STATUS[CVE-2025-9648] = "fixed-version: The vulnerability is fixed in the used revision"

# civetweb supports building with make or cmake (although cmake lacks few features)
inherit cmake

# Disable Lua and Duktape because they do not compile from CMake (as of v1.8, v1.9 and v1.10).
# Disable ASAN as it is included only in Debug build.
EXTRA_OECMAKE = " \
    -DBUILD_SHARED_LIBS=ON \
    -DCIVETWEB_ENABLE_DUKTAPE=OFF \
    -DCIVETWEB_ENABLE_LUA=OFF \
    -DCIVETWEB_ENABLE_ASAN=OFF \
    -DCIVETWEB_BUILD_TESTING=OFF \
"

# Building with ninja fails on missing third_party/lib/libcheck.a (which
# should come from external CMake project)
OECMAKE_GENERATOR = "Unix Makefiles"

PACKAGECONFIG ??= "caching ipv6 server ssl websockets cpp"
PACKAGECONFIG[caching] = "-DCIVETWEB_DISABLE_CACHING=OFF,-DCIVETWEB_DISABLE_CACHING=ON,"
PACKAGECONFIG[cgi] = "-DCIVETWEB_DISABLE_CGI=OFF,-DCIVETWEB_DISABLE_CGI=ON,"
PACKAGECONFIG[cpp] = "-DCIVETWEB_ENABLE_CXX=ON,-DCIVETWEB_ENABLE_CXX=OFF,"
PACKAGECONFIG[debug] = "-DCIVETWEB_ENABLE_MEMORY_DEBUGGING=ON,-DCIVETWEB_ENABLE_MEMORY_DEBUGGING=OFF,"
PACKAGECONFIG[ipv6] = "-DCIVETWEB_ENABLE_IPV6=ON,-DCIVETWEB_ENABLE_IPV6=OFF,"
PACKAGECONFIG[server] = "-DCIVETWEB_ENABLE_SERVER_EXECUTABLE=ON -DCIVETWEB_INSTALL_EXECUTABLE=ON,-DCIVETWEB_ENABLE_SERVER_EXECUTABLE=OFF -DCIVETWEB_INSTALL_EXECUTABLE=OFF,"
PACKAGECONFIG[ssl] = "-DCIVETWEB_ENABLE_SSL=ON -DCIVETWEB_SSL_OPENSSL_API_1_1=OFF -DCIVETWEB_SSL_OPENSSL_API_3_0=ON -DCIVETWEB_ENABLE_SSL_DYNAMIC_LOADING=OFF,-DCIVETWEB_ENABLE_SSL=OFF,openssl (=1.0.2%),"
PACKAGECONFIG[websockets] = "-DCIVETWEB_ENABLE_WEBSOCKETS=ON,-DCIVETWEB_ENABLE_WEBSOCKETS=OFF,"

do_install:append() {
    sed -i -e 's|${RECIPE_SYSROOT_NATIVE}|\$\{CMAKE_SYSROOT\}|g' \
        -e 's|${RECIPE_SYSROOT}|\$\{CMAKE_SYSROOT\}|g' ${D}${libdir}/cmake/civetweb/civetweb-targets.cmake
}

do_install:append:class-target() {
    sed -i '/list(APPEND _cmake_import_check_files_for_civetweb::server "\${_IMPORT_PREFIX}\/bin\/civetweb" )/d' \
        ${D}${libdir}/cmake/civetweb/civetweb-targets-noconfig.cmake
}

BBCLASSEXTEND = "native"
