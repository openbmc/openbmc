SUMMARY = "Power usage tool"
DESCRIPTION = "Linux tool to diagnose issues with power consumption and power management."
HOMEPAGE = "https://github.com/fenrus75/powertop"
BUGTRACKER = "https://github.com/fenrus75/powertop/issues"
DEPENDS = "ncurses libnl pciutils libtracefs"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=12f884d2ae1ff87c09e5b7ccc2c4ca7e"

SRC_URI = "git://github.com/fenrus75/powertop;protocol=https;branch=master;tag=v${PV}"
SRCREV = "11db5648c9fdea97d36ce6a11ef282e4e037aa92"

inherit meson gettext pkgconfig bash-completion

# meson defaults to /usr/bin, keep /usr/sbin for backward compatibility
EXTRA_OEMESON = "--bindir=${sbindir}"

inherit update-alternatives
ALTERNATIVE:${PN} = "powertop"
ALTERNATIVE_TARGET[powertop] = "${sbindir}/powertop"
ALTERNATIVE_LINK_NAME[powertop] = "${sbindir}/powertop"
ALTERNATIVE_PRIORITY = "100"

