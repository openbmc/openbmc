SUMMARY = "GTK+ applet for NetworkManager"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "gtk+3 libnma libnotify libsecret libgudev networkmanager iso-codes nss"

GNOMEBASEBUILDCLASS = "meson"
inherit features_check gnomebase gsettings gtk-icon-cache gettext

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[archive.sha256sum] = "ddbb400ace804b59cc513611ce9701f7ef3f00de151dbbfb96284c8c1ef2b18b"

# We don't not have ubuntu's appindicator (yet?)
EXTRA_OEMESON = "-Dappindicator=no"
# We currently don't build NetworkManager with libteamdctl support
EXTRA_OEMESON += "-Dteam=false"

PACKAGECONFIG ??= ""
PACKAGECONFIG[modemmanager] = "-Dwwan=true, -Dwwan=false, modemmanager"
PACKAGECONFIG[selinux] = "-Dselinux=true, -Dselinux=false, libselinux"

RDEPENDS_${PN} =+ "networkmanager"

FILES_${PN} += " \
    ${datadir}/nm-applet/ \
    ${datadir}/libnma/wifi.ui \
    ${datadir}/metainfo \
"
