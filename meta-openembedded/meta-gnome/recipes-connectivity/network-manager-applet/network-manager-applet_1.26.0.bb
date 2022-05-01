SUMMARY = "GTK+ applet for NetworkManager"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "gtk+3 libnma libnotify libsecret libgudev networkmanager iso-codes nss"

GNOMEBASEBUILDCLASS = "meson"
inherit features_check gnomebase gsettings gtk-icon-cache gettext

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[archive.sha256sum] = "69611b29064adbd57395fe3e51a9ebde1ea794615f776900453a2bbe3d8cddfd"

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
