SUMMARY = "Meta-filesystem packagegroups"

inherit packagegroup

PROVIDES = "${PACKAGES}"
PACKAGES = ' \
    packagegroup-meta-filesystems \
    packagegroup-meta-filesystems-support \
    packagegroup-meta-filesystems-utls \
'

RDEPENDS_packagegroup-meta-filesystems = "\
    packagegroup-meta-filesystems \
    packagegroup-meta-filesystems-support \
    packagegroup-meta-filesystems-utls \
"

RDEPENDS_packagegroup-meta-filesystems = "\
    ntfs-3g-ntfsprogs ifuse sshfs-fuse \
    logfsprogs owfs simple-mtpfs \
    unionfs-fuse fuse-exfat yaffs2-utils \
    ${@bb.utils.contains("DISTRO_FEATURES", "pam", "smbnetfs", "", d)} \
    "

RDEPENDS_packagegroup-meta-filesystems-support = "\
    physfs fuse \
    "

RDEPENDS_packagegroup-meta-filesystems-utils = "\
    xorriso aufs-util xfsprogs \
    f2fs-tools exfat-utils udevil \
    xfsdump \
    "

EXCLUDE_FROM_WORLD = "1"
