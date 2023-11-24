SUMMARY = "A packet dissection and creation library"
HOMEPAGE = "https://github.com/libnet/libnet"

SECTION = "libs"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=07f291bf6e78efa05cec668cf6a09acc"

DEPENDS = "libpcap"

SRC_URI = "git://github.com/libnet/libnet.git;protocol=https;branch=master"

SRC_URI[sha256sum] = "1e9e9054d688b059bcbaf878d8c4fbf69bfc0c9386cd4e7779fbb53339050d2e"
SRCREV = "deaebdfe2743e8a6f04d3c307d9272afeeecfade"

S = "${WORKDIR}/git"

inherit autotools binconfig multilib_script
MULTILIB_SCRIPTS = "${PN}-dev:${bindir}/libnet-config"

