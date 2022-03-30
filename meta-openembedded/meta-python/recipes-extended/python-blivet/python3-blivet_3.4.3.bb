DESCRIPTION = "A python module for system storage configuration"
HOMEPAGE = "http://fedoraproject.org/wiki/blivet"
LICENSE = "LGPL-2.0-or-later"
SECTION = "devel/python"

LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI += "\
           file://0002-run_program-support-timeout.patch \
           file://0003-support-infinit-timeout.patch \
           file://0004-fix-new.roots-object-is-not-iterable.patch \
           file://0005-fix-incorrect-timeout-while-system-time-changed.patch \
           file://0006-tweak-btrfs-packages.patch \
           file://0007-invoking-mount-with-infinite-timeout.patch \
           file://0008-use-oe-variable-to-replace-hardcoded-dir.patch \
           file://0009-invoking-fsck-with-infinite-timeout.patch \
           file://0010-invoking-mkfs-with-infinite-timeout.patch \
           file://0011-invoking-dd-with-infinite-timeout.patch \
"
SRC_URI[sha256sum] = "1b05b77f3ee35d82c7a577a168c9ba0204d3e9a87eb1975e5f9af47700eeff48"

inherit pypi features_check systemd setuptools3_legacy

REQUIRED_DISTRO_FEATURES = "systemd"

RDEPENDS:${PN} += "python3-pykickstart python3-pyudev \
                  parted python3-pyparted multipath-tools \
                  lsof cryptsetup libblockdev \
                  libbytesize \
"

FILES:${PN} += " \
    ${datadir}/dbus-1/system-services \
"

SYSTEMD_AUTO_ENABLE = "disable"
SYSTEMD_SERVICE:${PN} = "blivet.service"
