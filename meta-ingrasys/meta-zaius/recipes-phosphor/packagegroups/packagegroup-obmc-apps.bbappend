# Support OCC pass through and general occ control
# Support for GPIO presence service
RDEPENDS:${PN}-inventory:append:zaius = " openpower-occ-control phosphor-gpio-monitor-presence"
