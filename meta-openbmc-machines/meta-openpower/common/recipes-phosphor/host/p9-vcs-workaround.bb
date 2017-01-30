SUMMARY = "POWER9 VCS workaround"
DESCRIPTION = "Apply fixes over FSI to POWER9 CPUs prior to host power on"
PR = "r1"

inherit obmc-phosphor-systemd
inherit obmc-phosphor-license

FILESEXTRAPATHS_prepend := "${THISDIR}/op-pdbg-host-control:"

RDEPENDS_${PN} += "pdbg \
                   virtual-p9-vcs-workaround"

S = "${WORKDIR}"
SRC_URI += "file://vcs_workaround.sh"

do_install() {
        install -d ${D}${bindir}
        install -m 0755 ${WORKDIR}/vcs_workaround.sh \
                        ${D}${bindir}/vcs_workaround.sh
}

TMPL = "vcs_workaround@.service"
INSTFMT = "vcs_workaround@{0}.service"
TGTFMT = "obmc-power-chassis-on@{0}.target"
FMT = "../${TMPL}:${TGTFMT}.wants/${INSTFMT}"

SYSTEMD_SERVICE_${PN} += "${TMPL}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_SUBSTITUTIONS += "MACHINE:${MACHINE}:${TMPL}"
