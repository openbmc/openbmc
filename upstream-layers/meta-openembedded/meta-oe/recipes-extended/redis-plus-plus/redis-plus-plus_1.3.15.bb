DESCRIPTION = "C++ client for Redis based on hiredis"
HOMEPAGE = "https://github.com/sewenew/redis-plus-plus"
SECTION = "libs"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

DEPENDS += "hiredis"

SRC_URI = "git://github.com/sewenew/redis-plus-plus;branch=master;protocol=https;tag=${PV} \
           file://0001-CMakeLists.txt-fix-substitution-for-static-libs.patch"
SRCREV = "a63ac43bf192772910b52e27cd2b42a6098a0071"


inherit cmake

# if ssl is enabled for redis-plus-plus it must also be enabled for hiredis
PACKAGECONFIG ??= "ssl"
PACKAGECONFIG[ssl] = "-DREDIS_PLUS_PLUS_USE_TLS=ON, -DREDIS_PLUS_PLUS_USE_TLS=OFF, openssl"
PACKAGECONFIG[test] = "-DREDIS_PLUS_PLUS_BUILD_TEST=ON, -DREDIS_PLUS_PLUS_BUILD_TEST=OFF"

do_install:append() {
    # To remove absolute path in .cmake found by QA warning [buildpaths]
    sed -i -e 's|${STAGING_LIBDIR}/libcrypto.so|crypto|g' ${D}${datadir}/cmake/redis++/redis++-targets.cmake
    sed -i -e 's|${STAGING_LIBDIR}/libssl.so|ssl|g' ${D}${datadir}/cmake/redis++/redis++-targets.cmake
}
