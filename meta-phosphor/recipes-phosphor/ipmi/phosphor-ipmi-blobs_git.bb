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
SRC_URI = "git://github.com/openbmc/phosphor-ipmi-blobs"
SRCREV = "f39e668c2b87c9453e0e28d5a8886b81ed80c50e"

FILES:${PN} += "${libdir}/ipmid-providers"

EXTRA_OEMESON:append = " \
  -Dtests=disabled \
  -Dexamples=false \
  "
