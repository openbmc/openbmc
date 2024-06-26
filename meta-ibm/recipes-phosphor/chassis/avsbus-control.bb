SUMMARY = "AVSBus control"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch
inherit obmc-phosphor-systemd

RDEPENDS:${PN} += "i2c-tools"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

SRC_URI:append:witherspoon = " file://avsbus-enable.sh"
SRC_URI:append:witherspoon = " file://avsbus-disable.sh"

do_install:witherspoon() {
        install -d ${D}${bindir}
        install -m 0755 ${S}/avsbus-enable.sh ${D}${bindir}/avsbus-enable.sh
        install -m 0755 ${S}/avsbus-disable.sh ${D}${bindir}/avsbus-disable.sh
}


TMPL_EN= "avsbus-enable@.service"
TMPL_DIS= "avsbus-disable@.service"
INSTFMT_EN= "avsbus-enable@{0}.service"
INSTFMT_DIS= "avsbus-disable@{0}.service"
TGTFMT = "obmc-chassis-poweron@{0}.target"
FMT_EN = "../${TMPL_EN}:${TGTFMT}.requires/${INSTFMT_EN}"
FMT_DIS = "../${TMPL_DIS}:${TGTFMT}.requires/${INSTFMT_DIS}"

SYSTEMD_SERVICE:${PN}:append:witherspoon = " ${TMPL_EN}"
SYSTEMD_SERVICE:${PN}:append:witherspoon = " ${TMPL_DIS}"
SYSTEMD_LINK:${PN}:append:witherspoon = " ${@compose_list(d, 'FMT_DIS', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_LINK:${PN}:append:witherspoon = " ${@compose_list(d, 'FMT_EN', 'OBMC_CHASSIS_INSTANCES')}"

