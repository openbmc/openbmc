DESCRIPTION = "REALTIMEKIT Realtime Policy and Watchdog Daemon"
HOMEPAGE = "https://gitlab.freedesktop.org/pipewire/rtkit"
LICENSE = "GPL-3.0-only & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a8e768468b658b3ed44971b53d4a6716"

SRC_URI = "git://gitlab.freedesktop.org/pipewire/rtkit.git;protocol=https;branch=master"

SRCREV = "c52db0a9849908613ab8775b519834671b7bec8b"

inherit meson pkgconfig features_check useradd

REQUIRED_DISTRO_FEATURES = "polkit"

DEPENDS = "dbus libcap polkit xxd-native"

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[systemd] = ",,systemd"

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--system --no-create-home --user-group --shell /bin/nologin rtkit"

FILES:${PN} += "${libdir} ${datadir} ${systemd_system_unitdir}"

