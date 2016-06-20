require gdb-common.inc

DEPENDS = "expat-native ncurses-native"

inherit pythonnative

# Overrides PACKAGECONFIG variables in gdb-common.inc
PACKAGECONFIG ??= "python readline"
PACKAGECONFIG[python] = "--with-python=${STAGING_BINDIR_NATIVE}/python-native/python,--without-python,python-native"
PACKAGECONFIG[readline] = "--with-system-readline,--without-system-readline,readline-native"

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
