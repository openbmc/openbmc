SUMMARY = "Power usage tool"
DESCRIPTION = "Linux tool to diagnose issues with power consumption and power management."
HOMEPAGE = "http://01.org/powertop/"
BUGTRACKER = "http://bugzilla.lesswatts.org/"
DEPENDS = "ncurses libnl pciutils"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=12f884d2ae1ff87c09e5b7ccc2c4ca7e"

SRC_URI = "http://01.org/sites/default/files/downloads/powertop/powertop-v${PV}.tar.gz \
"

SRC_URI[md5sum] = "583518c5c4434c6e9b9c58c3920950b6"
SRC_URI[sha256sum] = "aa7fb7d8e9a00f05e7d8a7a2866d85929741e0d03a5bf40cab22d2021c959250"

UPSTREAM_CHECK_URI = "https://01.org/powertop/downloads"
UPSTREAM_CHECK_REGEX = "powertop-[v]?(?P<pver>\d+(\.\d+)+)\.tar"

inherit autotools gettext pkgconfig

S = "${WORKDIR}/${BPN}-v${PV}"

# we do not want libncursesw if we can
do_configure_prepend() {
    # configure.ac checks for delwin() in "ncursesw ncurses" so let's drop first one
    sed -i -e "s/ncursesw//g" ${S}/configure.ac
    mkdir -p ${B}/src/tuning/
}

inherit update-alternatives
ALTERNATIVE_${PN} = "powertop"
ALTERNATIVE_TARGET[powertop] = "${sbindir}/powertop"
ALTERNATIVE_LINK_NAME[powertop] = "${sbindir}/powertop"
ALTERNATIVE_PRIORITY = "100"
