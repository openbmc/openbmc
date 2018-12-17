DESCRIPTION = "Hardware identification and configuration data"
HOMEPAGE = "https://github.com/vcrhonek/hwdata"
SECTION = "System/Base"

LICENSE = "GPL-2.0+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1556547711e8246992b999edd9445a57"

PV = "0.316+git${SRCPV}"
SRCREV = "803819aef862ded2ce0737cd3cf642c633b68c35"
SRC_URI = "git://github.com/vcrhonek/${BPN}.git"

S = "${WORKDIR}/git"

do_configure() {
    ${S}/configure --datadir=${datadir} --libdir=${libdir}
}

do_install() {
    oe_runmake install DESTDIR=${D}
}

FILES_${PN} = "${libdir}/* \
               ${datadir}/* "
