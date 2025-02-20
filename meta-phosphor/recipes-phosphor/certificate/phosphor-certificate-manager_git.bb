SUMMARY = "Phosphor Certificate Manager"
DESCRIPTION = "Manages client and server certificates"
HOMEPAGE = "https://github.com/openbmc/phosphor-certificate-manager"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"
DEPENDS = " \
        cli11 \
        openssl \
        phosphor-dbus-interfaces \
        phosphor-logging \
        sdbusplus \
        sdeventplus \
        "
SRCREV = "48aab98b3e2011f0bfe08295ea8bf75ad2ca66f7"
PACKAGECONFIG ??= "bmcweb-cert authority-cert"
PACKAGECONFIG[ibm-hypervisor-cert] = "-Dca-cert-extension=enabled,-Dca-cert-extension=disabled"
PACKAGECONFIG[bmcweb-cert] = "-Dconfig-bmcweb=enabled,-Dconfig-bmcweb=disabled"
PACKAGECONFIG[authority-cert] = "-Dconfig-authority=enabled,-Dconfig-authority=disabled"
PV = "0.1+git${SRCPV}"
PR = "r1"

SRC_URI = "git://github.com/openbmc/phosphor-certificate-manager;branch=master;protocol=https"

S = "${WORKDIR}/git"
SYSTEMD_SERVICE:${PN} = "phosphor-certificate-manager@.service"
SYSTEMD_SERVICE:${PN} = " \
        phosphor-certificate-manager@.service \
        ${@bb.utils.contains('PACKAGECONFIG', 'ibm-hypervisor-cert', 'bmc-vmi-ca-manager.service', '', d)} \
        ${@bb.utils.contains('PACKAGECONFIG', 'authority-cert', 'phosphor-certificate-manager@authority.service', '', d)} \
        ${@bb.utils.contains('PACKAGECONFIG', 'bmcweb', 'phosphor-certificate-manager@bmcweb.service', '', d)} \
        "

inherit meson pkgconfig systemd

EXTRA_OEMESON += "-Dtests=disabled"

FILES:${PN}:append = " ${systemd_system_unitdir}/* ${datadir}/dbus-1"
