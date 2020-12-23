SUMMARY = "An abstraction layer for touchscreen panel events"
DESCRIPTION = "Tslib is an abstraction layer for touchscreen panel \
events, as well as a filter stack for the manipulation of those events. \
Tslib is generally used on embedded devices to provide a common user \
space interface to touchscreen functionality."
HOMEPAGE = "http://tslib.org/"

AUTHOR = "Martin Kepplinger <martink@posteo.de>"
SECTION = "base"
LICENSE = "LGPLv2+ & GPLv2+"
LIC_FILES_CHKSUM = "\
    file://COPYING;md5=fc178bcd425090939a8b634d1d6a9594 \
    file://tests/COPYING;md5=a23a74b3f4caf9616230789d94217acb \
"

SRC_URI = "https://github.com/kergoth/tslib/releases/download/${PV}/tslib-${PV}.tar.xz;downloadfilename=tslib-${PV}.tar.xz \
           file://ts.conf \
           file://tslib.sh \
           "
SRC_URI[sha256sum] = "aaf0aed410a268d7b51385d07fe4d9d64312038e87c447ec8a24c8db0a15617a"

UPSTREAM_CHECK_URI = "https://github.com/kergoth/tslib/releases"

inherit autotools pkgconfig

PACKAGECONFIG ??= "debounce dejitter iir linear median pthres skip lowpass invert variance input touchkit waveshare"
PACKAGECONFIG[debounce] = "--enable-debounce,--disable-debounce"
PACKAGECONFIG[dejitter] = "--enable-dejitter,--disable-dejitter"
PACKAGECONFIG[iir] = "--enable-iir,--disable-iir"
PACKAGECONFIG[linear] = "--enable-linear,--disable-linear"
PACKAGECONFIG[median] = "--enable-median,--disable-median"
PACKAGECONFIG[pthres] = "--enable-pthres,--disable-pthres"
PACKAGECONFIG[skip] = "--enable-skip,--disable-skip"
PACKAGECONFIG[lowpass] = "--enable-lowpass,--disable-lowpass"
PACKAGECONFIG[invert] = "--enable-invert,--disable-invert"
PACKAGECONFIG[variance] = "--enable-variance,--disable-variance"
PACKAGECONFIG[input] = "--enable-input,--disable-input"
PACKAGECONFIG[tatung] = "--enable-tatung,--disable-tatung"
PACKAGECONFIG[touchkit] = "--enable-touchkit,--disable-touchkit"
PACKAGECONFIG[waveshare] = "--enable-waveshare,--disable-waveshare"
PACKAGECONFIG[ucb1x00] = "--enable-ucb1x00,--disable-ucb1x00"
PACKAGECONFIG[mk712] = "--enable-mk712,--disable-mk712"
PACKAGECONFIG[h3600] = "--enable-h3600,--disable-h3600"
PACKAGECONFIG[dmc] = "--enable-dmc,--disable-dmc"
PACKAGECONFIG[linear-h2200] = "--enable-linear-h2200,--disable-linear-h2200"
PACKAGECONFIG[corgi] = "--enable-corgi,--disable-corgi"
PACKAGECONFIG[collie] = "--enable-collie,--disable-collie"
PACKAGECONFIG[arctic2] = "--enable-arctic2,--disable-arctic2"
PACKAGECONFIG[dmc_dus3000] = "--enable-dmc_dus3000,--disable-dmc_dus3000"
PACKAGECONFIG[cy8mrln-palmpre] = "--enable-cy8mrln-palmpre,--disable-cy8mrln-palmpre"
PACKAGECONFIG[galax] = "--enable-galax,--disable-galax"
PACKAGECONFIG[debug] = "--enable-debug,--disable-debug"

do_install_prepend() {
    install -m 0644 ${WORKDIR}/ts.conf ${S}/etc/ts.conf
}

do_install_append() {
    install -d ${D}${sysconfdir}/profile.d/
    install -m 0755 ${WORKDIR}/tslib.sh ${D}${sysconfdir}/profile.d/
}

RPROVIDES_tslib-conf = "libts-0.0-conf"

PACKAGES =+ "tslib-conf tslib-tests tslib-calibrate tslib-uinput"
DEBIAN_NOAUTONAME_tslib-conf = "1"
DEBIAN_NOAUTONAME_tslib-tests = "1"
DEBIAN_NOAUTONAME_tslib-calibrate = "1"
DEBIAN_NOAUTONAME_tslib-uinput = "1"

RDEPENDS_${PN} = "tslib-conf"
RRECOMMENDS_${PN} = "pointercal"

FILES_${PN}-dev += "${libdir}/ts/*.la"
FILES_tslib-conf = "${sysconfdir}/ts.conf ${sysconfdir}/profile.d/tslib.sh ${datadir}/tslib"
FILES_${PN} = "${libdir}/*.so.* ${libdir}/ts/*.so*"
FILES_tslib-calibrate += "${bindir}/ts_calibrate"
FILES_tslib-uinput += "${bindir}/ts_uinput"

FILES_tslib-tests = "${bindir}/ts_harvest ${bindir}/ts_print ${bindir}/ts_print_raw ${bindir}/ts_print_mt \
                     ${bindir}/ts_test ${bindir}/ts_test_mt ${bindir}/ts_verify ${bindir}/ts_finddev ${bindir}/ts_conf"
