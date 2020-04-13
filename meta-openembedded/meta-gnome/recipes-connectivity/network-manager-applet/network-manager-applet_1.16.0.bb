SUMMARY = "GTK+ applet for NetworkManager"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "gtk+3 libnma libnotify libsecret networkmanager iso-codes nss"

GNOMEBASEBUILDCLASS = "meson"
inherit features_check gnomebase gsettings gtk-icon-cache gettext

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[archive.md5sum] = "9652c2757e85d6caba657405cf794fbd"
SRC_URI[archive.sha256sum] = "d6f98a455a271e7e169b5d35d290f4880f503cdf7593251572c9330941b5c3e5"

PACKAGECONFIG ??= ""
PACKAGECONFIG[modemmanager] = "-Dwwan=true, -Dwwan=false, modemmanager"
PACKAGECONFIG[selinux] = "-Dselinux=true, -Dselinux=false, libselinux"

RDEPENDS_${PN} =+ "networkmanager"

FILES_${PN} += " \
    ${datadir}/nm-applet/ \
    ${datadir}/libnma/wifi.ui \
    ${datadir}/metainfo \
"
