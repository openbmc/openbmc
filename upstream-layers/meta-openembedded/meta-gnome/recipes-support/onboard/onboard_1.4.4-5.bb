SUMMARY = "An onscreen keyboard"
HOMEPAGE = "https://github.com/onboard-osk/onboard"
DESCRIPTION = "Onboard is an onscreen keyboard useful for everybody \
that cannot use a hardware keyboard. It has been designed with simplicity \
in mind and can be used right away without the need of any configuration, \
as it can read the keyboard layout from the X server."

LICENSE = "BSD-3-Clause AND GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING.GPL3;md5=8521fa4dd51909b407c5150498d34f4e \
                    file://COPYING.BSD3;md5=f56403ae5b2d6b82ad136d753c05a82e \
                   "

SRC_URI = "git://github.com/onboard-osk/onboard.git;protocol=https;branch=main;tag=v${PV} \
           file://0002-toggle-onboard-hoverclick-use-bin-sh-default-shell-i.patch \
          "
SRCREV = "5fa181edca5daf2450a63ca842a913cc0255c030"

UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+(\.\d+)+(-\d+)?)(\^\{\})$"

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
