SUMMARY = "Binary Encoded JSON library"
DESCRIPTION = "Used to decode Redfish Device Enablement (RDE) BEJ"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
SRCREV = "4b2d10f06d2fed433ec5db51a7e69ab7fd6a6c65"
PV = "0.1+git${SRCPV}"
PR = "r1"

SRC_URI = "git://github.com/openbmc/libbej;branch=main;protocol=https"

inherit meson pkgconfig

EXTRA_OEMESON = " \
  -Dtests=disabled \
"
