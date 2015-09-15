SUMMARY = "Valgrind memory debugger and instrumentation framework"
HOMEPAGE = "http://valgrind.org/"
BUGTRACKER = "http://valgrind.org/support/bug_reports.html"
LICENSE = "GPLv2 & GPLv2+ & BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=c46082167a314d785d012a244748d803 \
                    file://include/pub_tool_basics.h;beginline=1;endline=29;md5=e7071929a50d4b0fc27a3014b315b0f7 \
                    file://include/valgrind.h;beginline=1;endline=56;md5=92df8a1bde56fe2af70931ff55f6622f \
                    file://COPYING.DOCS;md5=8fdeb5abdb235a08e76835f8f3260215"

X11DEPENDS = "virtual/libx11"
DEPENDS = "${@bb.utils.contains('DISTRO_FEATURES', 'x11', '${X11DEPENDS}', '', d)} \
           ${@bb.utils.contains('DISTRO_FEATURES', 'ptest', 'boost', '', d)} \
        "

SRC_URI = "http://www.valgrind.org/downloads/valgrind-${PV}.tar.bz2 \
           file://fixed-perl-path.patch \
           file://Added-support-for-PPC-instructions-mfatbu-mfatbl.patch \
           file://sepbuildfix.patch \
           file://glibc.patch \
           file://force-nostabs.patch \
           file://remove-arm-variant-specific.patch \
           file://remove-ppc-tests-failing-build.patch \
           file://valgrind-remove-rpath.patch \
           file://enable.building.on.4.x.kernel.patch \
           file://add-ptest.patch \
           file://pass-maltivec-only-if-it-supported.patch \
           file://run-ptest \
           file://0001-valgrind-Enable-rt_sigpending-syscall-on-ppc64-linux.patch \
          "

SRC_URI[md5sum] = "60ddae962bc79e7c95cfc4667245707f"
SRC_URI[sha256sum] = "fa253dc26ddb661b6269df58144eff607ea3f76a9bcfe574b0c7726e1dfcb997"

COMPATIBLE_HOST = '(i.86|x86_64|mips|powerpc|powerpc64).*-linux'
COMPATIBLE_HOST_armv7a = 'arm.*-linux'

PR = "r1"

inherit autotools ptest

EXTRA_OECONF = "--enable-tls --without-mpicc"
EXTRA_OECONF_armv7a = "--enable-tls -host=armv7-none-linux-gnueabi --without-mpicc"
EXTRA_OECONF += "${@['--enable-only32bit','--enable-only64bit'][d.getVar('SITEINFO_BITS', True) != '32']}"
EXTRA_OEMAKE = "-w"

do_install_append () {
    install -m 644 ${B}/default.supp ${D}/${libdir}/valgrind/
}

RDEPENDS_${PN} += "perl"

FILES_${PN}-dbg += "${libdir}/${PN}/*/.debug/*"

# valgrind needs debug information for ld.so at runtime in order to
# redirect functions like strlen.
RRECOMMENDS_${PN} += "${TCLIBC}-dbg"

RDEPENDS_${PN}-ptest += " sed perl glibc-utils perl-module-file-glob"

do_compile_ptest() {
    oe_runmake check CFLAGS="${CFLAGS} -O0" CXXFLAGS="${CXXFLAGS} -O0"
}


do_install_ptest() {
    chmod +x ${B}/tests/vg_regtest

    # The test application binaries are not automatically installed.
    # Grab them from the build directory.
    #
    # The regression tests require scripts and data files that are not
    # copied to the build directory.  They must be copied from the
    # source directory. 
    saved_dir=$PWD
    for parent_dir in ${S} ${B} ; do
        cd $parent_dir

        # exclude shell or the package won't install
        rm -rf none/tests/shell* 2>/dev/null

        subdirs="tests cachegrind/tests callgrind/tests drd/tests helgrind/tests massif/tests memcheck/tests none/tests"

        # Get the vg test scripts, filters, and expected files
        for dir in $subdirs ; do
            find $dir | cpio -pvdu ${D}${PTEST_PATH}
        done
        cd $saved_dir
    done

    # clean out build artifacts before building the rpm
    find ${D}${PTEST_PATH} \
         \( -name "Makefile*" \
        -o -name "*.o" \
        -o -name "*.c" \
        -o -name "*.S" \
        -o -name "*.h" \) \
        -exec rm {} \;

    # needed by massif tests
    cp ${B}/massif/ms_print ${D}${PTEST_PATH}/massif/ms_print

    # handle multilib
    sed -i s:@libdir@:${libdir}:g ${D}${PTEST_PATH}/run-ptest
}

