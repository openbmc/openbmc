SUMMARY = "GTK+ applet for NetworkManager"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

DEPENDS = "gtk+3 libnotify libsecret networkmanager iso-codes nss"

GNOMEBASEBUILDCLASS = "meson"
inherit features_check gnomebase gsettings gtk-doc gtk-icon-cache gobject-introspection gettext

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[archive.md5sum] = "5c1bf351fde5adc12200345550516050"
SRC_URI[archive.sha256sum] = "118bbb8a5027634b62e8b45b16ceafce74441529c99bf230654e3bec38f9fbbf"

GTKDOC_MESON_OPTION = "gtk_doc"

PACKAGECONFIG ??= ""
PACKAGECONFIG[gcr] = "-Dgcr=true, -Dgcr=false, gcr"
PACKAGECONFIG[modemmanager] = "-Dwwan=true, -Dwwan=false, modemmanager"
PACKAGECONFIG[mobile-provider-info] = "-Dmobile_broadband_provider_info=true, -Dmobile_broadband_provider_info=false, mobile-broadband-provider-info,mobile-broadband-provider-info"
PACKAGECONFIG[selinux] = "-Dselinux=true, -Dselinux=false, libselinux"

RDEPENDS_${PN} =+ "networkmanager"

FILES_${PN} += " \
    ${datadir}/nm-applet/ \
    ${datadir}/libnma/wifi.ui \
    ${datadir}/metainfo \
"
