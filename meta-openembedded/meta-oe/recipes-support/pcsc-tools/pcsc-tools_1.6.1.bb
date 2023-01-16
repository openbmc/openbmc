SUMMARY = "Some tools to be used with smart cards and PC/SC"
HOMEPAGE = "http://ludovic.rousseau.free.fr/softwares/pcsc-tools"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENCE;md5=94d55d512a9ba36caa9b7df079bae19f"

SRC_URI = "git://github.com/LudovicRousseau/pcsc-tools;protocol=https;branch=master"

SRCREV = "63e532cfa97932aac76ca5f7473d1ce11602a139"

inherit autotools pkgconfig

S = "${WORKDIR}/git"

DEPENDS = "pcsc-lite"

FILES:${PN} += "${datadir}/pcsc/smartcard_list.txt"
