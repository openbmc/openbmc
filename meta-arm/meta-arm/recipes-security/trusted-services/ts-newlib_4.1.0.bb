SUMMARY = "Newlib static libraries built with Trusted Services opteesp deployment options"

TS_ENV = "opteesp"

require trusted-services.inc

SRC_URI += "git://sourceware.org/git/newlib-cygwin.git;name=newlib;protocol=https;branch=master;destsuffix=git/newlib \
"

# tag "newlib-4.1.0"
SRCREV_newlib = "415fdd4279b85eeec9d54775ce13c5c412451e08"
LIC_FILES_CHKSUM += "file://../newlib/COPYING.NEWLIB;md5=b8dda70da54e0efb49b1074f349d7749"

# Newlib does not compile with clang
TOOLCHAIN = "gcc"

EXTRA_OECMAKE += '-DNEWLIB_SOURCE_DIR=${WORKDIR}/git/newlib \
                  -DNEWLIB_CFLAGS="--sysroot=${STAGING_DIR_HOST}" \
                 '

OECMAKE_SOURCEPATH = "${S}/deployments/newlib/${TS_ENV}/"

# Silence compilation errors from GCC 14.1 due to stricter code validation
export NEWLIB_CFLAGS_TARGET = "-Wno-implicit-function-declaration -Wno-int-conversion"

# TS ships a patch that needs to be applied to newlib
apply_ts_patch() {
    set -ex
    cd ${WORKDIR}/git/newlib
    check_git_config
    git stash
    git branch -f bf_am
    git am ${S}/external/newlib/*.patch
    git reset bf_am
}
do_patch[postfuncs] += "apply_ts_patch"

FILES:${PN}-dev = "${TS_INSTALL}/newlib"
FILES:${PN}-staticdev = "${TS_INSTALL}/newlib/*/lib/*.a"
