SECTION = "x11/network"
SUMMARY = "Mail user agent plugins"
DEPENDS = "claws-mail libxml2 curl glib-2.0 gtk+"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=0c2348e0a084e573f0220f5e45d8097e"

PNBLACKLIST[claws-plugin-rssyl] ?= "depends on blacklisted claws-mail"

SRC_URI = "http://www.claws-mail.org/downloads/plugins_obsolete/rssyl-${PV}.tar.gz \
           file://configure.patch"
SRC_URI[md5sum] = "49b45608e8d160b3625d3d50016ec2ca"
SRC_URI[sha256sum] = "2e96a1cd6a1a5bb7f86cd2eb48f6e174665957fafe1f3b1e8361aac3bb967f79"
inherit autotools pkgconfig gettext

S = "${WORKDIR}/rssyl-${PV}"

FILES_${PN} = "${libdir}/claws-mail/plugins/*.so"
FILES_${PN}-dbg += "${libdir}/claws-mail/plugins/.debug"
FILES_${PN}-dev += "${libdir}/claws-mail/plugins/*.la"
FILES_${PN}-staticdev = "${libdir}/claws-mail/plugins/*.a"
