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

DEPENDS = "libaio zlib"
RDEPENDS_${PN} = "python bash"

PACKAGECONFIG_NUMA = "numa"
# ARM does not currently support NUMA
PACKAGECONFIG_NUMA_arm = ""

PACKAGECONFIG ??= "${PACKAGECONFIG_NUMA}"
PACKAGECONFIG[numa] = ",--disable-numa,numactl"

# rev for v3.8
SRCREV = "16500b5a0b03ee0142d592bb74a46943a223b06e"
SRC_URI = "git://git.kernel.dk/fio.git \
          file://0001-update-the-interpreter-paths.patch \
"

S = "${WORKDIR}/git"

# avoids build breaks when using no-static-libs.inc
DISABLE_STATIC = ""

EXTRA_OEMAKE = "CC='${CC}' LDFLAGS='${LDFLAGS}'"
EXTRA_OECONF = "--disable-optimizations"

do_configure() {
    ./configure ${EXTRA_OECONF}
}

do_install() {
    oe_runmake install DESTDIR=${D} prefix=${prefix} mandir=${mandir}
    install -d ${D}/${docdir}/${PN}
    cp -R --no-dereference --preserve=mode,links -v ${S}/examples ${D}/${docdir}/${PN}/
}
