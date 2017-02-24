SUMMARY = "The Corosync Cluster Engine and Application Programming Interfaces"
DESCRIPTION = "This package contains the Corosync Cluster Engine Executive, several default \
APIs and libraries, default configuration files, and an init script."
HOMEPAGE = "http://corosync.github.io/corosync/"

SECTION = "base"

inherit autotools pkgconfig systemd

SRC_URI = "http://build.clusterlabs.org/corosync/releases/${BP}.tar.gz"
SRC_URI[md5sum] = "11bdd5ee2aed5eb2443dd6d6acd6a1ab"
SRC_URI[sha256sum] = "6fe9523852a892701c4c28c1cd32e067e44cf0e696d5ecf3790afdef1fc309cb"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a85eb4ce24033adb6088dd1d6ffc5e5d"

DEPENDS = "groff-native nss libqb"

SYSTEMD_SERVICE_${PN} = "corosync.service corosync-notifyd.service"
SYSTEMD_AUTO_ENABLE = "enable"

INITSCRIPT_NAME = "corosync-daemon"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)}"

PACKAGECONFIG[systemd] = "--enable-systemd --with-systemddir=${systemd_unitdir}/system/,--with-systemddir="

EXTRA_OECONF = "--with-upstartdir=%{_sysconfdir}/init"

do_configure_prepend() {
    ( cd ${S}
    ${S}/autogen.sh )
}

do_install_append() {
    install -d ${D}${sysconfdir}/sysconfig/
    install -d ${D}/${sysconfdir}/init.d
    install -m 0644 ${S}/init/corosync.sysconfig.example ${D}${sysconfdir}/sysconfig/corosync
    install -m 0644 ${S}/init/corosync-notifyd.conf.in ${D}${sysconfdir}/sysconfig/corosync-notifyd.conf
    install -m 0644 ${S}/init/corosync.conf.in ${D}${sysconfdir}/sysconfig/corosync.conf
    install -m 0644 ${S}/init/corosync.in ${D}${sysconfdir}/init.d/corosync
    install -m 0644 ${S}/init/corosync-notifyd.in ${D}${sysconfdir}/init.d/corosync-notifyd

    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -d ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/init/corosync.service.in ${D}${systemd_unitdir}/system/corosync.service
        install -m 0644 ${S}/init/corosync-notifyd.service.in ${D}${systemd_unitdir}/system/corosync-notifyd.service
        sed -i -e 's,@INITWRAPPERSDIR@,${sysconfdir}/init.d,g' ${D}${systemd_unitdir}/system/corosync.service
        sed -i -e 's,@SYSCONFDIR@,${sysconfdir},g' ${D}${systemd_unitdir}/system/corosync-notifyd.service
        sed -i -e 's,@SBINDIR@,${base_sbindir},g' ${D}${systemd_unitdir}/system/corosync-notifyd.service
    fi
}

RDEPENDS_${PN} += "bash"

FILES_${PN}-dbg += "${libexecdir}/lcrso/.debug"
