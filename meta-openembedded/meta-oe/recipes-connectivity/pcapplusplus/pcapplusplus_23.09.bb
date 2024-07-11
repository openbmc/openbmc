SUMMARY = "A multiplatform C++ library for capturing, parsing and crafting of network packets"
HOMEPAGE = "https://pcapplusplus.github.io/"
BUGTRACKER = "https://github.com/seladb/PcapPlusPlus/issues"
SECTION = "libs/network"
LICENSE = "Unlicense"
LIC_FILES_CHKSUM = "file://LICENSE;md5=911690f51af322440237a253d695d19f"

DEPENDS = "libpcap"

SRC_URI = "git://github.com/seladb/PcapPlusPlus.git;protocol=https;branch=master \
           file://0001-packet-Initialize-m_ExternalRawData-in-IDnsResource-.patch \
           file://0001-initialize-timespec-variable.patch"

SRCREV = "4cf8ed44f9dd145f874dc1dd747dfefcfcab75be"

S = "${WORKDIR}/git"

inherit cmake

PACKAGECONFIG ??= ""
PACKAGECONFIG[examples] = "-DPCAPPP_BUILD_EXAMPLES=ON,-DPCAPPP_BUILD_EXAMPLES=OFF"
PACKAGECONFIG[shared] = "-DBUILD_SHARED_LIBS=ON,-DBUILD_SHARED_LIBS=OFF"
PACKAGECONFIG[tests] = "-DPCAPPP_BUILD_TESTS=ON,-DPCAPPP_BUILD_TESTS=OFF"
PACKAGECONFIG[zstd] = "-DLIGHT_PCAPNG_ZSTD=ON,-DLIGHT_PCAPNG_ZSTD=OFF,zstd"
