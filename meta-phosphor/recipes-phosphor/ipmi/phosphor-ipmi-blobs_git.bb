SUMMARY = "Phosphor OEM IPMI BLOBS Protocol Implementation"
DESCRIPTION = "This package handles a series of OEM IPMI commands that implement the BLOB protocol for sending and receiving data over IPMI."
HOMEPAGE = "http://github.com/openbmc/phosphor-ipmi-blobs"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"
DEPENDS += " \
  ipmi-blob-tool \
  phosphor-ipmi-host \
  phosphor-logging \
  "
SRCREV = "0ccc198cda77c56c992664f0292b990d99339406"
PV = "0.1+git${SRCPV}"
PR = "r1"

SRC_URI = "git://github.com/openbmc/phosphor-ipmi-blobs;branch=master;protocol=https"

S = "${WORKDIR}/git"

inherit meson pkgconfig

EXTRA_OEMESON:append = " \
  -Dtests=disabled \
  -Dexamples=false \
  "

FILES:${PN} += "${libdir}/ipmid-providers"
