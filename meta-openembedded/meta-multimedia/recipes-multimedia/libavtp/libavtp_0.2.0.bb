SUMMARY = "Implements Audio Video Transport Protocol (AVTP)"
DESCRIPTION = "Open source implementation of Audio Video Transport Protocol (AVTP) \
               specified in IEEE 1722-2016 spec."
HOMEPAGE = "https://github.com/Avnu/libavtp"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7fcb4331e23e45e171cd5693c1ff7d3e"

SRC_URI = "git://github.com/Avnu/libavtp.git;branch=master;protocol=https"
SRC_URI:append:libc-musl = " file://0001-provide-64bit-host-to-network-conversion-macros.patch"
SRCREV = "3599a5bf2d18fc3ae89b64f208d8380e6ee3a866"

S = "${WORKDIR}/git"

inherit meson pkgconfig

EXTRA_OEMESON = "-Dtests=disabled"
