SUMMARY = "Power usage tool"
DESCRIPTION = "Linux tool to diagnose issues with power consumption and power management."
HOMEPAGE = "https://01.org/powertop/"
BUGTRACKER = "https://app.devzing.com/powertopbugs/bugzilla"
DEPENDS = "ncurses libnl pciutils autoconf-archive-native"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=12f884d2ae1ff87c09e5b7ccc2c4ca7e"

SRC_URI = "git://github.com/fenrus75/powertop;protocol=https;branch=master \
           file://0001-wakeup_xxx.h-include-limits.h.patch \
           "
SRCREV = "d51ad395436d4d1dcc3ca46e1519ffeb475bf651"

S = "${WORKDIR}/git"

LDFLAGS:append = " -pthread"

inherit autotools gettext pkgconfig bash-completion

inherit update-alternatives
ALTERNATIVE:${PN} = "powertop"
ALTERNATIVE_TARGET[powertop] = "${sbindir}/powertop"
ALTERNATIVE_LINK_NAME[powertop] = "${sbindir}/powertop"
ALTERNATIVE_PRIORITY = "100"
