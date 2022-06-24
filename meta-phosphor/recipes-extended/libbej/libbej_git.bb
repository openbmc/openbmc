SUMMARY = "Binary Encoded JSON library"
DESCRIPTION = "Used to decode Redfish Device Enablement (RDE) BEJ"
PR = "r1"
PV = "0.1+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

inherit meson pkgconfig

EXTRA_OEMESON = " \
  -Dtests=disabled \
"

SRC_URI += "git://github.com/openbmc/libbej;branch=main;protocol=https"
SRCREV = "cc4098e1508eede5887724fc68119a5e12fc3f96"

S = "${WORKDIR}/git"
