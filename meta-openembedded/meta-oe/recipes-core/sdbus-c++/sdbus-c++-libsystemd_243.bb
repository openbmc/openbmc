SUMMARY = "libsystemd static library"
DESCRIPTION = "libsystemd static library built specifically as an integral component of sdbus-c++"

SECTION = "libs"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://LICENSE.LGPL2.1;md5=4fbd65380cdd255951079008b364516c"

inherit meson pkgconfig

DEPENDS += "gperf-native gettext-native util-linux libcap"

SRCREV = "efb536d0cbe2e58f80e501d19999928c75e08f6a"
SRCBRANCH = "v243-stable"
SRC_URI = "git://github.com/systemd/systemd-stable.git;protocol=git;branch=${SRCBRANCH}"

SRC_URI += "file://static-libsystemd-pkgconfig.patch"

# patches needed by musl
SRC_URI_append_libc-musl = " ${SRC_URI_MUSL}"
SRC_URI_MUSL = "\
               file://0002-don-t-use-glibc-specific-qsort_r.patch \
               file://0003-missing_type.h-add-__compare_fn_t-and-comparison_fn_.patch \
               file://0004-add-fallback-parse_printf_format-implementation.patch \
               file://0005-src-basic-missing.h-check-for-missing-strndupa.patch \
               file://0006-Include-netinet-if_ether.h.patch \
               file://0007-don-t-fail-if-GLOB_BRACE-and-GLOB_ALTDIRFUNC-is-not.patch \
               file://0008-add-missing-FTW_-macros-for-musl.patch \
               file://0010-fix-missing-of-__register_atfork-for-non-glibc-build.patch \
               file://0011-Use-uintmax_t-for-handling-rlim_t.patch \
               file://0014-test-sizeof.c-Disable-tests-for-missing-typedefs-in-.patch \
               file://0015-don-t-pass-AT_SYMLINK_NOFOLLOW-flag-to-faccessat.patch \
               file://0016-Define-glibc-compatible-basename-for-non-glibc-syste.patch \
               file://0017-Do-not-disable-buffering-when-writing-to-oom_score_a.patch \
               file://0018-distinguish-XSI-compliant-strerror_r-from-GNU-specif.patch \
               file://0019-Hide-__start_BUS_ERROR_MAP-and-__stop_BUS_ERROR_MAP.patch \
               file://0020-missing_type.h-add-__compar_d_fn_t-definition.patch \
               file://0021-avoid-redefinition-of-prctl_mm_map-structure.patch \
               file://0024-test-json.c-define-M_PIl.patch \
               file://0001-do-not-disable-buffer-in-writing-files.patch \
               file://0002-src-login-brightness.c-include-sys-wait.h.patch \
               file://0003-src-basic-copy.c-include-signal.h.patch \
               file://0004-src-shared-cpu-set-util.h-add-__cpu_mask-definition.patch \
               "

PACKAGECONFIG ??= "gshadow idn"
PACKAGECONFIG_remove_libc-musl = " gshadow idn"
PACKAGECONFIG[gshadow] = "-Dgshadow=true,-Dgshadow=false"
PACKAGECONFIG[idn] = "-Didn=true,-Didn=false"

EXTRA_OEMESON += "-Dstatic-libsystemd=pic"

S = "${WORKDIR}/git"

do_compile() {
    ninja -v ${PARALLEL_MAKE} version.h
    ninja -v ${PARALLEL_MAKE} libsystemd.a
}

do_install () {
    install -d ${D}${libdir}
    install ${B}/libsystemd.a ${D}${libdir}

    install -d ${D}${includedir}/systemd
    install ${S}/src/systemd/*.h ${D}${includedir}/systemd

    install -d ${D}${libdir}/pkgconfig
    install ${B}/src/libsystemd/libsystemd.pc ${D}${libdir}/pkgconfig
}
