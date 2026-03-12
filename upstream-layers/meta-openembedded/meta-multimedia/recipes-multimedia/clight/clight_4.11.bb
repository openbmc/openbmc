SUMMARY = "User daemon for automatic display management and ambient light adaptation"
HOMEPAGE = "https://github.com/FedeDP/Clight"
SECTION = "utils"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRCREV = "6a5ad96ce7643c433ea2f6bd597bc52dcc744120"
SRC_URI = "git://github.com/FedeDP/${BPN};protocol=https;branch=master;tag=${PV} \
"

inherit cmake pkgconfig features_check

REQUIRED_DISTRO_FEATURES = "polkit systemd"

DEPENDS += "\
    dbus \
    gsl \
    libconfig \
    libmodule \
    popt \
    systemd \
    udev \
"

RDEPENDS:${PN} = "clightd"

FILES:${PN} += " \
    ${libdir}/* \
    ${datadir}/* \
"
