DESCRIPTION = "Hardware identification and configuration data"
HOMEPAGE = "http://git.fedorahosted.org/git/hwdata.git"
SECTION = "System/Base"

LICENSE = "GPL-2.0+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1556547711e8246992b999edd9445a57"
SRC_URI = "https://git.fedorahosted.org/cgit/${BPN}.git/snapshot/${BP}.tar.gz"

SRC_URI[md5sum] = "f3fa1c5edb66ce5b376d95e772b2d303"
SRC_URI[sha256sum] = "56fc26275b102e538fcfcf9c1093a09f476a1ea8d4e0c733d3c578442923693d"

do_configure() {
    ${S}/configure --datadir=${datadir} --libdir=${libdir}
}

do_install() {
    oe_runmake install DESTDIR=${D}
}

FILES_${PN} = "${libdir}/* \
               ${datadir}/* "
