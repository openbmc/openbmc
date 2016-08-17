SECTION = "x11/network"
SUMMARY = "Mail user agent plugins"
DEPENDS = "claws-mail"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

PNBLACKLIST[claws-plugin-mailmbox] ?= "depends on blacklisted claws-mail"

SRC_URI = "http://www.claws-mail.org/downloads/plugins_obsolete/mailmbox-${PV}.tar.gz"
SRC_URI[md5sum] = "7f72c68e6e1a8768f1247f455fd20b62"
SRC_URI[sha256sum] = "633f7835261c88a0aa06de38553ac935a1799a7128120963a7fa87ace01dac18"

inherit autotools pkgconfig

S = "${WORKDIR}/mailmbox-${PV}"

FILES_${PN} = "${libdir}/claws-mail/plugins/*.so"
FILES_${PN}-dbg += "${libdir}/claws-mail/plugins/.debug"
FILES_${PN}-dev += "${libdir}/claws-mail/plugins/*.la"
FILES_${PN}-staticdev = "${libdir}/claws-mail/plugins/*.a"
