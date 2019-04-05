SUMMARY = "ITS Tool allows you to translate your XML documents with PO files"
HOMEPAGE = "http://itstool.org/"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=59c57b95fd7d0e9e238ebbc7ad47c5a5"

inherit autotools python3native

DEPENDS = "python3-native python3-lxml-native"

SRC_URI = "http://files.itstool.org/${BPN}/${BPN}-${PV}.tar.bz2"
SRC_URI[md5sum] = "655c6f78fc64faee45adcc45ccc5a57e"
SRC_URI[sha256sum] = "100506f8df62cca6225ec3e631a8237e9c04650c77495af4919ac6a100d4b308"

do_install_append() {
    # fix shebang of main script
    sed -i 's:${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN}:${bindir}/${PYTHON_PN}:g' ${D}${bindir}/itstool
}

BBCLASSEXTEND = "native"

RDEPENDS_${PN} += "python3 python3-lxml"

