require ttf.inc

SUMMARY = "Gentium fonts - TTF Version"
HOMEPAGE = "http://scripts.sil.org/gentium"
LICENSE = "OFL-1.0"
LICENSE_URL = "http://scripts.sil.org/cms/scripts/page.php?site_id=nrsi&item_id=OFL"
LIC_FILES_CHKSUM = "file://OFL;md5=33a5bf7b98a9c0ae87430760ba762295 \
"
PR = "r8"

SRC_URI = "https://archive.debian.org/debian/pool/main/t/ttf-gentium/ttf-gentium_${PV}.orig.tar.gz "

S = "${WORKDIR}/ttf-sil-gentium-${PV}"

do_install:append() {

    install -d ${D}${datadir}/doc/ttf-gentium/
    install -d ${D}${datadir}/doc/ttf-gentium-alt/

    install -m 0644 ${S}/OFL ${D}${datadir}/doc/ttf-gentium/
    install -m 0644 ${S}/OFL ${D}${datadir}/doc/ttf-gentium-alt/

}

PACKAGES = "${PN} ${PN}-alt"
FONT_PACKAGES = "${PN} ${PN}-alt"

FILES:${PN}-alt = "${datadir}/fonts/truetype/GenAI*.ttf \
                   ${datadir}/fonts/truetype/GenAR*.ttf \
                   ${datadir}/doc/ttf-gentium-alt/*"

FILES:${PN} = "${datadir}/fonts/truetype/GenI*.ttf \
               ${datadir}/fonts/truetype/GenR*.ttf \
               ${datadir}/doc/ttf-gentium/*"

SRC_URI[md5sum] = "4c3e6ae586be277537ebb68f2a45b883"
SRC_URI[sha256sum] = "4746c04c9a4ad9e0788a38e0a2f81919a630d8070ceabc89f156b6d41d8ceb37"
