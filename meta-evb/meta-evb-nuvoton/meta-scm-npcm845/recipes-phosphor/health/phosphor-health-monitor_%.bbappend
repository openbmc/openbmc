FILESEXTRAPATHS:prepend:scm-npcm845  := "${THISDIR}/files:"

inherit obmc-phosphor-systemd
RDEPENDS:${PN}:append:scm-npcm845 = "bash"
SRC_URI:append:scm-npcm845 = " file://bmc_health_config.json"
SRC_URI:append:scm-npcm845 = " file://0001-change-the-cpu-sensor-name-from-CPU-to-CPU_Utilizati.patch"
SRC_URI:append:scm-npcm845 = " \
  file://utilization-health-sel.sh \
  file://utilization-health-sel@.service \
  file://0002-Add-support-health-SEL-service.patch \
"
FILES:${PN}:append:scm-npcm845 = " ${systemd_system_unitdir}/utilization-health-sel@.service"
SRCREV:scm-npcm845 = "b7d7bd5a384ea501766b15e4613eb9b14fe71e7f"

do_install:append:scm-npcm845() {
    install -d ${D}/${sysconfdir}/healthMon/
    install -m 0644 ${WORKDIR}/bmc_health_config.json ${D}/${sysconfdir}/healthMon/
    install -D -m 0644 ${WORKDIR}/utilization-health-sel@.service ${D}${systemd_system_unitdir}
    install -D -m 0755 ${WORKDIR}/utilization-health-sel.sh ${D}/${bindir}/utilization-health-sel.sh
}
