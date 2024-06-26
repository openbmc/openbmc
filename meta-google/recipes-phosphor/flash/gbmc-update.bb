SUMMARY = "Google BMC Update Utilities"
DESCRIPTION = "Google BMC Update Utilities"
PR = "r1"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI += " \
 file://40-gbmc-upgrade.sh \
"

FILES:${PN} += "${datadir}/gbmc-br-dhcp"

RDEPENDS:${PN} += "curl"
RDEPENDS:${PN} += "tar"

do_install() {
    install -d ${D}${datadir}/gbmc-br-dhcp
    install -m 0644 ${UNPACKDIR}/40-gbmc-upgrade.sh ${D}${datadir}/gbmc-br-dhcp/
}
