SUMMARY = "GTK+ applet for NetworkManager"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "gtk+3 libnma libnotify libsecret libgudev networkmanager iso-codes nss"

GNOMEBASEBUILDCLASS = "meson"
inherit features_check gnomebase gsettings gtk-icon-cache gettext

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI += " file://0001-meson.build-address-meson-0.61-failures.patch"
SRC_URI[archive.sha256sum] = "b9f4bca5d0352718e07b7385fb195a9bbc8fd686b7959b74137854d52aab9c58"

# We don't not have ubuntu's appindicator (yet?)
EXTRA_OEMESON = "-Dappindicator=no"
# We currently don't build NetworkManager with libteamdctl support
EXTRA_OEMESON += "-Dteam=false"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'selinux', d)}"
PACKAGECONFIG[modemmanager] = "-Dwwan=true, -Dwwan=false, modemmanager"
PACKAGECONFIG[selinux] = "-Dselinux=true, -Dselinux=false, libselinux"

RDEPENDS:${PN} =+ "networkmanager"

FILES:${PN} += " \
    ${datadir}/nm-applet/ \
    ${datadir}/libnma/wifi.ui \
    ${datadir}/metainfo \
"
