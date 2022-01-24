SUMMARY = "Phosphor GPIO monitor application"
DESCRIPTION = "Application to monitor gpio assertions"
HOMEPAGE = "http://github.com/openbmc/phosphor-gpio-monitor"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"
inherit meson pkgconfig
inherit obmc-phosphor-dbus-service

GPIO_PACKAGES = " \
        ${PN}-monitor \
        ${PN}-presence \
"

PACKAGE_BEFORE_PN += "${GPIO_PACKAGES}"
ALLOW_EMPTY:${PN} = "1"
SYSTEMD_PACKAGES = "${GPIO_PACKAGES}"

RPROVIDES:${PN}-monitor += "virtual/obmc-gpio-monitor"
RPROVIDES:${PN}-presence += "virtual/obmc-gpio-presence"

PROVIDES += "virtual/obmc-gpio-monitor"
PROVIDES += "virtual/obmc-gpio-presence"

DEPENDS += "sdbusplus"
DEPENDS += "phosphor-dbus-interfaces"
DEPENDS += "libevdev"
DEPENDS += "phosphor-logging"
DEPENDS += "systemd"
DEPENDS += "boost"
DEPENDS += "libgpiod"
DEPENDS += "cli11"
DEPENDS += "nlohmann-json"

SYSTEMD_SERVICE:${PN}-monitor += "phosphor-multi-gpio-monitor.service"
SYSTEMD_SERVICE:${PN}-monitor += "phosphor-gpio-monitor@.service"
SYSTEMD_SERVICE:${PN}-presence += "phosphor-gpio-presence@.service"

FILES:${PN}-monitor += "${bindir}/phosphor-gpio-monitor"
FILES:${PN}-monitor += "${bindir}/phosphor-multi-gpio-monitor"
FILES:${PN}-monitor += "${bindir}/phosphor-gpio-util"
FILES:${PN}-monitor += "${nonarch_base_libdir}/udev/rules.d/99-gpio-keys.rules"
FILES:${PN}-presence += "${bindir}/phosphor-gpio-presence"

SRC_URI += "git://github.com/openbmc/phosphor-gpio-monitor;branch=master;protocol=https"
SRCREV = "c4b6ce728089737ad85ab5cfa7f34e2a912f091b"
S = "${WORKDIR}/git"

EXTRA_OEMESON:append = " -Dtests=disabled"
