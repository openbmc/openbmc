SUMMARY = "a SocketCAN over Ethernet tunnel"
HOMEPAGE = "https://github.com/mguentner/cannelloni"
LICENSE = "GPL-2.0-only"

SRC_URI = "git://github.com/mguentner/cannelloni.git;protocol=https;branch=master"
SRCREV = "9f649ef21710999c6f674bc3d914ab2e88363bac"

LIC_FILES_CHKSUM = "file://gpl-2.0.txt;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit cmake

PACKAGECONFIG ??= "lksctp-tools"
PACKAGECONFIG[lksctp-tools] = "-DSCTP_SUPPORT=true, -DSCTP_SUPPORT=false, lksctp-tools"

# Workaround for Clang error:
# sctpthread.cpp:95:18: error: variable length arrays in C++ are a Clang extension [-Werror,-Wvla-cxx-extension]
#   95 |   uint8_t buffer[m_linkMtuSize];
#      |                  ^~~~~~~~~~~~~
# Upstream fix proposed: https://github.com/mguentner/cannelloni/pull/82
# When upgrading the recipe, remove this flag once the upstream patch is merged and included to new version.
CXXFLAGS:append:toolchain-clang = " -Wno-error=vla-cxx-extension"
