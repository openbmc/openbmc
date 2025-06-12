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
SRCREV = "6e046368a2da9f2bd0a33ba12dd61e3c57338ce1"
PV = "1.0+git${SRCPV}"
PR = "r1"

SRC_URI = "git://github.com/openbmc/service-config-manager;branch=master;protocol=https"

S = "${WORKDIR}/git"
SYSTEMD_SERVICE:${PN} = "srvcfg-manager.service"

inherit meson pkgconfig systemd
