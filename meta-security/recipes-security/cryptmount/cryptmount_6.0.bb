SUMMARY = "Linux encrypted filesystem management tool"
HOMEPAGE = "http://cryptmount.sourceforge.net/"
LIC_FILES_CHKSUM = "file://README;beginline=3;endline=4;md5=dae0772f0ff46fd927e7fdb08af51b71"
LICENSE = "GPL-2.0-only"

SRC_URI = "https://sourceforge.net/projects/cryptmount/files/${BPN}/${BPN}-${PV}/${BPN}-${PV}.tar.gz \
           "

SRC_URI[sha256sum] = "86528a9175e1eb53f60613e3c3ea6ae6d69dbfe5ac2b53b2f58ba0f768371e7e"

inherit autotools-brokensep gettext pkgconfig systemd

EXTRA_OECONF = " --enable-cswap --enable-fsck --enable-argv0switch"

PACKAGECONFIG ?="intl luks gcrypt nls"
PACKAGECONFIG:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)}"

PACKAGECONFIG[systemd] = "--with-systemd, --without-systemd, systemd"
PACKAGECONFIG[intl] = "--with-libintl-prefix, --without-libintl-prefix"
PACKAGECONFIG[gcrypt] = "--with-libgcrypt, --without-libgcrypt, libgcrypt"
PACKAGECONFIG[luks] = "--enable-luks, --disable-luks, cryptsetup"
PACKAGECONFIG[nls] = "--enable-nls, --disable-nls, "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "cryptmount.service"

do_install:append () {
    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -D -m 0644 ${S}/sysinit/cryptmount.service ${D}${systemd_system_unitdir}/cryptmount.service
        rm -fr ${D}/usr/lib
    fi
}

FILES:${PN} += "${systemd_system_unitdir}"

RDEPENDS:${PN} = "libdevmapper"
