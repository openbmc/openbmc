SUMMARY = "A lightweight hotkey daemon"
HOMEPAGE = "https://github.com/wertarbyte/triggerhappy"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "https://github.com/wertarbyte/triggerhappy/archive/debian/0.5.0-1.tar.gz"

SRC_URI[md5sum] = "77f90a18c775e47c4c5e9e08987ca32f"
SRC_URI[sha256sum] = "9150bafbf7f2de7d57e6cc154676c33da98dc11ac6442e1ca57e5dce82bd4292"

S = "${WORKDIR}/${PN}-debian-${PV}-1"

inherit autotools-brokensep pkgconfig update-rc.d systemd

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
