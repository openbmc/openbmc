SUMMARY = "Phosphor Certificate Manager"
DESCRIPTION = "Manages client and server certificates"
HOMEPAGE = "https://github.com/openbmc/phosphor-certificate-manager"

PR = "r1"
PV = "0.1+git${SRCPV}"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI = "git://github.com/openbmc/phosphor-certificate-manager"
SRCREV = "7f7b47bea7bbd518d1ff5a306ecf3806cf8d0834"

inherit autotools \
        pkgconfig \
        systemd

DEPENDS = " \
        autoconf-archive-native \
        openssl \
        phosphor-dbus-interfaces \
        phosphor-logging \
        sdbusplus \
        sdeventplus \
        "

S = "${WORKDIR}/git"

EXTRA_OECONF += "--disable-tests"

SYSTEMD_SERVICE:${PN} = "phosphor-certificate-manager@.service"

PACKAGECONFIG ??= "bmcweb-cert nslcd-authority-cert"
PACKAGECONFIG[ibm-hypervisor-cert] = "--enable-ca-cert-extension,,"
PACKAGECONFIG[bmcweb-cert] = "--enable-bmcweb-cert-config,,"
PACKAGECONFIG[nslcd-authority-cert] = "--enable-nslcd-authority-cert-config,,"

SYSTEMD_SERVICE:${PN} = " \
        phosphor-certificate-manager@.service \
        ${@bb.utils.contains('PACKAGECONFIG', 'ibm-hypervisor-cert', 'bmc-vmi-ca-manager.service', '', d)} \
        ${@bb.utils.contains('PACKAGECONFIG', 'nslcd-authority-cert', 'phosphor-certificate-manager@authority.service', '', d)} \
        ${@bb.utils.contains('PACKAGECONFIG', 'bmcweb', 'phosphor-certificate-manager@bmcweb.service', '', d)} \
        "

FILES:${PN}:append = " ${systemd_system_unitdir}/* ${datadir}/dbus-1"
