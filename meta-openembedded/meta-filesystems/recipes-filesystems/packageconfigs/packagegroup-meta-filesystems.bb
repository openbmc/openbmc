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
    ifuse \
    logfsprogs \
    fuse-exfat \
    owfs \
    ${@bb.utils.contains("DISTRO_FEATURES", "pam", "smbnetfs", "", d)} \
    simple-mtpfs \
    yaffs2-utils \
    ntfs-3g-ntfsprogs \
    httpfs2 \
    unionfs-fuse \
    sshfs-fuse \
"

RDEPENDS_packagegroup-meta-filesystems-support = "\
    fuse3 \
    fuse \
    physfs \
"

RDEPENDS_packagegroup-meta-filesystems-utils = "\
    aufs-util \
    exfat-utils \
    fatcat \
    xfsdump \
    f2fs-tools \
    fatresize \
    udevil \
    ufs-utils \
    xfsprogs \
    xorriso \
"

EXCLUDE_FROM_WORLD = "1"
