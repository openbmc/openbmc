SUMMARY = "Binary Encoded JSON library"
DESCRIPTION = "Used to decode Redfish Device Enablement (RDE) BEJ"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
SRCREV = "a4bb3d7f83262a2dfca87947be10138be9f30dfd"
PV = "0.1+git${SRCPV}"
PR = "r1"

SRC_URI = "git://github.com/openbmc/libbej;branch=main;protocol=https"

S = "${WORKDIR}/git"

inherit meson pkgconfig

EXTRA_OEMESON = " \
  -Dtests=disabled \
"
