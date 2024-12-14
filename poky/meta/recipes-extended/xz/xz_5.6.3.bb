SUMMARY = "Utilities for managing LZMA compressed files"
HOMEPAGE = "https://tukaani.org/xz/"
DESCRIPTION = "XZ Utils is free general-purpose data compression software with a high compression ratio. XZ Utils were written for POSIX-like systems, but also work on some not-so-POSIX systems. XZ Utils are the successor to LZMA Utils."
SECTION = "base"

# The source includes bits of 0BSD, GPL-2.0, GPL-3.0, LGPL-2.1-or-later, but the
# only file which is GPL-3.0 is an m4 macro which isn't shipped in any of our
# packages, and the LGPL bits are under lib/, which appears to be used for
# libgnu, which appears to be used for DOS builds. So we're left with
# GPL-2.0-or-later and 0BSD.
LICENSE = "GPL-2.0-or-later & GPL-3.0-with-autoconf-exception & LGPL-2.1-or-later & 0BSD"
LICENSE:${PN} = "0BSD & GPL-2.0-or-later"
LICENSE:${PN}-dev = "0BSD & GPL-2.0-or-later"
LICENSE:${PN}-staticdev = "GPL-2.0-or-later"
LICENSE:${PN}-doc = "0BSD & GPL-2.0-or-later"
LICENSE:${PN}-dbg = "GPL-2.0-or-later"
LICENSE:${PN}-locale = "GPL-2.0-or-later"
LICENSE:liblzma = "0BSD"

LIC_FILES_CHKSUM = "file://COPYING;md5=c02de712b028a5cc7e22472e8f2b3db1 \
                    file://COPYING.0BSD;md5=0672c210ce80c83444339b9aa31fee2f \
                    file://COPYING.GPLv2;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING.GPLv3;md5=1ebbd3e34237af26da5dc08a4e440464 \
                    file://COPYING.LGPLv2.1;md5=4fbd65380cdd255951079008b364516c \
                    file://lib/getopt.c;endline=23;md5=3f33e207287bf72834f3ae8c247dfb6a \
                    "

SRC_URI = "https://github.com/tukaani-project/xz/releases/download/v${PV}/xz-${PV}.tar.gz \
           file://run-ptest \
          "
SRC_URI[sha256sum] = "b1d45295d3f71f25a4c9101bd7c8d16cb56348bbef3bbc738da0351e17c73317"
UPSTREAM_CHECK_REGEX = "releases/tag/v(?P<pver>\d+(\.\d+)+)"
UPSTREAM_CHECK_URI = "https://github.com/tukaani-project/xz/releases/"

CACHED_CONFIGUREVARS += "gl_cv_posix_shell=/bin/sh"

inherit autotools gettext ptest

PACKAGECONFIG[landlock] = "--enable-sandbox=landlock,--enable-sandbox=no"

PACKAGES =+ "liblzma"

FILES:liblzma = "${libdir}/liblzma*${SOLIBS}"

inherit update-alternatives
ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE:${PN} = "xz xzcat unxz \
                     lzma lzcat unlzma"

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN}-ptest += "bash file"

do_compile_ptest() {
        oe_runmake check TESTS=
}

do_install_ptest () {
    install -d ${D}${PTEST_PATH}/tests
    find ${B}/tests/.libs -type f -executable -exec cp {} ${D}${PTEST_PATH}/tests \;
    cp ${B}/config.h ${D}${PTEST_PATH}
    for i in files xzgrep_expected_output test_files.sh test_scripts.sh test_compress.sh; do
        cp -r ${S}/tests/$i ${D}${PTEST_PATH}/tests
    done
    mkdir -p ${D}${PTEST_PATH}/src/xz
    ln -s ${bindir}/xz ${D}${PTEST_PATH}/src/xz/xz
    mkdir -p ${D}${PTEST_PATH}/src/xzdec
    ln -s ${bindir}/xzdec ${D}${PTEST_PATH}/src/xzdec/xzdec
    mkdir -p ${D}${PTEST_PATH}/src/scripts
    ln -s ${bindir}/xzdiff ${D}${PTEST_PATH}/src/scripts/xzdiff
    ln -s ${bindir}/xzgrep ${D}${PTEST_PATH}/src/scripts/xzgrep
}
