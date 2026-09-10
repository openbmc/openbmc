SUMMARY = "Unicode Character Database"
HOMEPAGE = "https://unicode.org/ucd/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${UNPACKDIR}/ucd-license-v4.txt;md5=3049f4ad14be1ebf8c80a93d9d32b2d6"

SRC_URI = " \
    https://www.unicode.org/Public/zipped/${PV}/UCD.zip;name=ucd;subdir=ucd;downloadfilename=unicode-ucd-${PV}.zip \
    https://www.unicode.org/license.txt;downloadfilename=ucd-license-v4.txt;name=ucd-license \
"
SRC_URI[ucd.sha256sum] = "c86dd81f2b14a43b0cc064aa5f89aa7241386801e35c59c7984e579832634eb2"
SRC_URI[ucd-license.sha256sum] = "e7a93b009565cfce55919a381437ac4db883e9da2126fa28b91d12732bc53d96"

# The tarball name (UCD.zip) carries no version, so check the per-release
# directories in the Public/zipped/ index instead.
UPSTREAM_CHECK_URI = "https://www.unicode.org/Public/zipped/"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)/"

inherit allarch

S = "${UNPACKDIR}"

do_configure[noexec] = "1"

do_install() {
    install -d ${D}${datadir}/unicode
    cp -rf ${UNPACKDIR}/ucd ${D}${datadir}/unicode
}

FILES:${PN} = "${datadir}/unicode/ucd"
