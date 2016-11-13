SUMMARY = "Phosphor Inventory Manager"
DESCRIPTION = "Phosphor Inventory Manager."
HOMEPAGE = "http://github.com/openbmc/phosphor-inventory-manager"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

inherit autotools pkgconfig pythonnative

DEPENDS += " \
	    sdbusplus \
	    sdbusplus-native \
	    python-pyyaml-native \
	    python-mako-native \
	    autoconf-archive-native \
	    "
RDEPENDS_${PN} += "sdbusplus"

SRC_URI = "git://github.com/bradbishop/phosphor-inventory-manager"
SRCREV = "e99f656f8ee601d488cf5d6e49e7091dce91a0d2"

S = "${WORKDIR}/git"
