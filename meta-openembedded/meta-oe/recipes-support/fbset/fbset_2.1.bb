#
# Copyright Matthias Hentges <devel@hentges.net> (c) 2006
# License: MIT (see http://www.opensource.org/licenses/mit-license.php for a copy of the license)
#
# Filename: fbset_2.1.bb
# Date: 28-May-06

SUMMARY = "The fbset console tool"
HOMEPAGE = "http://users.telenet.be/geertu/Linux/fbdev/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://fbset.c;endline=19;md5=bf326f82cdfcac391af208f019c5603f"
RRECOMMENDS:${PN} = "fbset-modes"
DEPENDS = "bison-native flex-native"


SRC_URI = "${DEBIAN_MIRROR}/main/f/fbset/fbset_2.1.orig.tar.gz \
           file://makefile.patch \
           file://fbset-2.1-fix-makefile-dep.patch \
           file://0001-fbset-including-asm-types.h-is-needed-on-all-linux-s.patch \
"

inherit update-alternatives

do_install() {
    install -d ${D}${sbindir} ${D}${datadir}/man/man8 ${D}${datadir}/man/man5
    install -m 0755 ${B}/fbset ${D}${sbindir}/fbset.real
    install -m 0644 ${B}/*.5 ${D}${datadir}/man/man5
    install -m 0644 ${B}/*.8 ${D}${datadir}/man/man8
}

ALTERNATIVE:fbset = "fbset"
ALTERNATIVE_LINK_NAME[fbset] = "${sbindir}/fbset"
ALTERNATIVE_TARGET[fbset] = "${sbindir}/fbset.real"
ALTERNATIVE_PRIORITY[fbset] = "55"

SRC_URI[sha256sum] = "517fa062d7b2d367f931a1c6ebb2bef84907077f0ce3f0c899e34490bbea9338"
