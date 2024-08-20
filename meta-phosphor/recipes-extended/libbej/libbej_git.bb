SUMMARY = "Binary Encoded JSON library"
DESCRIPTION = "Used to decode Redfish Device Enablement (RDE) BEJ"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
SRCREV = "be27f2e9bfab32d9281496614e3d15a49a4c6aa9"
PV = "0.1+git${SRCPV}"
PR = "r1"

SRC_URI = "git://github.com/openbmc/libbej;branch=main;protocol=https"

S = "${WORKDIR}/git"

inherit meson pkgconfig

EXTRA_OEMESON = " \
  -Dtests=disabled \
"
