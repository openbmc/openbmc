FILESEXTRAPATHS_prepend_centriq2400-rep := "${THISDIR}/${PN}:"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI += "file://centriq-2400rep-console-client.sh"

do_install() {
        install -d ${D}${bindir}
        install -m 0755 ${WORKDIR}/centriq-2400rep-console-client.sh ${D}${bindir}/centriq-2400rep-console-client.sh
}

