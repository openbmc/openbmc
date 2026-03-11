SUMMARY = "Unicode Character Database"
HOMEPAGE = "https://unicode.org/ucd/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${UNPACKDIR}/ucd-license-v3.txt;md5=ea17640caddb659394df50e5db6efd69"

SRC_URI = " \
    https://www.unicode.org/Public/zipped/${PV}/UCD.zip;name=ucd;subdir=ucd;downloadfilename=unicode-ucd-${PV}.zip \
    https://www.unicode.org/license.txt;downloadfilename=ucd-license-v3.txt;name=ucd-license \
"
SRC_URI[ucd.sha256sum] = "033a5276b5d7af8844589f8e3482f3977a8385e71d107d375055465178c23600"
SRC_URI[ucd-license.sha256sum] = "abf84f74dea2812799e1dbef7f0581adf7db244881e4febb8684f441568da0ad"

inherit allarch

S = "${UNPACKDIR}"

do_configure[noexec] = "1"

do_install() {
    install -d ${D}${datadir}/unicode
    cp -rf ${UNPACKDIR}/ucd ${D}${datadir}/unicode
}

FILES:${PN} = "${datadir}/unicode/ucd"
