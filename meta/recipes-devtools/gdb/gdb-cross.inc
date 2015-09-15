require gdb-common.inc

DEPENDS = "expat-native ncurses-native readline-native python-native"

inherit pythonnative

EXTRA_OECONF += "--with-python=${STAGING_BINDIR_NATIVE}/python-native/python"

do_compile_prepend() {
    export BUILD_SYS="${BUILD_SYS}"
    export HOST_SYS="${HOST_SYS}"
    export STAGING_LIBDIR="${STAGING_LIBDIR_NATIVE}"
    export STAGING_INCDIR="${STAGING_INCDIR_NATIVE}"
}

#EXTRA_OEMAKE += "LDFLAGS='${BUILD_LDFLAGS}'"

GDBPROPREFIX = ""

PN = "gdb-cross-${TARGET_ARCH}"
BPN = "gdb"

inherit cross
inherit gettext

datadir .= "/gdb-${TUNE_PKGARCH}${TARGET_VENDOR}-${TARGET_OS}"
