SUMMARY = "Linux encrypted filesystem management tool"
HOMEPAGE = "http://cryptmount.sourceforge.net/"
LIC_FILES_CHKSUM = "file://README;beginline=3;endline=4;md5=673a990de93a2c5531a0f13f1c40725a"
LICENSE = "GPL-2.0-only"

SRC_URI = "https://sourceforge.net/projects/cryptmount/files/${BPN}/${BPN}-5.3/${BPN}-${PV}.tar.gz \
           file://remove_linux_fs.patch \
           "

SRC_URI[sha256sum] = "682953ff5ba497d48d6b13e22ca726c98659abd781bb8596bb299640dd255d9b"

inherit autotools-brokensep gettext pkgconfig systemd

EXTRA_OECONF = " --enable-cswap --enable-fsck --enable-argv0switch"

PACKAGECONFIG ?="intl luks gcrypt nls"
PACKAGECONFIG:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)}"

PACKAGECONFIG[systemd] = "--with-systemd, --without-systemd, systemd"
PACKAGECONFIG[intl] = "--with-libintl-prefix, --without-libintl-prefix"
PACKAGECONFIG[gcrypt] = "--with-libgcrypt, --without-libgcrypt, libgcrypt"
PACKAGECONFIG[luks] = "--enable-luks, --disable-luks, cryptsetup"
PACKAGECONFIG[nls] = "--enable-nls, --disable-nls, "

SYSTEMD_SERVICE:${PN} = "cryptmount.service"

RDEPENDS:${PN} = "libdevmapper"
