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
           file://0008-use-oe-variable-to-replace-hardcoded-dir.patch \
           file://0009-invoking-fsck-with-infinite-timeout.patch \
           file://0010-invoking-mkfs-with-infinite-timeout.patch \
           file://0011-invoking-dd-with-infinite-timeout.patch \
"
SRC_URI[sha256sum] = "54775ba212d1574b1b0750ce147f0d3cf3b5d73aaf040d172283edb57db4ba15"

inherit pypi features_check systemd setuptools3_legacy

REQUIRED_DISTRO_FEATURES = "systemd"

RDEPENDS:${PN} += "python3-pykickstart python3-pyudev \
                   parted python3-pyparted multipath-tools \
                   lsof cryptsetup libblockdev libblockdev-bin \
                   libbytesize \
"

FILES:${PN} += " \
    ${datadir}/dbus-1/system-services \
"

SYSTEMD_AUTO_ENABLE = "disable"
SYSTEMD_SERVICE:${PN} = "blivet.service"
