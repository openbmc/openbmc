SUMMARY = "ACPI Power/Sleep state daemon to allow host state events"
DESCRIPTION = "ACPI Power/Sleep state daemon to allow host state events"
GOOGLE_MISC_PROJ = "acpi-power-state-daemon"

require ../google-misc/google-misc.inc

inherit pkgconfig systemd

DEPENDS += " \
  phosphor-dbus-interfaces \
  sdbusplus \
  systemd \
  "

SYSTEMD_SERVICE:${PN} = " \
  acpi-power-state.service \
  host-s0-state.target \
  host-s5-state.target \
  "
