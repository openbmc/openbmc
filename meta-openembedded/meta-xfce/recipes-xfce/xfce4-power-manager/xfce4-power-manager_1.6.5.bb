SUMMARY = "Power manager for the Xfce desktop environment"
HOMEPAGE = "http://goodies.xfce.org/projects/applications/xfce4-power-manager"
SECTION = "x11"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

inherit xfce features_check

REQUIRED_DISTRO_FEATURES = "x11"

DEPENDS += "libnotify libxrandr virtual/libx11 libxext xfce4-panel upower libxscrnsaver"

SRC_URI[md5sum] = "709efbc2de9ed84b4831847ff70bcd7f"
SRC_URI[sha256sum] = "10adb67899b181ca5fc577fc9bb7a698fb94e42073585f7e2be642c7db127a74"

EXTRA_OECONF = " \
    --enable-network-manager \
    --enable-panel-plugins \
"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'polkit', d)}"
PACKAGECONFIG[polkit] = "--enable-polkit, --disable-polkit, polkit"

PACKAGES += "xfce4-powermanager-plugin"

FILES_${PN} += " \
    ${datadir}/polkit-1 \
    ${datadir}/metainfo \
"

FILES_xfce4-powermanager-plugin = " \
    ${libdir}/xfce4 \
    ${datadir}/xfce4 \
"

RDEPENDS_xfce4-powermanager-plugin = "${PN}"
RDEPENDS_${PN} = "networkmanager ${@bb.utils.contains('DISTRO_FEATURES','systemd','','consolekit',d)}"

# xfce4-brightness-plugin was replaced by xfce4-powermanager-plugin
RPROVIDES_xfce4-powermanager-plugin += "xfce4-brightness-plugin"
RREPLACES_xfce4-powermanager-plugin += "xfce4-brightness-plugin"
RCONFLICTS_xfce4-powermanager-plugin += "xfce4-brightness-plugin"
