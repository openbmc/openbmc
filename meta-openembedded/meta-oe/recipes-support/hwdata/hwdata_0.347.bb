DESCRIPTION = "Hardware identification and configuration data"
HOMEPAGE = "https://github.com/vcrhonek/hwdata"
SECTION = "System/Base"

LICENSE = "GPL-2.0+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1556547711e8246992b999edd9445a57"

SRCREV = "ae89c73d89bb9f416b25ad9e850e9606e66a573e"
SRC_URI = "git://github.com/vcrhonek/${BPN}.git;branch=master;protocol=https"
S = "${WORKDIR}/git"

do_configure() {
    ${S}/configure --datadir=${datadir} --libdir=${libdir}
}

do_install() {
    oe_runmake install DESTDIR=${D}
}

FILES:${PN} = "${libdir}/* \
               ${datadir}/* "
