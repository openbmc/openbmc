SUMMARY = "Power manager for the Xfce desktop environment"
HOMEPAGE = "http://goodies.xfce.org/projects/applications/xfce4-power-manager"
SECTION = "x11"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

inherit xfce features_check

REQUIRED_DISTRO_FEATURES = "x11"

DEPENDS += "libnotify libxrandr virtual/libx11 libxext xfce4-panel upower libxscrnsaver"

SRC_URI[sha256sum] = "e1608fd534b6b07529c0840ffc731cb93347ee6deb547a9933215b1816dcdf4d"

EXTRA_OECONF = " \
    --enable-network-manager \
    --enable-panel-plugins \
"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'polkit', d)}"
PACKAGECONFIG[polkit] = "--enable-polkit, --disable-polkit, polkit"

PACKAGES += "xfce4-powermanager-plugin"

FILES:${PN} += " \
    ${datadir}/polkit-1 \
    ${datadir}/metainfo \
"

FILES:xfce4-powermanager-plugin = " \
    ${libdir}/xfce4 \
    ${datadir}/xfce4 \
"

RDEPENDS:xfce4-powermanager-plugin = "${PN}"
RDEPENDS:${PN} = "networkmanager ${@bb.utils.contains('DISTRO_FEATURES','systemd','','consolekit',d)}"

# xfce4-brightness-plugin was replaced by xfce4-powermanager-plugin
RPROVIDES:xfce4-powermanager-plugin += "xfce4-brightness-plugin"
RREPLACES:xfce4-powermanager-plugin += "xfce4-brightness-plugin"
RCONFLICTS:xfce4-powermanager-plugin += "xfce4-brightness-plugin"
