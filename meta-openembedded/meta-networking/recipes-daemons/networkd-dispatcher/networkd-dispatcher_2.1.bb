SUMMARY = "Dispatcher service for systemd-networkd connection status changes"
DESCRIPTION = "This daemon is similar to NetworkManager-dispatcher, but is much \
more limited in the types of events it supports due to the limited nature of \
systemd-networkd(8)."
AUTHOR = "Clayton Craft and others"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://LICENSE;md5=84dcc94da3adb52b53ae4fa38fe49e5d"

inherit features_check systemd

RDEPENDS:${PN} = "python3-pygobject python3-dbus"
REQUIRED_DISTRO_FEATURES = "systemd"

SRCREV = "30e278e50749a60a930ceaa0971207c6436b8a0c"
SRC_URI = "git://gitlab.com/craftyguy/networkd-dispatcher;protocol=https;nobranch=1"

S = "${WORKDIR}/git"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "networkd-dispatcher.service"
SYSTEMD_AUTO_ENABLE = "disable"

# Nothing to build, just a python script to install
do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -D -m 0755 ${S}/networkd-dispatcher ${D}${bindir}/networkd-dispatcher
    install -D -m 0644 ${S}/networkd-dispatcher.service ${D}/${systemd_system_unitdir}/networkd-dispatcher.service
    install -D -m 0644 ${S}/networkd-dispatcher.conf ${D}/${sysconfdir}/conf.d/networkd-dispatcher.conf
}
