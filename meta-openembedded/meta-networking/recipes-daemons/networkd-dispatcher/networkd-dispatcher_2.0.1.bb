python __anonymous() {
    features = d.getVar('DISTRO_FEATURES')
    if not features or 'systemd' not in features:
        raise bb.parse.SkipPackage('networkd-dispatcher needs systemd in DISTRO_FEATURES')
}

SUMMARY = "Dispatcher service for systemd-networkd connection status changes"
DESCRIPTION = "This daemon is similar to NetworkManager-dispatcher, but is much \
more limited in the types of events it supports due to the limited nature of \
systemd-networkd(8)."
AUTHOR = "Clayton Craft and others"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://LICENSE;md5=84dcc94da3adb52b53ae4fa38fe49e5d"

RDEPENDS_${PN} = "systemd python3 python3-pygobject python3-dbus"

SRC_URI = "https://gitlab.com/craftyguy/networkd-dispatcher/-/archive/${PV}/networkd-dispatcher-${PV}.tar.bz2"
SRC_URI[md5sum] = "304d7dcc21331ea295e207f8493cb8d8"
SRC_URI[sha256sum] = "21f84c3646a043329dc64787e4e58dfce592b2559b0e3069af82c469805660c2"

# Nothing to build, just a python script to install
do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -D -m 0755 ${S}/networkd-dispatcher ${D}${bindir}/networkd-dispatcher
    install -D -m 0644 ${S}/networkd-dispatcher.service ${D}/${systemd_system_unitdir}/networkd-dispatcher.service
    install -D -m 0644 ${S}/networkd-dispatcher.conf ${D}/${sysconfdir}/conf.d/networkd-dispatcher.conf
}

FILES_${PN} += "${systemd_system_unitdir}/networkd-dispatcher.service"
