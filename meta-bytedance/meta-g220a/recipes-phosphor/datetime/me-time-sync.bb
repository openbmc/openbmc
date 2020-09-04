SUMMARY = "ME time sync"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch
inherit obmc-phosphor-systemd

RDEPENDS_${PN} += "bash"

S = "${WORKDIR}"

SRC_URI += "file://me-time-sync.sh"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/me-time-sync.sh ${D}${bindir}/me-time-sync.sh
}

SYSTEMD_SERVICE_${PN} += "me-time-sync.service"
