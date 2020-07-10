SUMMARY = "Service configuration manager daemon to control the service"
DESCRIPTION = "Applications must use service manager daemon to configure \
              phosphor-ipmi-net, bmcweb, obmc-console etc in the system, \
              instead of directly controlling the same using 'systemd' or \
              'iptables'."
HOMEPAGE = "https://github.com/openbmc/service-config-manager"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7becf906c8f8d03c237bad13bc3dac53"

SRC_URI = "git://github.com/openbmc/service-config-manager"
SRCREV = "1a885d98eea964bd9a3d57152dcaae7d4808211f"

inherit meson pkgconfig systemd

S = "${WORKDIR}/git"
SYSTEMD_SERVICE_${PN} = "srvcfg-manager.service"

DEPENDS = " \
    boost \
    phosphor-logging \
    sdbusplus \
    systemd \
    "