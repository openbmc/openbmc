LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit obmc-phosphor-systemd

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

RDEPENDS:${PN} += " bash"

SRC_URI += " \
    file://reconfig-interface-duid-ll \
    file://reconfig-interface-duid-ll@.service \
    "

do_install() {
    install -d ${D}${libexecdir}
    install -m 0755 ${UNPACKDIR}/reconfig-interface-duid-ll ${D}${libexecdir}
}

TGT = "${SYSTEMD_DEFAULT_TARGET}"
RECONF_DUID_LL_INSTFMT="../reconfig-interface-duid-ll@.service:${TGT}.wants/reconfig-interface-duid-ll@{0}.service"

SYSTEMD_SERVICE:${PN} += "reconfig-interface-duid-ll@.service"
SYSTEMD_LINK:${PN} += "${@compose_list(d, 'RECONF_DUID_LL_INSTFMT', 'FB_ETH_INTF')}"
