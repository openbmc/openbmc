SUMMARY = "Power Supply Sync"
DESCRIPTION = "Synchronizes the power supplies' INPUT_HISTORY data"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit obmc-phosphor-systemd

RDEPENDS_${PN} += "virtual/obmc-gpio-monitor"

SYNC_SERVICE = "power-supply-sync.service"
TGTFMT = "obmc-chassis-poweron@0.target"
SYNC_FMT = "../${SYNC_SERVICE}:${TGTFMT}.wants/${SYNC_SERVICE}"

SYSTEMD_SERVICE_${PN} += "${SYNC_SERVICE}"
SYSTEMD_LINK_${PN} += "${SYNC_FMT}"
