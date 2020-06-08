SUMMARY = "Power usage tool"
DESCRIPTION = "Linux tool to diagnose issues with power consumption and power management."
HOMEPAGE = "https://01.org/powertop/"
BUGTRACKER = "https://app.devzing.com/powertopbugs/bugzilla"
DEPENDS = "ncurses libnl pciutils"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=12f884d2ae1ff87c09e5b7ccc2c4ca7e"

SRC_URI = "git://github.com/fenrus75/powertop;protocol=https \
    file://0001-wakeup_xxx.h-include-limits.h.patch \
"
SRCREV = "e8765b5475b22b7a2b6e9e8a031c68a268a0b0b3"

S = "${WORKDIR}/git"

inherit autotools gettext pkgconfig

# we do not want libncursesw if we can
do_configure_prepend() {
    # configure.ac checks for delwin() in "ncursesw ncurses" so let's drop first one
    sed -i -e "s/ncursesw//g" ${S}/configure.ac
    mkdir -p ${B}/src/tuning/
    echo "${PV}" > ${S}/version-long
    echo "${PV}" > ${S}/version-short
}

inherit update-alternatives
ALTERNATIVE_${PN} = "powertop"
ALTERNATIVE_TARGET[powertop] = "${sbindir}/powertop"
ALTERNATIVE_LINK_NAME[powertop] = "${sbindir}/powertop"
ALTERNATIVE_PRIORITY = "100"
