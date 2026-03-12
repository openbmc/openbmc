SUMMARY = "OPTEE fTPM Microsoft TA"
DESCRIPTION = "TCG reference implementation of the TPM 2.0 Specification."
HOMEPAGE = "https://github.com/microsoft/ms-tpm-20-ref/"

COMPATIBLE_MACHINE ?= "invalid"
COMPATIBLE_MACHINE:genericarm64 = "genericarm64"
COMPATIBLE_MACHINE:qemuarm64 = "qemuarm64"
COMPATIBLE_MACHINE:qemuarm64-secureboot = "qemuarm64"
COMPATIBLE_MACHINE:qemuarm-secureboot = "qemuarm"

inherit deploy python3native

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5a3925ece0806073ae9ebbb08ff6f11e"
LIC_FILES_CHKSUM += "file://optee-ta/LICENSE;md5=5a3925ece0806073ae9ebbb08ff6f11e"

DEPENDS = "python3-pyelftools-native optee-os-tadevkit python3-cryptography-native"
DEPENDS:append:toolchain-clang = " lld-native"

FTPM_UUID = "bc50d971-d4c9-42c4-82cb-343fb7f37896"

SRC_URI_ms-tpm   ?= "gitsm://github.com/Microsoft/ms-tpm-20-ref;protocol=https"
SRC_URI_optee-ta ?= "gitsm://github.com/OP-TEE/optee_ftpm.git;protocol=https"

SRCBRANCH_ms-tpm    = "main"
OPTEE_TA_GIT_TAG ?= "tag=${PV};nobranch=1"

SRC_URI = "\
    ${SRC_URI_ms-tpm};branch=${SRCBRANCH_ms-tpm};name=ms-tpm;destsuffix=ms-tpm \
    ${SRC_URI_optee-ta};${OPTEE_TA_GIT_TAG};name=optee-ta;destsuffix=ms-tpm/optee-ta \
"

# As per optee-ftpm TA documentation, we have to use this SHA of MS TPM reference
SRCREV_ms-tpm   ?= "98b60a44aba79b15fcce1c0d1e46cf5918400f6a"

# v4.9.0
SRCREV_optee-ta ?= "a09269b15de635e1816fe832e26adfbfb44c5455"

SRCREV_FORMAT    = "ms-tpm_optee-ta"

UPSTREAM_CHECK_COMMITS = "1"

S = "${UNPACKDIR}/ms-tpm"

OPTEE_CLIENT_EXPORT = "${STAGING_DIR_HOST}${prefix}"
TEEC_EXPORT = "${STAGING_DIR_HOST}${prefix}"
TA_DEV_KIT_DIR = "${STAGING_INCDIR}/optee/export-user_ta"

EXTRA_OEMAKE += '\
    COMPILER=${TOOLCHAIN} \
    TA_DEV_KIT_DIR=${TA_DEV_KIT_DIR} \
    CROSS_COMPILE=${TARGET_PREFIX} \
    CFG_MS_TPM_20_REF="${S}" \
    CFLAGS="${CFLAGS} --sysroot=${STAGING_DIR_HOST}" \
'

EXTRA_OEMAKE:append:aarch64:qemuall = "\
    CFG_ARM64_ta_arm64=y \
"

CFLAGS:append:toolchain-clang = " -Wno-unknown-warning-option"

# python3-cryptography needs the legacy provider, so set OPENSSL_MODULES to the
# right path until this is relocated automatically.
export OPENSSL_MODULES = "${STAGING_LIBDIR_NATIVE}/ossl-modules"

PARALLEL_MAKE = ""

do_compile() {
    cd ${S}/optee-ta
    oe_runmake
}

do_install () {
    mkdir -p ${D}/${nonarch_base_libdir}/optee_armtz
    install -D -p -m 0644 ${S}/optee-ta/${FTPM_UUID}.ta ${D}/${nonarch_base_libdir}/optee_armtz/
    install -D -p -m 0644 ${S}/optee-ta/${FTPM_UUID}.stripped.elf ${D}/${nonarch_base_libdir}/optee_armtz/
}

do_deploy () {
    install -d ${DEPLOYDIR}/optee
    install -D -p -m 0644 ${S}/optee-ta/${FTPM_UUID}.stripped.elf ${DEPLOYDIR}/optee/
}

addtask deploy before do_build after do_install

FILES:${PN} += " \
               ${nonarch_base_libdir}/optee_armtz/${FTPM_UUID}.ta \
               ${nonarch_base_libdir}/optee_armtz/${FTPM_UUID}.stripped.elf \
               "

# Imports machine specific configs from staging to build
PACKAGE_ARCH = "${MACHINE_ARCH}"
INSANE_SKIP:${PN} += "ldflags"
