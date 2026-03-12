SUMMARY = "An onscreen keyboard"
HOMEPAGE = "https://github.com/onboard-osk/onboard"
DESCRIPTION = "Onboard is an onscreen keyboard useful for everybody \
that cannot use a hardware keyboard. It has been designed with simplicity \
in mind and can be used right away without the need of any configuration, \
as it can read the keyboard layout from the X server."

LICENSE = "GPL-3.0-or-later & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING.GPL3;md5=8521fa4dd51909b407c5150498d34f4e \
                    file://COPYING.BSD3;md5=f56403ae5b2d6b82ad136d753c05a82e \
                   "

SRC_URI = "git://github.com/onboard-osk/onboard.git;protocol=https;branch=main \
           file://0001-pypredict-lm-Define-error-API-if-platform-does-not-h.patch \
           file://0002-toggle-onboard-hoverclick-use-bin-sh-default-shell-i.patch \
          "
SRCREV = "350f7643576bc8c5f2cff9c6ddce0e1e7cff995d"

inherit features_check setuptools3 pkgconfig gtk-icon-cache gsettings mime-xdg

REQUIRED_DISTRO_FEATURES = "x11"

DEPENDS = "gtk+3 hunspell libcanberra libxkbfile dconf hicolor-icon-theme python3-distutils-extra-native intltool-native glib-2.0-native"

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/icons \
    ${datadir}/gnome-shell \
    ${datadir}/help \
"

RDEPENDS:${PN} += " \
    ncurses \
    librsvg-gtk \
    python3-dbus \
    python3-pycairo \
    python3-pygobject \
    python3-image \
"
