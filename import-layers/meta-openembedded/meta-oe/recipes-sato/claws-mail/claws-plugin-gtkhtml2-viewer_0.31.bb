SECTION = "x11/network"
SUMMARY = "Mail user agent plugins"
DEPENDS = "claws-mail gtkhtml2 curl"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=977f04a8048c04684e521c06e2844a94"

PNBLACKLIST[claws-plugin-gtkhtml2-viewer] ?= "depends on blacklisted claws-mail"

PR = "r1"

SRC_URI = "http://www.claws-mail.org/downloads/plugins_obsolete/gtkhtml2_viewer-${PV}.tar.gz \
           file://configure.patch"
SRC_URI[md5sum] = "a6c9dfa6f969ccd844796a5724b52167"
SRC_URI[sha256sum] = "4d41f6d961efaac0f51705e5052bac732bc0bdafee2ef2082a9cf9d89f183ae5"

inherit autotools pkgconfig gettext

S = "${WORKDIR}/gtkhtml2_viewer-${PV}"

FILES_${PN} = "${libdir}/claws-mail/plugins/*.so"
FILES_${PN}-dbg += "${libdir}/claws-mail/plugins/.debug"
FILES_${PN}-dev += "${libdir}/claws-mail/plugins/*.la"
FILES_${PN}-staticdev = "${libdir}/claws-mail/plugins/*.a"

