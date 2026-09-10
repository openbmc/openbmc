SUMMARY = "A multiplatform C++ library for capturing, parsing and crafting of network packets"
HOMEPAGE = "https://pcapplusplus.github.io/"
BUGTRACKER = "https://github.com/seladb/PcapPlusPlus/issues"
SECTION = "libs/network"
LICENSE = "Unlicense"
LIC_FILES_CHKSUM = "file://LICENSE;md5=911690f51af322440237a253d695d19f"

DEPENDS = "libpcap"

SRC_URI = "git://github.com/seladb/PcapPlusPlus.git;protocol=https;branch=master;tag=v${PV} \
           file://0001-Common-declare-align-template-inline-instead-of-stat.patch"
SRCREV = "0dbbb9c75eb232135f13fdb794318c4da3270ebc"


inherit cmake

PACKAGECONFIG ??= ""
PACKAGECONFIG[examples] = "-DPCAPPP_BUILD_EXAMPLES=ON,-DPCAPPP_BUILD_EXAMPLES=OFF"
PACKAGECONFIG[shared] = "-DBUILD_SHARED_LIBS=ON,-DBUILD_SHARED_LIBS=OFF"
PACKAGECONFIG[tests] = "-DPCAPPP_BUILD_TESTS=ON,-DPCAPPP_BUILD_TESTS=OFF"
PACKAGECONFIG[zstd] = "-DLIGHT_PCAPNG_ZSTD=ON,-DLIGHT_PCAPNG_ZSTD=OFF,zstd"
