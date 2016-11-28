SUMMARY = "POWER9 start host"
DESCRIPTION = "Service to start POWER9 IPL through SBE"
PR = "r1"

inherit obmc-phosphor-systemd
inherit obmc-phosphor-license

FILESEXTRAPATHS_prepend := "${THISDIR}/op-pdbg-host-control:"

PROVIDES += 'virtual/obmc-host-ctl'
RPROVIDES_${PN} += 'virtual-obmc-host-ctl'

RDEPENDS_${PN} += "pdbg \
                   p9-vcs-workaround"

S = "${WORKDIR}"
SRC_URI += "file://start_host.sh"

do_install() {
        install -d ${D}${bindir}
        install -m 0755 ${WORKDIR}/start_host.sh \
                        ${D}${bindir}/start_host.sh
}

TMPL = "start_host@.service"
INSTFMT = "start_host@{0}.service"
TGTFMT = "obmc-chassis-start@{0}.target"
FMT = "../${TMPL}:${TGTFMT}.wants/${INSTFMT}"

SYSTEMD_SERVICE_${PN} += "${TMPL}"
SYSTEMD_LINK_${PN} += "${@compose_list(d, 'FMT', 'OBMC_CHASSIS_INSTANCES')}"
SYSTEMD_SUBSTITUTIONS += "MACHINE:${MACHINE}:${TMPL}"
