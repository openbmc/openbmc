SUMMARY = "Monitor host GPIO in bare metal mode"
DESCRIPTION = "Monitor host GPIO in bare metal mode"
GOOGLE_MISC_PROJ = "bare-metal-host-monitor"

require ../google-misc/google-misc.inc

inherit pkgconfig systemd

SYSTEMD_SERVICE:${PN}:append = " \
  host-gpio-monitor.service \
  "

DEPENDS:append = " \
  sdbusplus \
  stdplus \
  phosphor-logging \
  "

RDEPENDS:${PN}:append = " \
  bare-metal-active \
  "
