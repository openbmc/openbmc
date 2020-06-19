SUMMARY = "Service configuration manager daemon to control the service"
DESCRIPTION = "Applications must use service manager daemon to configure \
              phosphor-ipmi-net, bmcweb, obmc-console etc in the system, \
              instead of directly controlling the same using 'systemd' or \
              'iptables'."

PR = "r1"
PV = "1.0+git${SRCPV}"

inherit meson pkgconfig systemd

require srvcfg-manager.inc

S = "${WORKDIR}/git"
SYSTEMD_SERVICE_${PN} = "srvcfg-manager.service"

DEPENDS += " \
    boost \
    phosphor-logging \
    sdbusplus \
    sdbusplus-native \
    systemd \
    "