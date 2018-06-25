SUMMARY = "An onscreen keyboard"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING.GPL3;md5=8521fa4dd51909b407c5150498d34f4e"

DEPENDS += "gtk+3 hunspell libcanberra libxkbfile dconf python3-distutils-extra-native intltool-native"

SRC_URI = "https://launchpad.net/onboard/1.4/${PV}/+download/${BPN}-${PV}.tar.gz \
           file://0001-pypredict-lm-Define-error-API-if-platform-does-not-h.patch \
           "
SRC_URI[md5sum] = "1a2fbe82e934f5b37841d17ff51e80e8"
SRC_URI[sha256sum] = "01cae1ac5b1ef1ab985bd2d2d79ded6fc99ee04b1535cc1bb191e43a231a3865"

inherit distro_features_check setuptools3 pkgconfig gtk-icon-cache gsettings

REQUIRED_DISTRO_FEATURES = "x11"

FILES_${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/icons \
    ${datadir}/gnome-shell \
    ${datadir}/help \
"

RDEPENDS_${PN} += " \
    ncurses \
    python3-dbus \
    python3-pycairo \
    python3-pygobject \
"
