SUMMARY = "Some tools to be used with smart cards and PC/SC"
HOMEPAGE = "http://ludovic.rousseau.free.fr/softwares/pcsc-tools"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENCE;md5=94d55d512a9ba36caa9b7df079bae19f"

SRC_URI = "git://github.com/LudovicRousseau/pcsc-tools;protocol=https;branch=master"

SRCREV = "53923ef8e98062c478f99e3bfcc8eb4654996c27"

inherit autotools pkgconfig

S = "${WORKDIR}/git"

DEPENDS = "pcsc-lite"

FILES:${PN} += "${datadir}/pcsc/smartcard_list.txt"
