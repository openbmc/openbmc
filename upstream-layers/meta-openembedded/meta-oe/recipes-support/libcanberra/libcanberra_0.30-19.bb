SUMMARY = "Implementation of XDG Sound Theme and Name Specifications"
DESCRIPTION = "Libcanberra is an implementation of the XDG Sound Theme and Name Specifications, for generating event sounds on free desktops."
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://LGPL;md5=2d5025d4aa3495befef8f17206a5b0a1 \
                    file://src/canberra.h;beginline=7;endline=24;md5=c616c687cf8da540a14f917e0d23ab03"

DEPENDS = "libtool libvorbis"

inherit autotools gtk-doc

SRC_URI = " \
    git://salsa.debian.org/gnome-team/libcanberra;protocol=https;branch=debian/latest;tag=debian/${PV} \
    file://0001-Determine-audio-buffer-size-for-a-time-of-500ms.patch \
"
SRCREV = "d1ed1ac0c9950ed3908c04abb7c4a6de5c51ed94"

EXTRA_OECONF = "\
    --enable-null \
    --disable-oss \
    --disable-tdb \
    --disable-lynx \
"

PACKAGECONFIG ??= " \
	${@bb.utils.filter('DISTRO_FEATURES', 'alsa pulseaudio', d)} \
	${@bb.utils.contains_any('DISTRO_FEATURES', 'x11 wayland', 'gstreamer', '', d)} \
	${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'gtk3', '', d)} \
"
PACKAGECONFIG[alsa] = "--enable-alsa, --disable-alsa, alsa-lib"
PACKAGECONFIG[pulseaudio] = "--enable-pulse, --disable-pulse, pulseaudio"
PACKAGECONFIG[gstreamer] = "--enable-gstreamer, --disable-gstreamer, gstreamer1.0"
PACKAGECONFIG[gtk] = "--enable-gtk, --disable-gtk, gtk+"
PACKAGECONFIG[gtk3] = "--enable-gtk3, --disable-gtk3, gtk+3"

FILES:${PN} += " \
	${systemd_system_unitdir} \
	${libdir}/gtk-2.0 \
	${libdir}/gtk-3.0 \
	${libdir}/gnome-settings-daemon-3.0 \
	${libdir}/libcanberra-0.30 \
	${datadir}/gdm \
	${datadir}/gnome \
"
FILES:${PN}-dev += "${datadir}/vala"

# libcanberra-gtk3-module.so ships a symlink to libcanberra-gtk-module.so
INSANE_SKIP:${PN} = "dev-so"

RRECOMMENDS:${PN} = "sound-theme-freedesktop"
