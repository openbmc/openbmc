SUMMARY = "libei is a library for Emulated Input, primarily aimed at the Wayland stack."
HOMEPAGE = "https://gitlab.freedesktop.org/libinput/libei"
SECTION = "graphics"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://COPYING;md5=a98fa76460f96f41696611d6f07e8d49"

DEPENDS = " \
	libxkbcommon \
	libevdev \
	libxslt-native \
	python3-attrs-native \
	python3-jinja2-native \
	${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', 'basu', d)} \
"

SRC_URI = "git://gitlab.freedesktop.org/libinput/libei.git;protocol=https;branch=main"

S = "${WORKDIR}/git"
SRCREV = "997b7c0f37faea4f8bae59613c8f27370925d5b0"

inherit meson pkgconfig

EXTRA_OEMESON = "-Dtests=disabled"

