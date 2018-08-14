SUMMARY = "Enlightened Sound Daemon"
SECTION = "gpe/base"
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=55ca817ccb7d5b5b66355690e9abc605"
DEPENDS = "audiofile"

inherit gnome

SRC_URI = "ftp://ftp.gnome.org/pub/GNOME/sources/esound/0.2/${P}.tar.bz2;name=archive \
           file://no-docs.patch \
           file://0001-audio_alsa09.c-alsa-drain-fix.patch \
           file://0002-Undefine-open64-and-fopen64.patch \
           file://0003-Use-I-path-in-configure.patch \
           "
SRC_URI[archive.md5sum] = "8d9aad3d94d15e0d59ba9dc0ea990c6c"
SRC_URI[archive.sha256sum] = "5eb5dd29a64b3462a29a5b20652aba7aa926742cef43577bf0796b787ca34911"

EXTRA_OECONF += " \
    --disable-arts \
    --disable-artstest \
"
EXTRA_OECONF_remove = "--disable-schemas-install"

CFLAGS += "-lm"

PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
PACKAGECONFIG[libwrap] = "--with-libwrap,--without-libwrap,tcp-wrappers,"
PACKAGECONFIG[alsa] = "--enable-alsa --disable-oss,--disable-alsa,alsa-lib,"

PACKAGECONFIG ??= "libwrap alsa \
    ${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)} \
"
PACKAGES =+ "esddsp esd esd-utils"

FILES_esddsp = "${bindir}/esddsp ${libdir}/libesddsp.so.*"
FILES_esd = "${bindir}/esd"
FILES_esd-utils = "${bindir}/*"
