HOMEPAGE = "http://github.com/openbmc/ipmi-blob-tool"
SUMMARY = "Library and Host-side tool for talking to OpenBMC IPMI BLOB handlers."
DESCRIPTION = "This package provides a library for the BMC and host for core blob mechanics and host-side binaries for talking to OpenBMC IPMI BLOB handlers."
PR = "r1"
PV = "0.1+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

inherit autotools pkgconfig

DEPENDS += "autoconf-archive-native"

EXTRA_OECONF = "--disable-tests"

S = "${WORKDIR}/git"
SRC_URI = "git://github.com/openbmc/ipmi-blob-tool"
SRCREV = "8da5f72db5a9d3374570cb860f52f35d518ac1c2"
