SUMMARY = "Phosphor GPIO monitor application"
DESCRIPTION = "Application to monitor gpio assertions"
HOMEPAGE = "http://github.com/openbmc/phosphor-gpio-monitor"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
DEPENDS += "sdbusplus"
DEPENDS += "phosphor-dbus-interfaces"
DEPENDS += "libevdev"
DEPENDS += "phosphor-logging"
DEPENDS += "systemd"
DEPENDS += "boost"
DEPENDS += "libgpiod"
DEPENDS += "cli11"
DEPENDS += "nlohmann-json"
SRCREV = "b5bd88532ccef5e82810a1472af3d8a9213873ae"
PV = "1.0+git${SRCPV}"
PR = "r1"

SRC_URI += "git://github.com/openbmc/phosphor-gpio-monitor;branch=master;protocol=https"

SYSTEMD_PACKAGES = "${GPIO_PACKAGES}"
SYSTEMD_SERVICE:${PN}-monitor += "phosphor-multi-gpio-monitor.service"
SYSTEMD_SERVICE:${PN}-monitor += "phosphor-gpio-monitor@.service"
SYSTEMD_SERVICE:${PN}-presence += "phosphor-gpio-presence@.service"
S = "${WORKDIR}/git"

inherit meson pkgconfig
inherit obmc-phosphor-dbus-service

EXTRA_OEMESON:append = " -Dtests=disabled"

FILES:${PN}-monitor += "${bindir}/phosphor-gpio-monitor"
FILES:${PN}-monitor += "${bindir}/phosphor-multi-gpio-monitor"
FILES:${PN}-monitor += "${bindir}/phosphor-gpio-util"
FILES:${PN}-monitor += "${nonarch_base_libdir}/udev/rules.d/99-gpio-keys.rules"
FILES:${PN}-presence += "${bindir}/phosphor-gpio-presence"

ALLOW_EMPTY:${PN} = "1"

GPIO_PACKAGES = " \
        ${PN}-monitor \
        ${PN}-presence \
"
PACKAGE_BEFORE_PN += "${GPIO_PACKAGES}"
