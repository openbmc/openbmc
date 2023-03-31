FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SPLASH_IMAGES:rpi = "file://psplash-raspberrypi-img.h;outsuffix=raspberrypi"

SRC_URI:append:rpi = " file://framebuf.conf"

do_install:append:rpi() {
    if [ "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}" ]; then
        install -Dm 0644 ${WORKDIR}/framebuf.conf ${D}${systemd_system_unitdir}/psplash-start.service.d/framebuf.conf
    fi
}

FILES:${PN}:append:rpi = " ${systemd_system_unitdir}/psplash-start.service.d"
