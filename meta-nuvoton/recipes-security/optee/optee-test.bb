SUMMARY = "OP-TEE sanity testsuite"
HOMEPAGE = "https://github.com/OP-TEE/optee_test"

LICENSE = "BSD-2-Clause & GPLv2"
LIC_FILES_CHKSUM = "file://${S}/LICENSE.md;md5=daa2bcccc666345ab8940aab1315a4fa"

SRC_URI = "git://github.com/OP-TEE/optee_test.git;branch=master;protocol=https"
SRCREV = "1cf0e6d2bdd1145370033d4e182634458528579d"

PV = "3.16.0+git${SRCPV}"

S = "${WORKDIR}/git"

# Imports machine specific configs from staging to build
PACKAGE_ARCH = "${MACHINE_ARCH}"

DEPENDS = "optee-client optee-os python3-pycryptodome-native python3-pycryptodomex-native python3-cryptography-native"

inherit python3native

OPTEE_CLIENT_EXPORT = "${STAGING_DIR_HOST}${prefix}"
TEEC_EXPORT         = "${STAGING_DIR_HOST}${prefix}"
TA_DEV_KIT_DIR      = "${STAGING_INCDIR}/optee/export-user_ta"

EXTRA_OEMAKE = " TA_DEV_KIT_DIR=${TA_DEV_KIT_DIR} \
                 OPTEE_CLIENT_EXPORT=${OPTEE_CLIENT_EXPORT} \
                 TEEC_EXPORT=${TEEC_EXPORT} \
                 CROSS_COMPILE_HOST=${TARGET_PREFIX} \
                 CROSS_COMPILE_TA=${TARGET_PREFIX} \
                 V=1 \
                 CFG_TEE_CLIENT_LOAD_PATH=${libdir} \
                 LIBGCC_LOCATE_CFLAGS='--sysroot=${STAGING_DIR_HOST}' \
               "

do_compile() {
    # Top level makefile doesn't seem to handle parallel make gracefully
    oe_runmake xtest
    oe_runmake ta
}

do_install () {
    install -D -p -m0755 ${S}/out/xtest/xtest ${D}${bindir}/xtest

    # install path should match the value set in optee-client/tee-supplicant
    # default TEEC_LOAD_PATH is /lib
    mkdir -p ${D}${nonarch_base_libdir}/optee_armtz/
    install -D -p -m0444 ${S}/out/ta/*/*.ta ${D}${nonarch_base_libdir}/optee_armtz/
}

FILES:${PN} += "${nonarch_base_libdir} ${libdir}/tee-supplicant/plugins/"

DEBUG_OPTIMIZATION:append = " -Wno-error=maybe-uninitialized -Wno-deprecated-declarations"
FULL_OPTIMIZATION:append = " -Wno-error=maybe-uninitialized -Wno-deprecated-declarations"
