SUMMARY = "Unicode Character Database"
HOMEPAGE = "https://unicode.org/ucd/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${WORKDIR}/license.html;beginline=43;endline=83;md5=cf6c4777b109bcee78b6083a21be6192"

SRC_URI = " \
    https://www.unicode.org/Public/zipped/${PV}/UCD.zip;name=ucd;subdir=ucd;downloadfilename=unicode-ucd-${PV}.zip \
    https://www.unicode.org/license.html;name=license \
"
SRC_URI[ucd.sha256sum] = "033a5276b5d7af8844589f8e3482f3977a8385e71d107d375055465178c23600"
SRC_URI[license.sha256sum] = "e415e1f2188ef2b1a5f7e6ee8c60cefe8a49dacd8b96c9025cad5013985129e4"

inherit allarch

do_configure[noexec] = "1"

do_install() {
    install -d ${D}${datadir}/unicode
    cp -rf ${WORKDIR}/ucd ${D}${datadir}/unicode
}

FILES:${PN} = "${datadir}/unicode/ucd"
