PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch
inherit phosphor-dbus-monitor

SRC_URI:append = "file://led-config.yaml \
           file://boot_status_led_on.service \
           file://boot_status_led_blink.service \
           file://boot_status_led_off.service"

do_install:append () {
    install -d ${D}/${datadir}/phosphor-dbus-monitor
    install -m 0644 ${UNPACKDIR}/led-config.yaml \
        ${D}/${datadir}/phosphor-dbus-monitor/led-config.yaml
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/*.service ${D}${systemd_system_unitdir}
}

FILES:${PN}:append = " ${datadir}/phosphor-dbus-monitor/led-config.yaml ${systemd_system_unitdir}/*"
