SUMMARY = "A lightweight hotkey daemon"
HOMEPAGE = "https://github.com/wertarbyte/triggerhappy"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

# matches debian/0.5.0-1 tag
SRCREV = "44a173195986d0d853316cb02a58785ded66c12b"
PV = "0.5.0+git${SRCPV}"
SRC_URI = "git://github.com/wertarbyte/${BPN}.git;branch=debian;protocol=https"

S = "${WORKDIR}/git"

inherit autotools-brokensep pkgconfig perlnative update-rc.d systemd

PACKAGECONFIG = "${@bb.utils.contains('DISTRO_FEATURES','systemd','systemd','',d)}"
PACKAGECONFIG[systemd] = ",,systemd"

INITSCRIPT_NAME = "triggerhappy"
INITSCRIPT_PARAMS = "defaults"
SYSTEMD_SERVICE_${PN} = "triggerhappy.service triggerhappy.socket"

FILES_${PN} = "\
    ${sbindir}/thd \
    ${sbindir}/th-cmd \
    ${sysconfdir}/triggerhappy/triggers.d \
    ${nonarch_base_libdir}/udev/rules.d/80-triggerhappy.rules \
    ${sysconfdir}/init.d/triggerhappy \
    ${systemd_unitdir}/system \
"
CONFFILES_${PN} = "${sysconfdir}/udev/rules.d/80-triggerhappy.rules"

do_install_append() {
    install -d ${D}${sysconfdir}/triggerhappy/triggers.d

    install -d ${D}${nonarch_base_libdir}/udev/rules.d
    install -m 0644 ${S}/udev/triggerhappy-udev.rules ${D}${nonarch_base_libdir}/udev/rules.d/80-triggerhappy.rules

    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${S}/debian/init.d ${D}${sysconfdir}/init.d/triggerhappy

    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -d ${D}/${systemd_unitdir}/system
        install -m 0644 ${S}/systemd/triggerhappy.socket ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/systemd/triggerhappy.service ${D}${systemd_unitdir}/system
    fi
}
