DESCRIPTION = "A python module for system storage configuration"
HOMEPAGE = "http://fedoraproject.org/wiki/blivet"
LICENSE = "LGPLv2+"
SECTION = "devel/python"

LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

S = "${WORKDIR}/git"
B = "${S}"

SRCREV = "ddc24c71bad10208b11ea0c45853898fadd7030b"
SRC_URI = "git://github.com/rhinstaller/blivet;branch=3.1-release \
           file://0001-comment-out-selinux.patch \
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
           file://0012-remove-dmraid-dependency-check.patch \
           file://0013-add-Z-y-and-y-to-lvm.pvcreate.patch \
"

UPSTREAM_CHECK_GITTAGREGEX = "blivet-(?P<pver>\d+(\.\d+)+)$"

inherit distro_features_check
REQUIRED_DISTRO_FEATURES = "systemd"

inherit setuptools3 python3native

RDEPENDS_${PN} = "python3-pykickstart python3-pyudev \
                  parted python3-pyparted multipath-tools \
                  lsof cryptsetup libblockdev \
                  libbytesize \
"

FILES_${PN} += " \
    ${datadir}/dbus-1/system-services \
"

inherit systemd

SYSTEMD_AUTO_ENABLE = "disable"
SYSTEMD_SERVICE_${PN} = "blivet.service"
