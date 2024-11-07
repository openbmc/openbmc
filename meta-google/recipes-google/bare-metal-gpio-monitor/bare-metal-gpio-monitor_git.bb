SUMMARY = "Monitor host GPIO in bare metal mode"
DESCRIPTION = "Monitor host GPIO in bare metal mode"
GOOGLE_MISC_PROJ = "bare-metal-host-monitor"

require ../google-misc/google-misc.inc

inherit pkgconfig systemd

DEPENDS:append = " \
  sdbusplus \
  stdplus \
  phosphor-logging \
  abseil-cpp \
  "

RDEPENDS:${PN}:append = " \
  bare-metal-active \
  "

FILES:${PN} += " \
    ${systemd_system_unitdir} \
"

# List of host instances
# Assume single host labeled as host "0". Override this variable, as needed.
HOST_INSTANCES ?= "0"

do_install:append() {
  install -d ${D}${systemd_system_unitdir}
  install -d ${D}${systemd_system_unitdir}/multi-user.target.wants
  # Configure the service to run at startup, one instance for each host.
  for HOST in ${HOST_INSTANCES}
  do
      ln -fs ../host-gpio-monitor@.service ${D}${systemd_system_unitdir}/multi-user.target.wants/host-gpio-monitor@${HOST}.service
  done
}
