SUMMARY = "Power usage tool"
DESCRIPTION = "Linux tool to diagnose issues with power consumption and power management."
HOMEPAGE = "http://01.org/powertop/"
BUGTRACKER = "http://bugzilla.lesswatts.org/"
DEPENDS = "ncurses libnl pciutils"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=12f884d2ae1ff87c09e5b7ccc2c4ca7e"

SRC_URI = "http://01.org/sites/default/files/downloads/powertop/powertop-${PV}.tar.gz \
           file://0001-include-rquired-headers-for-typedefs.patch \
"

SRC_URI[md5sum] = "c55fedb69203e480801b18bd7b886241"
SRC_URI[sha256sum] = "a87b563f73106babfa3e74dcf92f252938c061e309ace20a361358bbfa579c5a"

UPSTREAM_CHECK_URI = "https://01.org/powertop/downloads"

inherit autotools gettext pkgconfig

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
