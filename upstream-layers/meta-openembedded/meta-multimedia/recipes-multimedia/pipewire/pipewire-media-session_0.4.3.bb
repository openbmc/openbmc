SUMMARY = "PipeWire Media Session is an example session manager for PipeWire"
HOMEPAGE = "https://gitlab.freedesktop.org/pipewire/media-session"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://COPYING;md5=97be96ca4fab23e9657ffa590b931c1a"

DEPENDS = " \
	pipewire \
	alsa-lib \
	dbus \
"

SRC_URI = "git://gitlab.freedesktop.org/pipewire/media-session.git;protocol=https;branch=master"

SRCREV = "4ac1d82d26bbf9c6f37c56546d26be5a03f886fa"

inherit meson pkgconfig
# https://gitlab.freedesktop.org/pipewire/pipewire/-/issues/2952
CFLAGS += "-DPW_ENABLE_DEPRECATED"
FILES:${PN} += " \
	${systemd_user_unitdir}/pipewire-media-session.service \
	${datadir}/pipewire/media-session.d/* \
"

RRECOMMENDS:${PN} += "pipewire"
