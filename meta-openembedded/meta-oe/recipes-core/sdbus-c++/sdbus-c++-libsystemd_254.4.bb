SUMMARY = "libsystemd static library"
DESCRIPTION = "libsystemd static library built specifically as an integral component of sdbus-c++"

SECTION = "libs"

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://LICENSE.LGPL2.1;md5=4fbd65380cdd255951079008b364516c"

inherit meson pkgconfig

DEPENDS += "gperf-native gettext-native util-linux libcap util-linux python3-jinja2-native"

SRCREV = "2e7504449a51fb38db9cd2da391c6434f82def51"
SRCBRANCH = "v254-stable"
SRC_URI = "git://github.com/systemd/systemd-stable.git;protocol=https;branch=${SRCBRANCH} \
           file://static-libsystemd-pkgconfig.patch \
           "

# patches needed by musl
SRC_URI:append:libc-musl = " ${SRC_URI_MUSL}"

SRC_URI_MUSL = "\
               file://0009-missing_type.h-add-comparison_fn_t.patch \
               file://0010-add-fallback-parse_printf_format-implementation.patch \
               file://0011-src-basic-missing.h-check-for-missing-strndupa.patch \
               file://0012-don-t-fail-if-GLOB_BRACE-and-GLOB_ALTDIRFUNC-is-not-.patch \
               file://0013-add-missing-FTW_-macros-for-musl.patch \
               file://0014-Use-uintmax_t-for-handling-rlim_t.patch \
               file://0016-don-t-pass-AT_SYMLINK_NOFOLLOW-flag-to-faccessat.patch \
               file://0017-Define-glibc-compatible-basename-for-non-glibc-syste.patch \
               file://0018-Do-not-disable-buffering-when-writing-to-oom_score_a.patch \
               file://0019-distinguish-XSI-compliant-strerror_r-from-GNU-specif.patch \
               file://0020-avoid-redefinition-of-prctl_mm_map-structure.patch \
               file://0021-do-not-disable-buffer-in-writing-files.patch \
               file://0022-Handle-__cpu_mask-usage.patch \
               file://0023-Handle-missing-gshadow.patch \
               file://0024-missing_syscall.h-Define-MIPS-ABI-defines-for-musl.patch \
               file://0005-pass-correct-parameters-to-getdents64.patch \
               file://0001-Adjust-for-musl-headers.patch \
               file://0006-test-bus-error-strerror-is-assumed-to-be-GNU-specifi.patch \
               file://0003-errno-util-Make-STRERROR-portable-for-musl.patch \
               file://0028-sd-event-Make-malloc_trim-conditional-on-glibc.patch \
               file://0029-shared-Do-not-use-malloc_info-on-musl.patch \
               "

PACKAGECONFIG ??= "gshadow idn"
PACKAGECONFIG:remove:libc-musl = " gshadow idn"
PACKAGECONFIG[gshadow] = "-Dgshadow=true,-Dgshadow=false"
PACKAGECONFIG[idn] = "-Didn=true,-Didn=false"

CFLAGS:append:libc-musl = " -D__UAPI_DEF_ETHHDR=0 "

EXTRA_OEMESON += "-Dstatic-libsystemd=pic"

S = "${WORKDIR}/git"

RDEPENDS:${PN}-dev = ""

do_compile() {
    ninja -v ${PARALLEL_MAKE} version.h
    ninja -v ${PARALLEL_MAKE} libsystemd.a
    ninja -v ${PARALLEL_MAKE} src/libsystemd/libsystemd.pc
}

do_install () {
    install -d ${D}${libdir}
    install ${B}/libsystemd.a ${D}${libdir}

    install -d ${D}${includedir}/systemd
    install ${S}/src/systemd/*.h ${D}${includedir}/systemd

    install -d ${D}${libdir}/pkgconfig
    install ${B}/src/libsystemd/libsystemd.pc ${D}${libdir}/pkgconfig
}
