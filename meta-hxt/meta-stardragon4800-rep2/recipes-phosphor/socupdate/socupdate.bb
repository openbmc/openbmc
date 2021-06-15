SUMMARY = "HXT SOC update"
DESCRIPTION = "A sample script to help update the host(soc) firmware"
HOMEPAGE = ""
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI += "file://socupdate.sh"

do_install() {
        install -d ${D}${bindir}
        install -m 0755 ${WORKDIR}/socupdate.sh ${D}${bindir}/socupdate.sh
}
