SUMMARY = "File system QA test suite"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSES/GPL-2.0;md5=74274e8a218423e49eefdea80bc55038"

SRCREV = "92c428db1fe3fa4f7bd2593788bc9b71403d59ae"
SRCREV_unionmount = "c6ab621ac19f2b96d34cd98f244e611750e2bb23"
SRCREV_FORMAT = "default_unionmount"

SRC_URI = "git://git.kernel.org/pub/scm/fs/xfs/xfstests-dev.git;branch=master;tag=v${PV} \
           git://github.com/amir73il/unionmount-testsuite.git;branch=master;protocol=https;name=unionmount;destsuffix=unionmount-testsuite \
           file://0001-add-missing-FTW_-macros-when-not-available-in-libc.patch \
           file://0002-Drop-detached_mounts_propagation-and-remove-sys-moun.patch \
           file://0001-include-libgen.h-for-basename-API-prototype.patch \
           file://0002-Add-missing-STATX_ATTR_-defines-from-musl-sys-stat.h.patch \
           file://0001-bstat-use-uint32_t-instead-of-__uint32_t-to-fix-buil.patch \
           "


# brokensep because m4/package_globals.m4 calls ". ./VERSION" (and that's not the only issue)
inherit autotools-brokensep useradd pkgconfig

DEPENDS += "xfsprogs acl"
RDEPENDS:${PN} += "\
    bash \
    bc \
    coreutils \
    e2fsprogs \
    e2fsprogs-tune2fs \
    e2fsprogs-resize2fs \
    libaio \
    libcap-bin \
    overlayfs-tools \
    perl \
    python3 \
    python3-core \
    xfsprogs \
    acl \
    gawk \
    util-linux-mkfs \
    util-linux-mount \
    util-linux-findmnt \
    inetutils-hostname \
    grep \
"

USERADD_PACKAGES = "${PN}"
# these users are necessary to run the tests
USERADD_PARAM:${PN} = "-U -m fsgqa; -N 123456-fsgqa; -N fsgqa2"

EXTRA_OECONF = "INSTALL_USER=root INSTALL_GROUP=root"

TARGET_CC_ARCH:append:libc-musl = " -D_LARGEFILE64_SOURCE"

do_configure:prepend() {
    # this is done by Makefile configure target, but we don't call it in do_configure
    cp -a ${S}/include/install-sh .
}

do_install() {
    # otherwise install-sh duplicates DESTDIR prefix
    export DIST_ROOT="/" DIST_MANIFEST="" DESTDIR="${D}"
    oe_runmake install

    unionmount_target_dir=${D}${prefix}/xfstests/unionmount-testsuite
    install -d $unionmount_target_dir/tests
    install ${UNPACKDIR}/unionmount-testsuite/tests/* -t $unionmount_target_dir/tests
    install ${UNPACKDIR}/unionmount-testsuite/*.py -t $unionmount_target_dir
    install ${UNPACKDIR}/unionmount-testsuite/run -t $unionmount_target_dir
    install ${UNPACKDIR}/unionmount-testsuite/README -t $unionmount_target_dir
}

FILES:${PN} += "${prefix}/xfstests"

# This one is reproducible only on 32bit MACHINEs
# http://errors.yoctoproject.org/Errors/Details/766963/
# lstat64.c:65:14: error: passing argument 1 of 'time' from incompatible pointer type [-Wincompatible-pointer-types]
# bstat.c:18:19: error: passing argument 1 of 'ctime' from incompatible pointer type [-Wincompatible-pointer-types]
CFLAGS += "-Wno-error=incompatible-pointer-types"
