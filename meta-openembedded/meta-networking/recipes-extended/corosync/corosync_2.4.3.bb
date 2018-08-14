SUMMARY = "The Corosync Cluster Engine and Application Programming Interfaces"
DESCRIPTION = "This package contains the Corosync Cluster Engine Executive, several default \
APIs and libraries, default configuration files, and an init script."
HOMEPAGE = "http://corosync.github.io/corosync/"

SECTION = "base"

inherit autotools pkgconfig systemd useradd

SRC_URI = "http://build.clusterlabs.org/corosync/releases/${BP}.tar.gz"

SRC_URI[md5sum] = "bf0ede13018aadc8a046183bc593b0e0"
SRC_URI[sha256sum] = "b1e7e422e8d2f7af8e5315fa329b1dbd1175fff32efaa55e8054584c80a2c597"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a85eb4ce24033adb6088dd1d6ffc5e5d"

DEPENDS = "groff-native nss libqb"

SYSTEMD_SERVICE_${PN} = "corosync.service corosync-notifyd.service \
                         ${@bb.utils.contains('PACKAGECONFIG', 'qdevice', 'corosync-qdevice.service', '', d)} \
                         ${@bb.utils.contains('PACKAGECONFIG', 'qnetd', 'corosync-qnetd.service', '', d)} \
"
SYSTEMD_AUTO_ENABLE = "disable"

INITSCRIPT_NAME = "corosync-daemon"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)} \
                   dbus qdevice qnetd snmp \
"

PACKAGECONFIG[dbus] = "--enable-dbus,--disable-dbus,dbus"
PACKAGECONFIG[qdevice] = "--enable-qdevices,--disable-qdevices"
PACKAGECONFIG[qnetd] = "--enable-qnetd,--disable-qnetd"
PACKAGECONFIG[rdma] = "--enable-rdma,--disable-rdma"
PACKAGECONFIG[snmp] = "--enable-snmp,--disable-snmp,net-snmp"
PACKAGECONFIG[systemd] = "--enable-systemd --with-systemddir=${systemd_system_unitdir},--disable-systemd --without-systemddir,systemd"

EXTRA_OECONF = "ac_cv_path_BASHPATH=${base_bindir}/bash ap_cv_cc_pie=no"
EXTRA_OEMAKE = "tmpfilesdir_DATA="

do_configure_prepend() {
    ( cd ${S}
    ${S}/autogen.sh )
}

do_install_append() {
    install -d ${D}${sysconfdir}/sysconfig/
    install -m 0644 ${S}/init/corosync.sysconfig.example ${D}${sysconfdir}/sysconfig/corosync
    install -m 0644 ${S}/tools/corosync-notifyd.sysconfig.example ${D}${sysconfdir}/sysconfig/corosync-notifyd

    rm -rf "${D}${localstatedir}/run"

    install -d ${D}${sysconfdir}/default/volatiles
    echo "d root root 0755 ${localstatedir}/log/cluster none" > ${D}${sysconfdir}/default/volatiles/05_corosync

    if [ ${@bb.utils.filter('PACKAGECONFIG', 'qnetd', d)} ]; then
        chown -R coroqnetd:coroqnetd ${D}${sysconfdir}/${BPN}/qnetd
        echo "d coroqnetd coroqnetd 0770 /var/run/corosync-qnetd none" >> ${D}${sysconfdir}/default/volatiles/05_corosync
    fi

    if [ ${@bb.utils.filter('DISTRO_FEATURES','systemd',d)} ]; then
        install -d ${D}${sysconfdir}/tmpfiles.d
        echo "d ${localstatedir}/log/cluster - - - -" > ${D}${sysconfdir}/tmpfiles.d/corosync.conf

        if [ ${@bb.utils.filter('PACKAGECONFIG', 'qnetd', d)} ]; then
            install -m 0644 ${S}/conf/tmpfiles.d/corosync-qnetd.conf ${D}${sysconfdir}/tmpfiles.d
        fi
    fi
}

RDEPENDS_${PN} += "bash ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'sysvinit-pidof', 'procps', d)}"

FILES_${PN}-dbg += "${libexecdir}/lcrso/.debug"
FILES_${PN}-doc += "${datadir}/snmp/mibs/COROSYNC-MIB.txt"

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN} = "--system coroqnetd"
USERADD_PARAM_${PN} = "--system -d / -M -s /bin/nologin -c 'User for corosync-qnetd' -g coroqnetd coroqnetd"
