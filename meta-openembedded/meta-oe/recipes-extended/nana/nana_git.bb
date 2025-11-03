SUMMARY = "Support for assertion checking and logging in GNU C/C++"
DESCRIPTION = "GNU Nana is a free library providing improved support for assertion\
checking (as in assert.h) and logging (printf style debugging) in \
GNU C and C++."
SECTION = "Development/Languages/C and C++"

PV = "2.5+git"
SRCREV = "6d70617db8b9972e6c1008265fc228aba91c2042"
SRC_URI = "git://github.com/pjmaker/nana;protocol=https;branch=master \
    file://0001-Makefile.am-fix-build-with-separate-build-dir.patch \
    file://0002-man-Makefile.am-we-seem-not-to-need-the-work-around-.patch \
"
S = "${WORKDIR}/git"

LICENSE = "BSD-2-Clause"

LIC_FILES_CHKSUM = "file://COPYING;md5=16aa57f3b7fdda870cee597275bd5d11"

inherit autotools pkgconfig texinfo

EXTRA_OEMAKE = "DESTDIR=${D}"

do_configure:prepend() {
    # make distclean but in ${S}
    rm -rf ${S}/src/*.o ${S}/src/.deps \
        ${S}/Makefile ${S}/config.log ${S}/config.status \
        ${S}/doc/Makefile ${S}/doc/nana.pdf \
        ${S}/emacs/Makefile ${S}/examples/Makefile \
        ${S}/gdb/Makefile ${S}/gdb/nana-libtrace ${S}/gdb/nana-trace \
        ${S}/man/Makefile ${S}/perf/Makefile \
        ${S}/shortform/Makefile ${S}/shortform/nana-sfdir ${S}/shortform/nana-sfg \
        ${S}/src/Makefile ${S}/src/libnana.a \
        ${S}/src/nana ${S}/src/nana-c++lg ${S}/src/nana-clg ${S}/src/nana-config.h ${S}/src/nana-run ${S}/src/nanafilter \
        ${S}/test/Makefile ${S}/test/gammon
}

do_configure:prepend:class-nativesdk() {
    sed -i -e 's:@CPP@:\$\{CXX\} \$\{CXXFLAGS\} \-E:g' ${S}/src/nana.in
    sed -i -e 's:@CC@:\$\{CC\} \$\{CFLAGS\} \-E:g' ${S}/src/nana-clg.in
    sed -i -e 's:@CXX@::g' ${S}/src/nana-c++lg.in
    sed -i -e 's:@GDB@:\$\{GDB\}:g' ${S}/src/nana-run.in
}

BBCLASSEXTEND = "native nativesdk"

do_install:append() {
    sed -i -e 's,--sysroot=${STAGING_DIR_TARGET},,g' ${D}${bindir}/nana-c++lg
    sed -i -e 's,--sysroot=${STAGING_DIR_TARGET},,g' ${D}${bindir}/nana-clg
    sed -i -e 's,--sysroot=${STAGING_DIR_TARGET},,g' ${D}${bindir}/nana
}
