SUMMARY = "VRM Overrides"
DESCRIPTION = "Sets VRMs to custom voltages"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch
inherit obmc-phosphor-systemd

RDEPENDS:${PN} += "i2c-tools bash"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"
SRC_URI:append:ibm-ac-server = " file://ir35221-unbind-bind.sh"
SRC_URI:append:ibm-ac-server = " file://vrm-control.sh"

do_install:ibm-ac-server() {
        install -d ${D}${bindir}
        install -m 0755 ${UNPACKDIR}/ir35221-unbind-bind.sh ${D}${bindir}/ir35221-unbind-bind.sh
        install -m 0755 ${UNPACKDIR}/vrm-control.sh ${D}${bindir}/vrm-control.sh
}

TMPL = "vrm-control@.service"
INSTFMT = "vrm-control@{0}.service"
TGTFMT_ON = "obmc-chassis-poweron@{0}.target"
FMT_ON = "../${TMPL}:${TGTFMT_ON}.requires/${INSTFMT}"

TMPL_ON_IRBIND = "ir35221-on-bind@.service"
INSTFMT_ON_IRBIND = "ir35221-on-bind@{0}.service"
FMT_ON_IRBIND = "../${TMPL_ON_IRBIND}:${TGTFMT_ON}.wants/${INSTFMT_ON_IRBIND}"

TMPL_ON_IRUNBIND = "ir35221-on-unbind@.service"
INSTFMT_ON_IRUNBIND = "ir35221-on-unbind@{0}.service"
FMT_ON_IRUNBIND = "../${TMPL_ON_IRUNBIND}:${TGTFMT_ON}.requires/${INSTFMT_ON_IRUNBIND}"

SYSTEMD_SERVICE:${PN}:append:ibm-ac-server = " ${TMPL_ON_IRUNBIND} ${TMPL_ON_IRBIND}"
SYSTEMD_SERVICE:${PN}:append:ibm-ac-server = " ${TMPL}"
SYSTEMD_LINK:${PN}:append:ibm-ac-server = " ${@compose_list(d, 'FMT_ON', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_LINK:${PN}:append:ibm-ac-server = " ${@compose_list(d, 'FMT_ON_IRBIND', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_LINK:${PN}:append:ibm-ac-server = " ${@compose_list(d, 'FMT_ON_IRUNBIND', 'OBMC_CHASSIS_INSTANCES')}"
