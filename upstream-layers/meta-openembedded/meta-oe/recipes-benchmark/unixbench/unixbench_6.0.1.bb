SUMMARY = "BYTE UNIX Benchmark Suite"
DESCRIPTION = "UnixBench is the original BYTE UNIX benchmark suite that provides a basic indicator of the performance of a Unix-like system. It includes tests for CPU, memory, I/O, and graphics performance."
HOMEPAGE = "https://github.com/kdlucas/byte-unixbench"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = " \
    git://github.com/kdlucas/byte-unixbench.git;protocol=https;branch=master;tag=v${PV} \
    file://0001-skip-make-checks.patch \
"
SRCREV = "e949d4402f76b4bc9b7b114418faace882f0ef12"

UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+(\.\d+)+)"

do_configure[noexec] = "1"

RDEPENDS:${PN} += " \
    perl \
    perl-modules \
"

INSANE_SKIP:${PN} = "ldflags"

do_compile() {
    oe_runmake -C ${S}/UnixBench \
    CC="${CC}" \
    AS="${AS}" \
    AR="${AR}" \
    LD="${CC}" \
    UB_GCC_OPTIONS="${CFLAGS}" \
    LDFLAGS="${LDFLAGS} -lm" \
    all
}

do_install() {
    # Install UnixBench under datadir to preserve directory structure
    install -d ${D}${datadir}/unixbench/pgms
    install -d ${D}${datadir}/unixbench/testdir
    install -d ${D}${datadir}/unixbench/results
    install -d ${D}${datadir}/unixbench/tmp
    
    # Install UnixBench binaries under pgms/ directory
    for binary in arithoh register short int long float double hanoi syscall context1 pipe spawn execl dhry2 dhry2reg looper fstime whetstone-double; do
        if [ -f ${S}/UnixBench/pgms/${binary} ]; then
            install -m 0755 ${S}/UnixBench/pgms/${binary} ${D}${datadir}/unixbench/pgms/${binary}
        fi
    done
    
    # Install Run script
    install -m 0755 ${S}/UnixBench/Run ${D}${datadir}/unixbench/Run
    
    # Install test scripts
    install -m 0755 ${S}/UnixBench/pgms/multi.sh ${D}${datadir}/unixbench/pgms/
    install -m 0755 ${S}/UnixBench/pgms/tst.sh ${D}${datadir}/unixbench/pgms/
    install -m 0644 ${S}/UnixBench/pgms/index.base ${D}${datadir}/unixbench/pgms/
    install -m 0644 ${S}/UnixBench/pgms/unixbench.logo ${D}${datadir}/unixbench/pgms/
    
    # Install test data files
    install -m 0644 ${S}/UnixBench/testdir/sort.src ${D}${datadir}/unixbench/testdir/
    install -m 0644 ${S}/UnixBench/testdir/cctest.c ${D}${datadir}/unixbench/testdir/
    install -m 0644 ${S}/UnixBench/testdir/dc.dat ${D}${datadir}/unixbench/testdir/
    install -m 0644 ${S}/UnixBench/testdir/large.txt ${D}${datadir}/unixbench/testdir/
}

FILES:${PN} += " \
    ${datadir}/unixbench \
"
