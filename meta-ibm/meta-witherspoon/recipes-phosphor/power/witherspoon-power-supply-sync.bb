SUMMARY = "Witherspoon Power Supply Sync"
DESCRIPTION = "Synchronizes the power supplies' INPUT_HISTORY data"
PR = "r1"

inherit obmc-phosphor-license
inherit obmc-phosphor-systemd

RDEPENDS_${PN} += "virtual/obmc-gpio-monitor"

SYNC_SERVICE = "witherspoon-power-supply-sync.service"
TGTFMT = "obmc-chassis-poweron@0.target"
SYNC_FMT = "../${SYNC_SERVICE}:${TGTFMT}.wants/${SYNC_SERVICE}"

SYSTEMD_SERVICE_${PN} += "${SYNC_SERVICE}"
SYSTEMD_LINK_${PN} += "${SYNC_FMT}"
