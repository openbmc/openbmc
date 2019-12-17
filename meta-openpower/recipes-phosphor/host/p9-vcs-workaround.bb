SUMMARY = "POWER9 VCS workaround"
DESCRIPTION = "Apply fixes over FSI to POWER9 CPUs prior to host power on"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit obmc-phosphor-systemd

FILESEXTRAPATHS_prepend := "${THISDIR}/op-host-control:"
RDEPENDS_${PN} += "virtual-p9-vcs-workaround op-proc-control"

S = "${WORKDIR}"

TMPL = "vcs_workaround@.service"
INSTFMT = "vcs_workaround@{0}.service"
TGTFMT = "obmc-chassis-poweron@{0}.target"
FMT = "../${TMPL}:${TGTFMT}.requires/${INSTFMT}"

SYSTEMD_SERVICE_${PN} += "${TMPL}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT', 'OBMC_CHASSIS_INSTANCES')}"
