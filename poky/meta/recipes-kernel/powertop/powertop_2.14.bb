SUMMARY = "Power usage tool"
DESCRIPTION = "Linux tool to diagnose issues with power consumption and power management."
HOMEPAGE = "https://01.org/powertop/"
BUGTRACKER = "https://app.devzing.com/powertopbugs/bugzilla"
DEPENDS = "ncurses libnl pciutils autoconf-archive"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=12f884d2ae1ff87c09e5b7ccc2c4ca7e"

SRC_URI = "git://github.com/fenrus75/powertop;protocol=https;branch=master \
           file://0001-wakeup_xxx.h-include-limits.h.patch \
           file://0001-src-fix-compatibility-with-ncurses-6.3.patch \
           "
SRCREV = "52f022f9bbe6e060fba11701d657a8d9762702ba"

S = "${WORKDIR}/git"

LDFLAGS:append = " -pthread"

inherit autotools gettext pkgconfig bash-completion

inherit update-alternatives
ALTERNATIVE:${PN} = "powertop"
ALTERNATIVE_TARGET[powertop] = "${sbindir}/powertop"
ALTERNATIVE_LINK_NAME[powertop] = "${sbindir}/powertop"
ALTERNATIVE_PRIORITY = "100"
