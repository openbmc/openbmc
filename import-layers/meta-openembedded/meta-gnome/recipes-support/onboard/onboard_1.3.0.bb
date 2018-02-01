SUMMARY = "An onscreen keyboard"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING.GPL3;md5=8521fa4dd51909b407c5150498d34f4e"

DEPENDS += "gtk+3 hunspell libcanberra libxkbfile dconf python3-distutils-extra-native intltool-native"

SRC_URI = "https://launchpad.net/onboard/1.3/${PV}/+download/${BPN}-${PV}.tar.gz"
SRC_URI[md5sum] = "8000df3e789512a90fcb227580fd60ff"
SRC_URI[sha256sum] = "fd74b54b8bd7a075cf5f6e1a8ca3e6de5cd2663507adb690d7b1a85e71afa2e4"

inherit setuptools3 pkgconfig gtk-icon-cache gsettings

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
