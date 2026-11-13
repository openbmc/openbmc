SUMMARY = "Unicode Character Database"
HOMEPAGE = "https://unicode.org/ucd/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${UNPACKDIR}/ucd-license-v4.txt;md5=3049f4ad14be1ebf8c80a93d9d32b2d6"

SRC_URI = " \
    https://www.unicode.org/Public/${PV}/ucd/UCD.zip;name=ucd;subdir=${BP};downloadfilename=unicode-ucd-${PV}.zip \
    https://www.unicode.org/license.txt;downloadfilename=ucd-license-v4.txt;name=ucd-license \
"
SRC_URI[ucd.sha256sum] = "7b3e555514060b92290d154f53655c5eb0fa62b16eb04c03434ff72d1a66a0d8"
SRC_URI[ucd-license.sha256sum] = "e7a93b009565cfce55919a381437ac4db883e9da2126fa28b91d12732bc53d96"

# The tarball name (UCD.zip) carries no version, so check the per-release
# directories in the Public/ index instead.
UPSTREAM_CHECK_URI = "https://www.unicode.org/Public/"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)/"

inherit allarch

do_configure[noexec] = "1"

do_install() {
    install -d ${D}${datadir}/unicode
    cp -rf ${S} ${D}${datadir}/unicode/ucd
}

FILES:${PN} = "${datadir}/unicode/ucd"
