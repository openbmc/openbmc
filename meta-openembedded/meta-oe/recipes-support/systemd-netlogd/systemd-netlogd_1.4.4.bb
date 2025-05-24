SUMMARY = "Forwards messages from the journal to other hosts over the network using the Syslog Protocol"

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://LICENSE.LGPL2.1;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = "git://github.com/systemd/systemd-netlogd.git;protocol=https;branch=main"
SRCREV = "b03cc3b1a75048c7cf19467d8918a4b7320767e6"

inherit meson systemd pkgconfig useradd features_check

REQUIRED_DISTRO_FEATURES = "systemd"
COMPATIBLE_HOST:libc-musl = "null"

S = "${WORKDIR}/git"

DEPENDS += "systemd"
DEPENDS += "openssl"
DEPENDS += "gperf-native"
DEPENDS += "python3-sphinx-native"

# systemd-netlogd uses prefix and sysconfdir in a weird way.
EXTRA_OEMESON += "--prefix ${libdir}/systemd --sysconfdir ${sysconfdir}/systemd"

FILES:${PN} += "${libdir}"

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "-r systemd-journal"
USERADD_PARAM:${PN} = "--system -d / -M --shell /sbin/nologin -g systemd-journal systemd-journal-netlog"
