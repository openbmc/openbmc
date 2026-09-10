SUMMARY = "Power usage tool"
DESCRIPTION = "Linux tool to diagnose issues with power consumption and power management."
HOMEPAGE = "https://01.org/powertop/"
BUGTRACKER = "https://app.devzing.com/powertopbugs/bugzilla"
DEPENDS = "ncurses libnl pciutils libtracefs"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=12f884d2ae1ff87c09e5b7ccc2c4ca7e"

SRC_URI = "git://github.com/fenrus75/powertop;protocol=https;branch=master;tag=v${PV}"
SRCREV = "6147262e506c607036eaca4182ee491b61d06aa3"

inherit meson gettext pkgconfig bash-completion

# meson defaults to /usr/bin, keep /usr/sbin for backward compatibility
EXTRA_OEMESON = "--bindir=${sbindir}"

inherit update-alternatives
ALTERNATIVE:${PN} = "powertop"
ALTERNATIVE_TARGET[powertop] = "${sbindir}/powertop"
ALTERNATIVE_LINK_NAME[powertop] = "${sbindir}/powertop"
ALTERNATIVE_PRIORITY = "100"

