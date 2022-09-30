SUMMARY = "The Corosync Cluster Engine and Application Programming Interfaces"
DESCRIPTION = "This package contains the Corosync Cluster Engine Executive, several default \
APIs and libraries, default configuration files, and an init script."
HOMEPAGE = "http://corosync.github.io/corosync/"

SECTION = "base"

inherit autotools pkgconfig systemd

SRC_URI = "https://github.com/${BPN}/${BPN}/releases/download/v${PV}/${BP}.tar.gz \
           file://corosync.conf \
          "
SRC_URI[sha256sum] = "ca6ed32b4d7f33ed614afce8760fe58d0de92c68b575d4969ebacd892f3d1e27"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+\.(?!99)\d+(\.\d+)+)"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a85eb4ce24033adb6088dd1d6ffc5e5d"

DEPENDS = "groff-native nss libqb kronosnet"

SYSTEMD_SERVICE:${PN} = "corosync.service corosync-notifyd.service"
SYSTEMD_AUTO_ENABLE = "disable"

INITSCRIPT_NAME = "corosync-daemon"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)} \
                   dbus snmp \
"

PACKAGECONFIG[dbus] = "--enable-dbus,--disable-dbus,dbus"
PACKAGECONFIG[snmp] = "--enable-snmp,--disable-snmp,net-snmp"
PACKAGECONFIG[systemd] = "--enable-systemd --with-systemddir=${systemd_system_unitdir},--disable-systemd --without-systemddir,systemd"

EXTRA_OECONF = "ac_cv_path_BASHPATH=${base_bindir}/bash ap_cv_cc_pie=no"
EXTRA_OEMAKE = "tmpfilesdir_DATA="

#do_configure:prepend() {
#    ( cd ${S}
#    ${S}/autogen.sh )
#}

do_install:append() {
    install -D -m 0644 ${WORKDIR}/corosync.conf ${D}${sysconfdir}/corosync/corosync.conf.example
    install -d ${D}${sysconfdir}/sysconfig/
    install -m 0644 ${S}/init/corosync.sysconfig.example ${D}${sysconfdir}/sysconfig/corosync
    install -m 0644 ${S}/tools/corosync-notifyd.sysconfig.example ${D}${sysconfdir}/sysconfig/corosync-notifyd

    rmdir ${D}${localstatedir}/log/cluster ${D}${localstatedir}/log
    rmdir --ignore-fail-on-non-empty ${D}${localstatedir}

    install -d ${D}${sysconfdir}/default/volatiles
    echo "d root root 0755 ${localstatedir}/log/cluster none" > ${D}${sysconfdir}/default/volatiles/05_corosync

    if [ ${@bb.utils.filter('DISTRO_FEATURES','systemd',d)} ]; then
        install -d ${D}${sysconfdir}/tmpfiles.d
        echo "d ${localstatedir}/log/cluster - - - -" > ${D}${sysconfdir}/tmpfiles.d/corosync.conf
    fi
}

RDEPENDS:${PN} += "bash ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'sysvinit-pidof', 'procps', d)}"

FILES:${PN}-dbg += "${libexecdir}/lcrso/.debug"
FILES:${PN}-doc += "${datadir}/snmp/mibs/COROSYNC-MIB.txt"
