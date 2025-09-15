LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch systemd obmc-phosphor-systemd

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

RDEPENDS:${PN} += "bash"

SRC_URI += "\
    file://santabarbara-early-sys-init \
    file://santabarbara-eid-init.service \
    file://santabarbara-eid-init \
    file://santabarbara-sys-init.service \
    "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = " \
    santabarbara-eid-init.service \
    santabarbara-sys-init.service \
    "

do_install() {
    install -d ${D}${libexecdir}
    install -m 0755 ${UNPACKDIR}/santabarbara-early-sys-init ${D}${libexecdir}
    install -m 0755 ${UNPACKDIR}/santabarbara-eid-init ${D}${libexecdir}
}

