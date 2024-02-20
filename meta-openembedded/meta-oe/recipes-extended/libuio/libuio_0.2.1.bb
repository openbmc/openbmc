SUMMARY = "Libuio - helper library for UIO subsystem"
SECTION = "base"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=393a5ca445f6965873eca0259a17f833"

SRCREV = "17d96e8f9a5bce7cee5e2222855ab46a246dba51"

SRC_URI = "git://git.code.sf.net/p/libuio/code;branch=master;protocol=https"

PV .= "+0.2.2+git"

inherit autotools

S = "${WORKDIR}/git"

PACKAGES += "${PN}-tools"

FILES:${PN} = "${libdir}"
FILES:${PN}-tools = "${bindir}"
