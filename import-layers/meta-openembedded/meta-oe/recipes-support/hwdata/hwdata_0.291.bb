DESCRIPTION = "Hardware identification and configuration data"
HOMEPAGE = "http://git.fedorahosted.org/git/hwdata.git"
SECTION = "System/Base"

LICENSE = "GPL-2.0+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1556547711e8246992b999edd9445a57"
SRC_URI = "https://git.fedorahosted.org/cgit/${BPN}.git/snapshot/${BP}.tar.gz"

SRC_URI[md5sum] = "90ffce584bbcb1a5e77eac8503949f71"
SRC_URI[sha256sum] = "e1007a96645cb3390aa9c0ed3f090a69d2302ce4d801914b6af1ab4ec85ede4e"

do_configure() {
    ${S}/configure --datadir=${datadir} --libdir=${libdir}
}

do_install() {
    oe_runmake install DESTDIR=${D}
}

FILES_${PN} = "${libdir}/* \
               ${datadir}/* "
