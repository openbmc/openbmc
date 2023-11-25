SUMMARY = "File system QA test suite"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSES/GPL-2.0;md5=74274e8a218423e49eefdea80bc55038"

SRCREV = "11914614784735c504f43b5b6baabaa713375984"
SRCREV_FORMAT = "xfstests_unionmount"

SRC_URI = "git://git.kernel.org/pub/scm/fs/xfs/xfstests-dev.git;branch=for-next;name=xfstests \
           git://github.com/amir73il/unionmount-testsuite.git;branch=master;protocol=https;name=unionmount;destsuffix=unionmount-testsuite \
           file://0001-ltp-fsx.h-Explicitly-use-int-for-return-type-for-aio.patch \
           file://0002-Drop-detached_mounts_propagation-and-remove-sys-moun.patch \
           file://0001-add-missing-FTW_-macros-when-not-available-in-libc.patch \
           "

SRCREV_xfstests = "f7765774a1b5cb98c2f21a892e82b3421f40e791"
SRCREV_unionmount = "e3825b16b46f4c4574a1a69909944c059835f914"

S = "${WORKDIR}/git"

inherit autotools-brokensep useradd

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
"

USERADD_PACKAGES = "${PN}"
# these users are necessary to run the tests
USERADD_PARAM:${PN} = "-U -m fsgqa; -N 123456-fsgqa; -N fsgqa2"

EXTRA_OECONF = "INSTALL_USER=root INSTALL_GROUP=root"

TARGET_CC_ARCH:append:libc-musl = " -D_LARGEFILE64_SOURCE"
# install-sh script in the project is outdated
# we use the one from the latest libtool to solve installation issues
# It looks like the upstream is not interested in having it fixed :(
# https://www.spinics.net/lists/fstests/msg16981.html
do_configure:prepend() {
    cp ${STAGING_DIR_NATIVE}${datadir}/libtool/build-aux/install-sh ${B}
}

do_install:append() {
    unionmount_target_dir=${D}/usr/xfstests/unionmount-testsuite
    install -d ${D}/usr/xfstests/unionmount-testsuite/tests
    install -D ${WORKDIR}/unionmount-testsuite/tests/* -t $unionmount_target_dir/tests
    install ${WORKDIR}/unionmount-testsuite/*.py -t $unionmount_target_dir
    install ${WORKDIR}/unionmount-testsuite/run -t $unionmount_target_dir
    install ${WORKDIR}/unionmount-testsuite/README -t $unionmount_target_dir
}

FILES:${PN} += "\
    /usr/xfstests \
"
