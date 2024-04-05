SUMMARY = "libsystemd static library"
DESCRIPTION = "libsystemd static library built specifically as an integral component of sdbus-c++"

SECTION = "libs"

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://LICENSE.LGPL2.1;md5=4fbd65380cdd255951079008b364516c"

inherit meson pkgconfig

DEPENDS += "gperf-native gettext-native util-linux libcap util-linux python3-jinja2-native"

SRCREV = "387a14a7b67b8b76adaed4175e14bb7e39b2f738"
SRCBRANCH = "v255-stable"
SRC_URI = "git://github.com/systemd/systemd-stable.git;protocol=https;branch=${SRCBRANCH} \
           file://static-libsystemd-pkgconfig.patch \
           "

# patches needed by musl
SRC_URI:append:libc-musl = " ${SRC_URI_MUSL}"

SRC_URI_MUSL = "\
    file://0001-missing_type.h-add-comparison_fn_t.patch \
    file://0002-add-fallback-parse_printf_format-implementation.patch \
    file://0002-binfmt-Don-t-install-dependency-links-at-install-tim.patch \
    file://0003-src-basic-missing.h-check-for-missing-strndupa.patch \
    file://0004-don-t-fail-if-GLOB_BRACE-and-GLOB_ALTDIRFUNC-is-not-.patch \
    file://0005-add-missing-FTW_-macros-for-musl.patch \
    file://0006-Use-uintmax_t-for-handling-rlim_t.patch \
    file://0007-don-t-pass-AT_SYMLINK_NOFOLLOW-flag-to-faccessat.patch \
    file://0008-Define-glibc-compatible-basename-for-non-glibc-syste.patch \
    file://0008-implment-systemd-sysv-install-for-OE.patch \
    file://0009-Do-not-disable-buffering-when-writing-to-oom_score_a.patch \
    file://0010-distinguish-XSI-compliant-strerror_r-from-GNU-specif.patch \
    file://0011-avoid-redefinition-of-prctl_mm_map-structure.patch \
    file://0012-do-not-disable-buffer-in-writing-files.patch \
    file://0013-Handle-__cpu_mask-usage.patch \
    file://0014-Handle-missing-gshadow.patch \
    file://0015-missing_syscall.h-Define-MIPS-ABI-defines-for-musl.patch \
    file://0016-pass-correct-parameters-to-getdents64.patch \
    file://0017-Adjust-for-musl-headers.patch \
    file://0018-test-bus-error-strerror-is-assumed-to-be-GNU-specifi.patch \
    file://0019-errno-util-Make-STRERROR-portable-for-musl.patch \
    file://0020-sd-event-Make-malloc_trim-conditional-on-glibc.patch \
    file://0021-shared-Do-not-use-malloc_info-on-musl.patch \
    file://0022-avoid-missing-LOCK_EX-declaration.patch \
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
