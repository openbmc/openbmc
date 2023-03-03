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

S = "${WORKDIR}/git"
SRCREV = "80dae7e24bec02b2befe09a72fbac6e2b38ccb5c"

inherit meson pkgconfig
# https://gitlab.freedesktop.org/pipewire/pipewire/-/issues/2952
CFLAGS += "-DPW_ENABLE_DEPRECATED"
FILES:${PN} += " \
	${systemd_user_unitdir}/pipewire-media-session.service \
	${datadir}/pipewire/media-session.d/* \
"

RRECOMMENDS:${PN} += "pipewire"
