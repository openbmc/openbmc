FILESEXTRAPATHS_prepend_stardragon4800-rep2 := "${THISDIR}/${PN}:"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI += "file://stardragon4800-rep2-console-client.sh"

do_install() {
        install -d ${D}${bindir}
        install -m 0755 ${WORKDIR}/stardragon4800-rep2-console-client.sh ${D}${bindir}/stardragon4800-rep2-console-client.sh
}
