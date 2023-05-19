SUMMARY = "Meta-filesystem packagegroups"

PACKAGE_ARCH = "${TUNE_PKGARCH}"
inherit packagegroup

PROVIDES = "${PACKAGES}"
PACKAGES = ' \
    packagegroup-meta-filesystems \
    packagegroup-meta-filesystems-support \
    packagegroup-meta-filesystems-utls \
'

RDEPENDS:packagegroup-meta-filesystems = "\
    packagegroup-meta-filesystems \
    packagegroup-meta-filesystems-support \
    packagegroup-meta-filesystems-utls \
"

RDEPENDS:packagegroup-meta-filesystems = "\
    ifuse \
    libisofs \
    libburn \
    libisoburn \
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

RDEPENDS:packagegroup-meta-filesystems-support = "\
    fuse3 \
    fuse \
    physfs \
"

RDEPENDS:packagegroup-meta-filesystems-utils = "\
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
