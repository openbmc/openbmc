DESCRIPTION = "A python module for system storage configuration"
HOMEPAGE = "https://fedoraproject.org/wiki/blivet"
LICENSE = "LGPL-2.0-or-later"
SECTION = "devel/python"

LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI += "\
           file://0002-run_program-support-timeout.patch \
           file://0003-support-infinit-timeout.patch \
           file://0005-fix-incorrect-timeout-while-system-time-changed.patch \
           file://0006-tweak-btrfs-packages.patch \
           file://0007-invoking-mount-with-infinite-timeout.patch \
           file://0009-invoking-fsck-with-infinite-timeout.patch \
           file://0010-invoking-mkfs-with-infinite-timeout.patch \
           file://0011-invoking-dd-with-infinite-timeout.patch \
"
SRC_URI[sha256sum] = "6d8374d05eeab513b2a26cf01267e853df7b31e13ad1a1ba7d73a856190d0518"

inherit pypi features_check systemd setuptools3_legacy

REQUIRED_DISTRO_FEATURES = "systemd"

RDEPENDS:${PN} += "python3-dasbus python3-pygobject python3-pykickstart python3-pyudev \
                   parted python3-pyparted multipath-tools \
                   lsof cryptsetup libblockdev libblockdev-bin \
                   libbytesize \
                   util-linux \
"

do_install:append() {
    install -d ${D}${sysconfdir}/dbus-1/system.d
    install -m 644 ${S}/dbus/blivet.conf ${D}${sysconfdir}/dbus-1/system.d/blivet.conf
    install -d ${D}${datadir}/dbus-1/system-services
    install -m 644 ${S}/dbus/com.redhat.Blivet0.service ${D}${datadir}/dbus-1/system-services/com.redhat.Blivet0.service
    install -d ${D}${libexecdir}
    install -m 755 ${S}/dbus/blivetd ${D}${libexecdir}/blivetd
    install -d ${D}${systemd_system_unitdir}
    install -m 644 ${S}/dbus/blivet.service ${D}${systemd_system_unitdir}/blivet.service
}

FILES:${PN} += " \
    ${datadir}/dbus-1/system-services \
"

SYSTEMD_AUTO_ENABLE = "disable"
SYSTEMD_SERVICE:${PN} = "blivet.service"
