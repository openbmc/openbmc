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
SRCREV = "1c88803f75c4d5b606345df7bdef318b677f9696"
PV = "1.0+git${SRCPV}"
PR = "r1"

SRC_URI = "git://github.com/openbmc/phosphor-gpio-monitor;branch=master;protocol=https"
SRC_URI += " file://phosphor-multi-gpio-presence.json"

SYSTEMD_PACKAGES = "${GPIO_PACKAGES}"
SYSTEMD_SERVICE:${PN}-monitor += "phosphor-multi-gpio-monitor.service"
SYSTEMD_SERVICE:${PN}-monitor += "phosphor-gpio-monitor@.service"
SYSTEMD_SERVICE:${PN}-presence += "phosphor-gpio-presence@.service"
SYSTEMD_SERVICE:${PN}-presence += "phosphor-multi-gpio-presence.service"
S = "${WORKDIR}/git"

inherit meson pkgconfig
inherit obmc-phosphor-dbus-service

EXTRA_OEMESON:append = " -Dtests=disabled"

FILES:${PN}-monitor += "${bindir}/phosphor-gpio-monitor"
FILES:${PN}-monitor += "${bindir}/phosphor-multi-gpio-monitor"
FILES:${PN}-monitor += "${bindir}/phosphor-gpio-util"
FILES:${PN}-monitor += "${nonarch_base_libdir}/udev/rules.d/99-gpio-keys.rules"
FILES:${PN}-presence += "${bindir}/phosphor-gpio-presence"
FILES:${PN}-presence += "${bindir}/phosphor-multi-gpio-presence"
FILES:${PN}-presence += "${datadir}/${PN}/phosphor-multi-gpio-presence.json"

ALLOW_EMPTY:${PN} = "1"

GPIO_PACKAGES = " \
        ${PN}-monitor \
        ${PN}-presence \
"
PACKAGE_BEFORE_PN += "${GPIO_PACKAGES}"

do_install:append() {
    install -d ${D}${datadir}/phosphor-gpio-monitor/
    install -m 0644 ${UNPACKDIR}/phosphor-multi-gpio-presence.json ${D}${datadir}/${PN}/
}
