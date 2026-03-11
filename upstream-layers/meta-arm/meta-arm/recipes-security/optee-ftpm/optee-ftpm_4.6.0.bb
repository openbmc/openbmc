SUMMARY = "OPTEE fTPM Microsoft TA"
DESCRIPTION = "TCG reference implementation of the TPM 2.0 Specification."
HOMEPAGE = "https://github.com/microsoft/ms-tpm-20-ref/"

COMPATIBLE_MACHINE ?= "invalid"
COMPATIBLE_MACHINE:genericarm64 = "genericarm64"
COMPATIBLE_MACHINE:qemuarm64 = "qemuarm64"
COMPATIBLE_MACHINE:qemuarm64-secureboot = "qemuarm64"
COMPATIBLE_MACHINE:qemuarm-secureboot = "qemuarm"

#FIXME - doesn't currently work with clang
TOOLCHAIN = "gcc"

inherit deploy python3native

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5a3925ece0806073ae9ebbb08ff6f11e"
LIC_FILES_CHKSUM += "file://optee-ta/LICENSE;md5=5a3925ece0806073ae9ebbb08ff6f11e"

DEPENDS = "python3-pyelftools-native optee-os-tadevkit python3-cryptography-native "

FTPM_UUID = "bc50d971-d4c9-42c4-82cb-343fb7f37896"

SRC_URI_ms-tpm   ?= "gitsm://github.com/Microsoft/ms-tpm-20-ref;protocol=https"
SRC_URI_optee-ta ?= "gitsm://github.com/OP-TEE/optee_ftpm.git;protocol=https"

SRCBRANCH_ms-tpm    = "main"
SRCBRANCH_optee-ta  = "master"

SRC_URI = "\
    ${SRC_URI_ms-tpm};branch=${SRCBRANCH_ms-tpm};name=ms-tpm;destsuffix=ms-tpm \
    ${SRC_URI_optee-ta};branch=${SRCBRANCH_optee-ta};name=optee-ta;destsuffix=ms-tpm/optee-ta \
"

# As per optee-ftpm TA documentation, we have to use this SHA of MS TPM reference
SRCREV_ms-tpm   ?= "98b60a44aba79b15fcce1c0d1e46cf5918400f6a"

# v4.6.0 + fix for CVE-2025-46733
SRCREV_optee-ta ?= "ce33372ab772e879826361a1ca91126260bd9be1"

SRCREV_FORMAT    = "ms-tpm_optee-ta"

UPSTREAM_CHECK_COMMITS = "1"

S = "${UNPACKDIR}/ms-tpm"

OPTEE_CLIENT_EXPORT = "${STAGING_DIR_HOST}${prefix}"
TEEC_EXPORT = "${STAGING_DIR_HOST}${prefix}"
TA_DEV_KIT_DIR = "${STAGING_INCDIR}/optee/export-user_ta"

EXTRA_OEMAKE += '\
    TA_DEV_KIT_DIR=${TA_DEV_KIT_DIR} \
    CROSS_COMPILE=${TARGET_PREFIX} \
    CFG_MS_TPM_20_REF="${S}" \
    CFLAGS="${CFLAGS} --sysroot=${STAGING_DIR_HOST}" \
'

EXTRA_OEMAKE:append:aarch64:qemuall = "\
    CFG_ARM64_ta_arm64=y \
"

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
