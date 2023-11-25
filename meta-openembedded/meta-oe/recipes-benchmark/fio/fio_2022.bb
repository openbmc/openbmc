SUMMARY = "Filesystem and hardware benchmark and stress tool"
DESCRIPTION = "fio is an I/O tool meant to be used both for benchmark and \
stress/hardware verification. It has support for a number of I/O engines, \
I/O priorities (for newer Linux kernels), rate I/O, forked or threaded jobs, \
and much more. It can work on block devices as well as files. fio accepts \
job descriptions in a simple-to-understand text format. Several example job \
files are included. fio displays all sorts of I/O performance information."
HOMEPAGE = "http://freecode.com/projects/fio"
SECTION = "console/tests"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "libaio zlib coreutils-native"
DEPENDS += "${@bb.utils.contains('MACHINE_FEATURES', 'pmem', 'pmdk', '', d)}"
RDEPENDS:${PN} = "python3-core bash"

PACKAGECONFIG_NUMA = "numa"
# ARM does not currently support NUMA
PACKAGECONFIG_NUMA:arm = ""
PACKAGECONFIG_NUMA:armeb = ""

PACKAGECONFIG ??= "${PACKAGECONFIG_NUMA}"
PACKAGECONFIG[numa] = ",--disable-numa,numactl"

SRCREV = "6e44f31b9241cdc56d0857fb10ddb2ec40faa541"
SRC_URI = "git://git.kernel.dk/fio.git;branch=master \
           file://0001-Fio-3.31.patch \
           file://0002-lib-rand-Enhance-__fill_random_buf-using-the-multi-r.patch \
           file://0003-lib-rand-get-rid-of-unused-MAX_SEED_BUCKETS.patch \
           file://0004-ioengines-merge-filecreate-filestat-filedelete-engin.patch \
           file://0005-engines-http-Add-storage-class-option-for-s3.patch \
           file://0006-engines-http-Add-s3-crypto-options-for-s3.patch \
           file://0007-doc-Add-usage-and-example-about-s3-storage-class-and.patch \
           file://0008-README-link-to-GitHub-releases-for-Windows.patch \
           file://0009-engines-xnvme-fix-segfault-issue-with-xnvme-ioengine.patch \
           file://0010-doc-update-fio-doc-for-xnvme-engine.patch \
           file://0011-test-add-latency-test-using-posixaio-ioengine.patch \
           file://0012-test-fix-hash-for-t0016.patch \
           file://0013-doc-get-rid-of-trailing-whitespace.patch \
           file://0014-doc-clarify-that-I-O-errors-may-go-unnoticed-without.patch \
           file://0015-Revert-Minor-style-fixups.patch \
           file://0016-Revert-Fix-multithread-issues-when-operating-on-a-si.patch \
           file://0017-Add-wait-for-handling-SIGBREAK.patch \
           file://0018-engines-io_uring-pass-back-correct-error-value-when-.patch \
           file://0019-Enable-CPU-affinity-support-on-Android.patch \
           file://0020-io_uring-Replace-pthread_self-with-s-tid.patch \
           file://0021-engines-io_uring-delete-debug-code.patch \
           file://0022-t-io_uring-prep-for-including-engines-nvme.h-in-t-io.patch \
           file://0023-t-io_uring-add-support-for-async-passthru.patch \
           file://0024-t-io_uring-fix-64-bit-cast-on-32-bit-archs.patch \
           file://0025-test-add-basic-test-for-io_uring-ioengine.patch \
           file://0026-t-io_uring-remove-duplicate-definition-of-gettid.patch \
           file://0027-test-add-some-tests-for-seq-and-rand-offsets.patch \
           file://0028-test-use-Ubuntu-22.04-for-64-bit-tests.patch \
           file://0029-test-get-32-bit-Ubuntu-22.04-build-working.patch \
           file://0030-test-add-tests-for-lfsr-and-norandommap.patch \
           file://0031-backend-revert-bad-memory-leak-fix.patch \
           file://0032-Fio-3.32.patch \
           "

S = "${WORKDIR}/git"

# avoids build breaks when using no-static-libs.inc
DISABLE_STATIC = ""

EXTRA_OEMAKE = "CC='${CC}' LDFLAGS='${LDFLAGS}'"
EXTRA_OECONF = "${@bb.utils.contains('MACHINE_FEATURES', 'x86', '--disable-optimizations', '', d)}"

do_configure() {
    ./configure ${EXTRA_OECONF}
}

do_install() {
    oe_runmake install DESTDIR=${D} prefix=${prefix} mandir=${mandir}
    install -d ${D}/${docdir}/${PN}
    cp -R --no-dereference --preserve=mode,links -v ${S}/examples ${D}/${docdir}/${PN}/
}
