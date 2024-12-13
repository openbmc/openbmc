require ttf.inc

SUMMARY = "Inconsolata font - TTF Version"
HOMEPAGE = "http://www.levien.com/type/myfonts/inconsolata.html"
LICENSE = "OFL-1.1"
LIC_FILES_CHKSUM = "file://${UNPACKDIR}/OFL.txt;md5=969851e3a70122069a4d9ee61dd5a2ed"

SRC_URI = "http://levien.com/type/myfonts/Inconsolata.otf \
    file://OFL.txt"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

FILES:${PN} = "${datadir}/fonts/truetype/Inconsolata.ttf \
    ${datadir}/doc/ttf-inconsolata/*"

do_configure() {
    cp -fr ${UNPACKDIR}/Inconsolata.otf ${S}/Inconsolata.ttf
}

do_install:append() {
    install -d ${D}${datadir}/doc/ttf-inconsolata/
    install -m 0644 ${UNPACKDIR}/OFL.txt ${D}${datadir}/doc/ttf-inconsolata/
}

SRC_URI[md5sum] = "0fbe014c1f0fb5e3c71140ff0dc63edf"
SRC_URI[sha256sum] = "1561e616c414a1b82d6e6dfbd18e5726fd65028913ade191e5fa38b6ec375a1a"
