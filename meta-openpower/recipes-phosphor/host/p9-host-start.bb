SUMMARY = "POWER9 start host"
DESCRIPTION = "Service to start POWER9 IPL through SBE"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit obmc-phosphor-systemd

FILESEXTRAPATHS:prepend := "${THISDIR}/op-host-control:"

PROVIDES += 'virtual/obmc-host-ctl'
RPROVIDES:${PN} += 'virtual-obmc-host-ctl'

RDEPENDS:${PN} += "op-proc-control \
                   op-proc-control-systemd-links"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

TMPL = "start_host@.service"
INSTFMT = "start_host@{0}.service"
TGTFMT = "obmc-host-startmin@{0}.target"
FMT = "../${TMPL}:${TGTFMT}.requires/${INSTFMT}"

SYSTEMD_SERVICE:${PN} += "${TMPL}"
SYSTEMD_LINK:${PN} += "${@compose_list(d, 'FMT', 'OBMC_CHASSIS_INSTANCES')}"
