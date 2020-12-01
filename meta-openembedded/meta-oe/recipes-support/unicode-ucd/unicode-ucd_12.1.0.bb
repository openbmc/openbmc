SUMMARY = "Unicode Character Database"
HOMEPAGE = "https://unicode.org/ucd/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://../license.html;beginline=42;endline=85;md5=ddcaebcc17ab633995f12c383599f377"

SRC_URI = " \
    https://www.unicode.org/Public/zipped/${PV}/UCD.zip;name=ucd;subdir=ucd \
    file://license.html \
"
SRC_URI[ucd.md5sum] = "430cbdac2615451571dd69a976dd08f6"
SRC_URI[ucd.sha256sum] = "25ba51a0d4c6fa41047b7a5e5733068d4a734588f055f61e85f450097834a0a6"

inherit allarch

do_configure[noexec] = "1"

do_install() {
    install -d ${D}${datadir}/unicode
    cp -rf ${WORKDIR}/ucd ${D}${datadir}/unicode
}

FILES_${PN} = "${datadir}/unicode/ucd"
