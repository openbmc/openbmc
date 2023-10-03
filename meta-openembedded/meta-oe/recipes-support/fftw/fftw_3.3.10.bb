DESCRIPTION = "FFTW"
SECTION = "libs"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

SRC_URI = " \
    http://www.fftw.org/fftw-${PV}.tar.gz \
    file://0001-NEON-autodetection-segfaults-assume-neon-present.patch \
    file://install-bench.patch \
    file://run-ptest \
"
SRC_URI[sha256sum] = "56c932549852cddcfafdab3820b0200c7742675be92179e59e6215b340e26467"

inherit autotools pkgconfig ptest

# we had multiple recipes in the past
PROVIDES = "fftwl fftwf"

EXTRA_OECONF = "--disable-fortran --enable-shared --enable-threads"

CFLAGS += "-D_GNU_SOURCE"

# neon is optional for arm version < 8 -> check tune features
FFTW_NEON = "${@bb.utils.contains('TUNE_FEATURES', 'neon', '--enable-neon', '', d)}"
# neon is suppored for arm version = 8 -> enable
FFTW_NEON:aarch64 = "--enable-neon"
FFTW_NEON:class-native = ""

do_configure() {
    # configure fftw
    rm -rf ${WORKDIR}/build-fftw
	mkdir -p ${B}
    cd ${B}
    # full (re)configure
    autotools_do_configure
    mv ${B} ${WORKDIR}/build-fftw

    # configure fftwl
    rm -rf ${WORKDIR}/build-fftwl
	mkdir -p ${B}
    cd ${B}
    # configure only
    oe_runconf  --enable-long-double
    mv ${B} ${WORKDIR}/build-fftwl 

    # configure fftwf
    rm -rf ${WORKDIR}/build-fftwf
	mkdir -p ${B}
    cd ${B}
    # configure only
    oe_runconf --enable-single ${FFTW_NEON}
    mv ${B} ${WORKDIR}/build-fftwf
}

do_compile() {
    for lib in fftw fftwl fftwf; do
        cd ${WORKDIR}/build-$lib
        test -n "${TOOLCHAIN_OPTIONS}" && sed -i -e 's|${TOOLCHAIN_OPTIONS}||g' config.h
        autotools_do_compile
    done
}

do_install() {
    for lib in fftw fftwl fftwf; do
        cd ${WORKDIR}/build-$lib
        autotools_do_install
    done
}

do_install_ptest() {
    for lib in fftw fftwl fftwf; do
        install -d ${D}${PTEST_PATH}/$lib
        install -m 0755 ${S}/tests/check.pl ${D}${PTEST_PATH}/$lib
        cd ${WORKDIR}/build-$lib
        if [ $lib = "fftw" ]; then
            mv ${D}${bindir}/bench ${D}${PTEST_PATH}/$lib
        fi
        if [ $lib = "fftwl" ]; then
            mv ${D}${bindir}/benchl ${D}${PTEST_PATH}/$lib
        fi
        if [ $lib = "fftwf" ]; then
            mv ${D}${bindir}/benchf ${D}${PTEST_PATH}/$lib
        fi
    done
}

PACKAGES =+ "libfftw libfftwl libfftwf"
FILES:libfftw = "${libdir}/libfftw3.so.* ${libdir}/libfftw3_*.so.*"
FILES:libfftwl = "${libdir}/libfftw3l.so.* ${libdir}/libfftw3l_*.so.*"
FILES:libfftwf = "${libdir}/libfftw3f.so.* ${libdir}/libfftw3f_*.so.*"

PACKAGES =+ "fftw-wisdom fftwl-wisdom fftwf-wisdom fftw-wisdom-to-conf"
FILES:fftw-wisdom = "${bindir}/fftw-wisdom"
FILES:fftwl-wisdom = "${bindir}/fftwl-wisdom"
FILES:fftwf-wisdom = "${bindir}/fftwf-wisdom"
FILES:fftw-wisdom-to-conf = "${bindir}/fftw-wisdom-to-conf"

FILES:${PN}-dev += "${libdir}/cmake"
RDEPENDS:${PN}-dev = "libfftw libfftwl libfftwf"
RDEPENDS:${PN}-ptest += "perl"
RDEPENDS:${PN}-ptest:remove = "fftw"

BBCLASSEXTEND = "native"
