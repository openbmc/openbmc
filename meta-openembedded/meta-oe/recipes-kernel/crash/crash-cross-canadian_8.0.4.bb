inherit cross-canadian

SUMMARY = "crash utility (cross-canadian crash for ${TARGET_ARCH} target)"
PN = "crash-cross-canadian-${TRANSLATED_TARGET_ARCH}"
BPN = "crash"

require crash.inc


DEPENDS = "\
    nativesdk-ncurses \
    nativesdk-expat \
    nativesdk-gettext \
    nativesdk-gmp \
    nativesdk-mpfr \
    nativesdk-readline \
    nativesdk-zlib \
    virtual/${HOST_PREFIX}gcc \
    virtual/${HOST_PREFIX}binutils \
    virtual/nativesdk-${HOST_PREFIX}compilerlibs \
    virtual/nativesdk-libc"

RDEPENDS:${PN} = "nativesdk-liblzma"

EXTRA_OEMAKE:class-cross-canadian = 'RPMPKG="${PV}" \
                                     GDB_TARGET="${BUILD_SYS} --target=${TARGET_SYS}" \
                                     GDB_HOST="${HOST_SYS}" \
                                     GDB_MAKE_JOBS="${PARALLEL_MAKE}" \
                                     LDFLAGS="${LDFLAGS}" \
                                     '

# To ship crash into your sdk, you should create/update a packagegroup-cross-canadian.bbappend and
# add the following
# CRASH = "crash-cross-canadian-${TRANSLATED_TARGET_ARCH}"
# RDEPENDS:${PN} += "${@all_multilib_tune_values(d, 'CRASH')}"
#
# You should also add some kernel packages in your sdk, add the followng in your conf/local.conf:
#
# TOOLCHAIN_TARGET_TASK += "\
#     kernel-vmlinux \
#     kernel-dbg \
#     kernel-dev \
# "
#
# After sourcing the sdk environment script, you can analyze a kernel panic dump with
#
# crash $OECORE_TARGET_SYSROOT/boot/<vmlinux file> $OECORE_TARGET_SYSROOT/boot/<System.map file> <your vmcore>

do_install:class-cross-canadian () {
    install -m 0755 ${S}/crash ${D}/${bindir}
    cross_canadian_bindirlinks
}
