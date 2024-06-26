SUMMARY = "Power device Workarounds"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch
inherit obmc-phosphor-systemd

RDEPENDS:${PN} += "i2c-tools"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}/${MACHINE}:"
SRC_URI:append:witherspoon = " file://power-workarounds.sh"

do_install:append:witherspoon() {
        install -d ${D}${bindir}
        install -m 0755 ${S}/power-workarounds.sh ${D}${bindir}/power-workarounds.sh
}

TMPL_WA = "power-workarounds@.service"
INSTFMT_WA = "power-workarounds@{0}.service"
TGTFMT = "obmc-chassis-poweron@{0}.target"
FMT_WA = "../${TMPL_WA}:${TGTFMT}.requires/${INSTFMT_WA}"

SYSTEMD_SERVICE:${PN}:append:witherspoon = " ${TMPL_WA}"
SYSTEMD_LINK:${PN}:append:witherspoon = "${@compose_list(d, 'FMT_WA', 'OBMC_CHASSIS_INSTANCES')}"
