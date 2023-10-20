SUMMARY = "Library and Host-side tool for talking to OpenBMC IPMI BLOB handlers."
DESCRIPTION = "This package provides a library for the BMC and host for core blob mechanics and host-side binaries for talking to OpenBMC IPMI BLOB handlers."
HOMEPAGE = "http://github.com/openbmc/ipmi-blob-tool"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"
SRCREV = "ff2d30f89a361c43be6508b9b9dc6cc794b7d943"
PV = "0.1+git${SRCPV}"
PR = "r1"

SRC_URI = "git://github.com/openbmc/ipmi-blob-tool;branch=master;protocol=https"

S = "${WORKDIR}/git"

inherit meson pkgconfig

EXTRA_OEMESON = "-Dtests=disabled"
