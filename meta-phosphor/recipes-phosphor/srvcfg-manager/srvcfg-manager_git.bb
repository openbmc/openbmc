SUMMARY = "Service configuration manager daemon to control the service"
DESCRIPTION = "Applications must use service manager daemon to configure \
              phosphor-ipmi-net, bmcweb, obmc-console etc in the system, \
              instead of directly controlling the same using 'systemd' or \
              'iptables'."
HOMEPAGE = "https://github.com/openbmc/service-config-manager"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7becf906c8f8d03c237bad13bc3dac53"
DEPENDS = " \
    boost \
    phosphor-logging \
    sdbusplus \
    systemd \
    "
SRCREV = "b35f7fcb7aded18b78f1429c3c6f95594d146d5c"
PV = "1.0+git${SRCPV}"
PR = "r1"

SRC_URI = "git://github.com/openbmc/service-config-manager;branch=master;protocol=https"

PACKAGECONFIG[persist-settings-to-file] = "-Dpersist-settings-to-file=enabled,-Dpersist-settings-to-file=disabled"

S = "${WORKDIR}/git"
SYSTEMD_SERVICE:${PN} = "srvcfg-manager.service"

inherit meson pkgconfig systemd
