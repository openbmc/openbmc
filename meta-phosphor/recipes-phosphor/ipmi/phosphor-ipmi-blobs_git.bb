HOMEPAGE = "http://github.com/openbmc/phosphor-ipmi-blobs"
SUMMARY = "Phosphor OEM IPMI BLOBS Protocol Implementation"
DESCRIPTION = "This package handles a series of OEM IPMI commands that implement the BLOB protocol for sending and receiving data over IPMI."
PR = "r1"
PV = "0.1+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

inherit meson pkgconfig

DEPENDS += " \
  ipmi-blob-tool \
  phosphor-ipmi-host \
  phosphor-logging \
  "

S = "${WORKDIR}/git"
SRC_URI = "git://github.com/openbmc/phosphor-ipmi-blobs;branch=master;protocol=https"
SRCREV = "3b89eda93870cdc701481afc7da85de7a033e4a6"

FILES:${PN} += "${libdir}/ipmid-providers"

EXTRA_OEMESON:append = " \
  -Dtests=disabled \
  -Dexamples=false \
  "
