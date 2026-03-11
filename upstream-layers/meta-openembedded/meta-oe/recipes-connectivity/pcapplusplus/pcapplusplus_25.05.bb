SUMMARY = "A multiplatform C++ library for capturing, parsing and crafting of network packets"
HOMEPAGE = "https://pcapplusplus.github.io/"
BUGTRACKER = "https://github.com/seladb/PcapPlusPlus/issues"
SECTION = "libs/network"
LICENSE = "Unlicense"
LIC_FILES_CHKSUM = "file://LICENSE;md5=911690f51af322440237a253d695d19f"

DEPENDS = "libpcap"

SRC_URI = "git://github.com/seladb/PcapPlusPlus.git;protocol=https;branch=master;tag=v${PV}"
SRCREV = "a49a79e0b67b402ad75ffa96c1795def36df75c8"


inherit cmake

PACKAGECONFIG ??= ""
PACKAGECONFIG[examples] = "-DPCAPPP_BUILD_EXAMPLES=ON,-DPCAPPP_BUILD_EXAMPLES=OFF"
PACKAGECONFIG[shared] = "-DBUILD_SHARED_LIBS=ON,-DBUILD_SHARED_LIBS=OFF"
PACKAGECONFIG[tests] = "-DPCAPPP_BUILD_TESTS=ON,-DPCAPPP_BUILD_TESTS=OFF"
PACKAGECONFIG[zstd] = "-DLIGHT_PCAPNG_ZSTD=ON,-DLIGHT_PCAPNG_ZSTD=OFF,zstd"
