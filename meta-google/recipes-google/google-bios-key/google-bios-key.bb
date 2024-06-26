SUMMARY = "Google BIOS Public Keys"
DESCRIPTION = "Google BIOS Public Keys"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

FILESEXTRAPATHS:prepend:gbmc := "${THISDIR}/${PN}:"

SRC_URI:append:gbmc = " \
     file://platforms_secure.pem \
     file://platforms_bringup.pem \
"


FILES:${PN} += "${datadir}/google-bios-key/platforms_secure.pem"
FILES:${PN} += "${datadir}/google-bios-key/platforms_bringup.pem"
FILES:${PN} += "${datadir}/platforms_secure.pem"
FILES:${PN} += "${datadir}/platforms_bringup.pem"

do_install() {
    install -d ${D}${datadir}/google-bios-key
    install -m 0644 ${UNPACKDIR}/platforms_secure.pem ${D}${datadir}/google-bios-key
    install -m 0644 ${UNPACKDIR}/platforms_bringup.pem ${D}${datadir}/google-bios-key

    ln -s -r ${D}${datadir}/google-bios-key/platforms_secure.pem ${D}${datadir}/platforms_secure.pem
    ln -s -r ${D}${datadir}/google-bios-key/platforms_bringup.pem ${D}${datadir}/platforms_bringup.pem
}
