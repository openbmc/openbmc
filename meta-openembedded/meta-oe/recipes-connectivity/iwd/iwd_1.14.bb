SUMMARY = "Wireless daemon for Linux"
HOMEPAGE = "https://iwd.wiki.kernel.org/"
LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=fb504b67c50331fc78734fed90fb0e09"

DEPENDS = "ell"

SRC_URI = "https://www.kernel.org/pub/linux/network/wireless/${BP}.tar.xz \
           file://0001-build-Use-abs_top_srcdir-instead-of-abs_srcdir-for-e.patch \
          "
SRC_URI[sha256sum] = "21be6ad59ba666ba1e50e01889d647472b9b2f96f4941123db036fd33c257f0b"

inherit autotools manpages pkgconfig python3native systemd

PACKAGECONFIG ??= " \
    client \
    monitor \
    ${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)} \
"
PACKAGECONFIG[client] = "--enable-client,--disable-client,readline"
PACKAGECONFIG[monitor] = "--enable-monitor,--disable-monitor"
PACKAGECONFIG[manpages] = "--enable-manual-pages,--disable-manual-pages,python3-docutils-native"
PACKAGECONFIG[wired] = "--enable-wired,--disable-wired"
PACKAGECONFIG[ofono] = "--enable-ofono,--disable-ofono"
PACKAGECONFIG[systemd] = "--with-systemd-unitdir=${systemd_system_unitdir},--disable-systemd-service,systemd"

EXTRA_OECONF = "--enable-external-ell"

SYSTEMD_SERVICE_${PN} = " \
    iwd.service \
    ${@bb.utils.contains('PACKAGECONFIG', 'wired', 'ead.service', '', d)} \
"

do_configure_prepend() {
    install -d ${S}/build-aux
}

do_install_append() {
    # If client and monitor are disabled, bindir is empty, causing a QA error
    rmdir --ignore-fail-on-non-empty ${D}/${bindir}
}

FILES_${PN} += " \
    ${datadir}/dbus-1 \
    ${nonarch_libdir}/modules-load.d \
    ${systemd_unitdir}/network \
"

RDEPENDS_${PN} = "dbus"

RRECOMMENDS_${PN} = "\
    kernel-module-pkcs7-message \
    kernel-module-pkcs8-key-parser \
    kernel-module-x509-key-parser \
"
