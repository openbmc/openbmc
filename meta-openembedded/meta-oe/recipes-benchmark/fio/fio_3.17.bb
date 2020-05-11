SUMMARY = "Filesystem and hardware benchmark and stress tool"
DESCRIPTION = "fio is an I/O tool meant to be used both for benchmark and \
stress/hardware verification. It has support for a number of I/O engines, \
I/O priorities (for newer Linux kernels), rate I/O, forked or threaded jobs, \
and much more. It can work on block devices as well as files. fio accepts \
job descriptions in a simple-to-understand text format. Several example job \
files are included. fio displays all sorts of I/O performance information."
HOMEPAGE = "http://freecode.com/projects/fio"
SECTION = "console/tests"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "libaio zlib coreutils-native"
DEPENDS += "${@bb.utils.contains('MACHINE_FEATURES', 'pmem', 'pmdk', '', d)}"
RDEPENDS_${PN} = "python3-core bash"

PACKAGECONFIG_NUMA = "numa"
# ARM does not currently support NUMA
PACKAGECONFIG_NUMA_arm = ""
PACKAGECONFIG_NUMA_armeb = ""

PACKAGECONFIG ??= "${PACKAGECONFIG_NUMA}"
PACKAGECONFIG[numa] = ",--disable-numa,numactl"

SRCREV = "08ce9dc20b8a4e55db7af6d869ddfa49b4a02d03"
SRC_URI = "git://git.kernel.dk/fio.git \
          file://0001-update-the-interpreter-paths.patch \
          file://python3_shebangs.patch \
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
