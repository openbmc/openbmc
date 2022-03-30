DESCRIPTION = "Hardware identification and configuration data"
HOMEPAGE = "https://github.com/vcrhonek/hwdata"
SECTION = "System/Base"

LICENSE = "GPL-2.0-or-later | XFree86-1.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1556547711e8246992b999edd9445a57"

SRCREV = "f9e7eed1b85760028a5157986b3313c99c4d0225"
SRC_URI = "git://github.com/vcrhonek/${BPN}.git;branch=master;protocol=https"
S = "${WORKDIR}/git"

inherit allarch

do_configure() {
    ${S}/configure --datadir=${datadir} --libdir=${libdir}
}

do_compile[noexec] = "1"

do_install() {
    oe_runmake install DESTDIR=${D}
}

FILES:${PN} = "${libdir}/* \
               ${datadir}/* "
