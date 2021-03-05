SUMMARY = "Multimedia processing server for Linux"
AUTHOR = "Wim Taymans <wtaymans@redhat.com>"
HOMEPAGE = "https://pipewire.org"
SECTION = "multimedia"
LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=d8153c6e65986f862a0550ca74a3ed73 \
    file://LGPL;md5=2d5025d4aa3495befef8f17206a5b0a1 \
"
DEPENDS = "alsa-lib dbus udev"
SRCREV = "14c11c0fe4d366bad4cfecdee97b6652ff9ed63d"
PV = "0.2.7"

SRC_URI = "git://github.com/PipeWire/pipewire"

S = "${WORKDIR}/git"

inherit meson pkgconfig systemd manpages

PACKAGECONFIG ??= "\
    ${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)} \
    gstreamer \
"

PACKAGECONFIG[systemd] = "-Dsystemd=true,-Dsystemd=false,systemd"
PACKAGECONFIG[gstreamer] = "-Dgstreamer=enabled,-Dgstreamer=disabled,glib-2.0 gstreamer1.0 gstreamer1.0-plugins-base"
PACKAGECONFIG[manpages] = "-Dman=true,-Dman=false,libxml-parser-perl-native"

PACKAGES =+ "\
    ${PN}-spa-plugins \
    ${PN}-alsa \
    ${PN}-config \
    gstreamer1.0-${PN} \
    lib${PN} \
    lib${PN}-modules \
"

RDEPENDS_lib${PN} += "lib${PN}-modules ${PN}-spa-plugins"

FILES_${PN} = "\
    ${sysconfdir}/pipewire/pipewire.conf \
    ${bindir}/pipewire* \
    ${systemd_user_unitdir}/* \
"
FILES_lib${PN} = "\
    ${libdir}/libpipewire-*.so.* \
"
FILES_lib${PN}-modules = "\
    ${libdir}/pipewire-*/* \
"
FILES_${PN}-spa-plugins = "\
    ${bindir}/spa-* \
    ${libdir}/spa/* \
"
FILES_${PN}-alsa = "\
    ${libdir}/alsa-lib/* \
    ${datadir}/alsa/alsa.conf.d/50-pipewire.conf \
"
FILES_gstreamer1.0-${PN} = "\
    ${libdir}/gstreamer-1.0/* \
"

CONFFILES_${PN} = "\
    ${sysconfdir}/pipewire/pipewire.conf \
"
