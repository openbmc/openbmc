SUMMARY = "OP-TEE sanity testsuite"
DESCRIPTION = "Open Portable Trusted Execution Environment - Test suite"
HOMEPAGE = "https://www.op-tee.org/"

LICENSE = "BSD-2-Clause & GPL-2.0-only"

inherit python3native ptest
inherit deploy
require optee.inc

DEPENDS = "optee-client optee-os-tadevkit python3-cryptography-native openssl"
DEPENDS:append:toolchain-clang = " lld-native"

SRC_URI = "git://github.com/OP-TEE/optee_test.git;protocol=https;${OPTEE_SRC_URI_BRANCH_OR_TAG} \
           file://run-ptest \
          "

B = "${WORKDIR}/build"

EXTRA_OEMAKE += "TA_DEV_KIT_DIR=${TA_DEV_KIT_DIR} \
                 OPTEE_OPENSSL_EXPORT=${STAGING_INCDIR} \
                 CROSS_COMPILE_HOST=${HOST_PREFIX} \
                 CROSS_COMPILE_TA=${HOST_PREFIX} \
                 O=${B} \
               "

CFLAGS += "-Wno-error=deprecated-declarations"

do_compile() {
    cd ${S}
    # Top level makefile doesn't seem to handle parallel make gracefully
    oe_runmake xtest
    oe_runmake ta
    oe_runmake test_plugin
}
do_compile[cleandirs] = "${B}"

do_install () {
    install -D -p -m0755 ${B}/xtest/xtest ${D}${bindir}/xtest

    # install path should match the value set in optee-client/tee-supplicant
    # default TEEC_LOAD_PATH is /lib
    mkdir -p ${D}${nonarch_base_libdir}/optee_armtz/
    install -D -p -m0444 ${B}/ta/*/*.ta ${D}${nonarch_base_libdir}/optee_armtz/
    mkdir -p ${D}${libdir}/tee-supplicant/plugins
    install -D -p -m0444 ${B}/supp_plugin/*.plugin ${D}${libdir}/tee-supplicant/plugins/
}

do_deploy () {
    install -d ${DEPLOYDIR}/${MLPREFIX}optee/ta
    install -m 644 ${B}/ta/*/*.elf ${DEPLOYDIR}/${MLPREFIX}optee/ta
}

addtask deploy before do_build after do_install

FILES:${PN} += "${nonarch_base_libdir}/optee_armtz/ \
                ${libdir}/tee-supplicant/plugins/ \
               "

# Imports machine specific configs from staging to build
PACKAGE_ARCH = "${MACHINE_ARCH}"
