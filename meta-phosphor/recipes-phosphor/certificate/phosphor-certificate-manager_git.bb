SUMMARY = "Phosphor Certificate Manager"
DESCRIPTION = "Manages client and server certificates"
HOMEPAGE = "https://github.com/openbmc/phosphor-certificate-manager"

PR = "r1"
PV = "0.1+git${SRCPV}"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI = "git://github.com/openbmc/phosphor-certificate-manager;branch=master;protocol=https"
SRCREV = "ebd21ba4fc36577a70cad207e42d66594b73ed13"

inherit meson pkgconfig systemd

DEPENDS = " \
        cli11 \
        openssl \
        phosphor-dbus-interfaces \
        phosphor-logging \
        sdbusplus \
        sdeventplus \
        "

S = "${WORKDIR}/git"

EXTRA_OEMESON += "-Dtests=disabled"

SYSTEMD_SERVICE:${PN} = "phosphor-certificate-manager@.service"

PACKAGECONFIG ??= "bmcweb-cert nslcd-authority-cert"
PACKAGECONFIG[ibm-hypervisor-cert] = "-Dca-cert-extension=enabled,-Dca-cert-extension=disabled"
PACKAGECONFIG[bmcweb-cert] = "-Dconfig-bmcweb=enabled,-Dconfig-bmcweb=disabled"
PACKAGECONFIG[nslcd-authority-cert] = "-Dconfig-nslcd=enabled,-Dconfig-nslcd=disabled"

SYSTEMD_SERVICE:${PN} = " \
        phosphor-certificate-manager@.service \
        ${@bb.utils.contains('PACKAGECONFIG', 'ibm-hypervisor-cert', 'bmc-vmi-ca-manager.service', '', d)} \
        ${@bb.utils.contains('PACKAGECONFIG', 'nslcd-authority-cert', 'phosphor-certificate-manager@authority.service', '', d)} \
        ${@bb.utils.contains('PACKAGECONFIG', 'bmcweb', 'phosphor-certificate-manager@bmcweb.service', '', d)} \
        "

FILES:${PN}:append = " ${systemd_system_unitdir}/* ${datadir}/dbus-1"
