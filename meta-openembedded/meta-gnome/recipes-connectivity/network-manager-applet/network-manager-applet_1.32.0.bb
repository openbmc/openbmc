SUMMARY = "GTK+ applet for NetworkManager"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "gtk+3 libnma libnotify libsecret libgudev networkmanager iso-codes nss"

GNOMEBASEBUILDCLASS = "meson"
inherit features_check gnomebase gsettings gtk-icon-cache gettext pkgconfig

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"

SRC_URI:append:libc-musl = "${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-lld', ' file://0001-linker-scripts-Do-not-export-_IO_stdin_used.patch', '', d)}"

SRC_URI[archive.sha256sum] = "a2b5affa1505ad43902959fdbe09e5bcec57b11ed333fa60458ffb9c62efba38"

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
