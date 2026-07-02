require crash.inc

BBCLASSEXTEND = "native cross"

EXTRA_OEMAKE:class-cross = 'RPMPKG="${PV}" \
                            GDB_TARGET="${BUILD_SYS} --target=${TARGET_SYS}" \
                            GDB_HOST="${BUILD_SYS}" \
                            GDB_MAKE_JOBS="${PARALLEL_MAKE}" \
                            '

EXTRA_OEMAKE:append:class-native = " LDFLAGS='${BUILD_LDFLAGS}'"
EXTRA_OEMAKE:append:class-cross = " LDFLAGS='${BUILD_LDFLAGS}'"

# gdb 16.2 (bundled, built for the build host in the cross case) needs gmp and
# mpfr, and crash links readline/ncurses. Unlike native.bbclass, cross.bbclass
# does not remap DEPENDS to their -native variants, so list them explicitly.
DEPENDS:append:class-cross = " zlib-native readline-native ncurses-native gmp-native mpfr-native"

do_install:class-target () {
    oe_runmake DESTDIR=${D} install
}

do_install:class-native () {
    oe_runmake DESTDIR=${D}${STAGING_DIR_NATIVE} install
}

do_install:class-cross () {
    install -m 0755 ${S}/crash ${D}/${bindir}
}

RDEPENDS:${PN}:class-native = ""
RDEPENDS:${PN}:class-cross = ""
