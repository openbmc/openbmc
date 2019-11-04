SUMMARY = "Unicode Character Database"
HOMEPAGE = "https://unicode.org/ucd/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://unicode.org.license.html;beginline=42;endline=85;md5=67619d0c52706853c0256514e7fd04cd"

SRC_URI = " \
    https://www.unicode.org/Public/zipped/${PV}/UCD.zip;name=ucd;subdir=ucd \
    https://www.unicode.org/license.html;name=license;subdir=${BP};downloadfilename=unicode.org.license.html \
"
SRC_URI[ucd.md5sum] = "430cbdac2615451571dd69a976dd08f6"
SRC_URI[ucd.sha256sum] = "25ba51a0d4c6fa41047b7a5e5733068d4a734588f055f61e85f450097834a0a6"

SRC_URI[license.md5sum] = "f03bafb623258f85ff2032c1ce567b7c"
SRC_URI[license.sha256sum] = "983225207de8a707d0903a8d70fb7a4b28c5e0f64f2366e84a6192a2d618fed4"

inherit allarch

do_configure[noexec] = "1"

do_install() {
    install -d ${D}${datadir}/unicode
    cp -rf ${WORKDIR}/ucd ${D}${datadir}/unicode
}

FILES_${PN} = "${datadir}/unicode/ucd"
