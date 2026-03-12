SUMMARY = "Unicode Character Database"
HOMEPAGE = "https://unicode.org/ucd/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${UNPACKDIR}/ucd-license-v4.txt;md5=3049f4ad14be1ebf8c80a93d9d32b2d6"

SRC_URI = " \
    https://www.unicode.org/Public/zipped/${PV}/UCD.zip;name=ucd;subdir=ucd;downloadfilename=unicode-ucd-${PV}.zip \
    https://www.unicode.org/license.txt;downloadfilename=ucd-license-v4.txt;name=ucd-license \
"
SRC_URI[ucd.sha256sum] = "033a5276b5d7af8844589f8e3482f3977a8385e71d107d375055465178c23600"
SRC_URI[ucd-license.sha256sum] = "e7a93b009565cfce55919a381437ac4db883e9da2126fa28b91d12732bc53d96"

inherit allarch

S = "${UNPACKDIR}"

do_configure[noexec] = "1"

do_install() {
    install -d ${D}${datadir}/unicode
    cp -rf ${UNPACKDIR}/ucd ${D}${datadir}/unicode
}

FILES:${PN} = "${datadir}/unicode/ucd"
