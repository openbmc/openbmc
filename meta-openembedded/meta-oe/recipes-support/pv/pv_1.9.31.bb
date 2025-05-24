SUMMARY = "Terminal-based tool for monitoring the progress of data through a pipeline"
HOMEPAGE = "http://www.ivarch.com/programs/pv.shtml"

LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://docs/COPYING;md5=1ebbd3e34237af26da5dc08a4e440464"

SRC_URI = "https://www.ivarch.com/programs/sources/${BP}.tar.gz \
           file://pv-test-system-version.patch \
           file://run-ptest \
"
SRC_URI[sha256sum] = "a35e92ec4ac0e8f380e8e840088167ae01014bfa008a3a9d6506b848079daedf"

UPSTREAM_CHECK_URI = "http://www.ivarch.com/programs/pv.shtml"
UPSTREAM_CHECK_REGEX = "pv-(?P<pver>\d+(\.\d+)+).tar"

inherit autotools gettext ptest

LDEMULATION:mipsarchn32 = "${@bb.utils.contains('TUNE_FEATURES', 'bigendian', 'elf32btsmipn32', 'elf32ltsmipn32', d)}"
export LDEMULATION

# for ptests
VALGRIND = "valgrind"

# valgrind supports armv7 and above
VALGRIND:armv4 = ''
VALGRIND:armv5 = ''
VALGRIND:armv6 = ''

# X32 isn't supported by valgrind at this time
VALGRIND:linux-gnux32 = ''
VALGRIND:linux-muslx32 = ''

# Disable for some MIPS variants
VALGRIND:mipsarchr6 = ''
VALGRIND:linux-gnun32 = ''

# Disable for powerpc64 with musl
VALGRIND:libc-musl:powerpc64 = ''
VALGRIND:libc-musl:powerpc64le = ''

# RISC-V support for valgrind is not there yet
VALGRIND:riscv64 = ""
VALGRIND:riscv32 = ""

RDEPENDS:${PN}-ptest += "bash coreutils tmux ${VALGRIND}"
RDEPENDS:${PN}-ptest:append:libc-musl = " musl-locales"
RDEPENDS:${PN}-ptest:append:libc-glibc = " glibc-binary-localedata-c"

do_install_ptest() {
    testsdir=${D}${PTEST_PATH}/tests
    install -d ${testsdir}
    cp -r ${S}/tests/* ${testsdir}
    #
    # remove self-hosted install test
    rm -f ${testsdir}/Bug_-_Install_all_files.test
    # skip the failing valgrind tests for now (March 2025).
    # See: https://bugzilla.yoctoproject.org/show_bug.cgi?id=15817
    rm -f ${testsdir}/Memory*
    # test requires at least 3GB free on /tmp
    rm -f ${testsdir}/Integrity_-_Large_file_support.test
    # fails due to our prompt: 
    rm -f ${testsdir}/Terminal_-_Detect_width.test
    #
    # sed -i -e 's@\$SRCDIR/@./@g' ${D}${PTEST_PATH}/run-ptest
}
