SUMMARY = "Newlib static libraries built with Trusted Services opteesp deployment options"

TS_ENV = "opteesp"

require trusted-services.inc

SRC_URI += "git://sourceware.org/git/newlib-cygwin.git;name=newlib;protocol=https;branch=master;destsuffix=git/newlib \
            file://0003-Add-newlib-deployment.patch \
            file://0021-newlib-configure.patch \
"

# tag "newlib-0.4.1"
SRCREV_newlib = "415fdd4279b85eeec9d54775ce13c5c412451e08"
LIC_FILES_CHKSUM += "file://../newlib/COPYING.NEWLIB;md5=b8dda70da54e0efb49b1074f349d7749"

EXTRA_OECMAKE += '-DNEWLIB_SOURCE_DIR=${WORKDIR}/git/newlib \
                  -DNEWLIB_EXTRA="CFLAGS=--sysroot=${STAGING_DIR_HOST}" \
                 '

OECMAKE_SOURCEPATH = "${S}/deployments/newlib/${TS_ENV}/"

# TS ships a patch that needs to be applied to newlib
apply_ts_patch() {
    for p in ${S}/external/newlib/*.patch; do
        patch -p1 -d ${WORKDIR}/git/newlib < ${p}
    done
}
do_patch[postfuncs] += "apply_ts_patch"

FILES:${PN}-dev = "${TS_INSTALL}/newlib_install"
FILES:${PN}-staticdev = "${TS_INSTALL}/newlib_install/*/lib/*.a"
