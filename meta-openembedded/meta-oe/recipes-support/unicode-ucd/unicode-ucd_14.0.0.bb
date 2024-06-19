SUMMARY = "Unicode Character Database"
HOMEPAGE = "https://unicode.org/ucd/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${UNPACKDIR}/license.txt;md5=4b11b5cbb0a24df9f4e7db63db98f22f"

SRC_URI = " \
    https://www.unicode.org/Public/zipped/${PV}/UCD.zip;name=ucd;subdir=ucd;downloadfilename=unicode-ucd-${PV}.zip \
    https://www.unicode.org/license.txt;name=ucd-license \
"
SRC_URI[ucd.sha256sum] = "033a5276b5d7af8844589f8e3482f3977a8385e71d107d375055465178c23600"
SRC_URI[ucd-license.sha256sum] = "f7830d126f59d83842565d3dddedc79db4ca978ed52aee0ebcc040ea76a85519"

inherit allarch

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

do_configure[noexec] = "1"

do_install() {
    install -d ${D}${datadir}/unicode
    cp -rf ${UNPACKDIR}/ucd ${D}${datadir}/unicode
}

FILES:${PN} = "${datadir}/unicode/ucd"
