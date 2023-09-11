SUMMARY = "Spice agent for Linux"
HOMEPAGE = "https://spice-space.org"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRCREV = "aa08162f036840d3e33502dc0a836b03b9cec97c"

SRC_URI = "git://gitlab.freedesktop.org/spice/linux/vd_agent.git;protocol=https;branch=master"

S = "${WORKDIR}/git"

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"
REQUIRED_DISTRO_FEATURES = "opengl x11"

inherit autotools pkgconfig features_check

DEPENDS = "glib-2.0 alsa-lib gtk4 dbus libdrm spice-protocol libxfixes xrandr xinerama libx11"

EXTRA_OECONF = " \
	--with-gtk4 \
	--enable-pie \
"

FILES:${PN} += "${datadir}"

