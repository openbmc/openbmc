SUMMARY = "POWER9 CFAM override"
DESCRIPTION = "Applies user CFAM register overrides from file"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit obmc-phosphor-systemd

FILESEXTRAPATHS:prepend := "${THISDIR}/op-host-control:"
RDEPENDS:${PN} += "op-proc-control"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

TMPL = "cfam_override@.service"
INSTFMT = "cfam_override@{0}.service"
TGTFMT = "obmc-chassis-poweron@{0}.target"
FMT = "../${TMPL}:${TGTFMT}.requires/${INSTFMT}"

SYSTEMD_SERVICE:${PN} += "${TMPL}"
SYSTEMD_LINK:${PN} += "${@compose_list(d, 'FMT', 'OBMC_CHASSIS_INSTANCES')}"
