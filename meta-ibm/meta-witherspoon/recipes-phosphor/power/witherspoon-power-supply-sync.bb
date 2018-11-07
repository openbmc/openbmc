SUMMARY = "Witherspoon Power Supply Sync"
DESCRIPTION = "Synchronizes the power supplies' INPUT_HISTORY data"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${IBMBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit obmc-phosphor-systemd

RDEPENDS_${PN} += "virtual/obmc-gpio-monitor"

SYNC_SERVICE = "witherspoon-power-supply-sync.service"
TGTFMT = "obmc-chassis-poweron@0.target"
SYNC_FMT = "../${SYNC_SERVICE}:${TGTFMT}.wants/${SYNC_SERVICE}"

SYSTEMD_SERVICE_${PN} += "${SYNC_SERVICE}"
SYSTEMD_LINK_${PN} += "${SYNC_FMT}"
