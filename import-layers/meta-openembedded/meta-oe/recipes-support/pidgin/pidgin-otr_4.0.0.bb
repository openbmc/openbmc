SUMMARY = "(OTR) Messaging allows you to have private conversations over instant messaging"
HOMEPAGE = "http://www.cypherpunks.ca/otr/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=92fe174bad6da3763f6e9e9eaff6df24"
DEPENDS = "libgcrypt libotr pidgin intltool-native"

PNBLACKLIST[pidgin-otr] ?= "depends on blacklisted pidgin"

SRC_URI = "http://www.cypherpunks.ca/otr/${BP}.tar.gz \
           file://ignore_extra-portability_warnings.patch \
"

SRC_URI[md5sum] = "eadb953376acc474e56041d4c12aa2c8"
SRC_URI[sha256sum] = "d56b3f092dbe9ee6597641c7d2dd294884dc04ba47aaf4ec571cd54977df4691"


FILES_${PN} = "${libdir}/pidgin/*"
FILES_${PN}-dbg = "${libdir}/pidgin/.debug"
FILES_${PN}-dbg += "${libdir}/pidgin/.debug/pidgin-otr.so"
FILES_${PN}-dbg += "/usr/src/debug/pidgin-otr/${PV}-${PR}/${P}/*"
inherit autotools pkgconfig
