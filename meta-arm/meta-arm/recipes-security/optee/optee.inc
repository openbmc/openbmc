UPSTREAM_CHECK_GITTAGREGEX = "^(?P<pver>\d+(\.\d+)+)$"

COMPATIBLE_MACHINE ?= "invalid"
COMPATIBLE_MACHINE:qemuarm64 ?= "qemuarm64"
COMPATIBLE_MACHINE:qemuarm ?= "qemuarm"
# Please add supported machines below or set it in .bbappend or .conf

OPTEEMACHINE ?= "${MACHINE}"
OPTEEMACHINE:aarch64:qemuall ?= "vexpress-qemu_armv8a"
OPTEEMACHINE:arm:qemuall ?= "vexpress-qemu_virt"

OPTEE_ARCH = "null"
OPTEE_ARCH:arm = "arm32"
OPTEE_ARCH:aarch64 = "arm64"
OPTEE_CORE = "${@d.getVar('OPTEE_ARCH').upper()}"

OPTEE_TOOLCHAIN = "${@d.getVar('TOOLCHAIN') or 'gcc'}"
OPTEE_COMPILER = "${@bb.utils.contains("BBFILE_COLLECTIONS", "clang-layer", "${OPTEE_TOOLCHAIN}", "gcc", d)}"

# Set here but not passed to EXTRA_OEMAKE by default as that breaks
# the optee-os build
TA_DEV_KIT_DIR = "${STAGING_INCDIR}/optee/export-user_ta"

EXTRA_OEMAKE += "V=1 \
                 LIBGCC_LOCATE_CFLAGS='${HOST_CC_ARCH}${TOOLCHAIN_OPTIONS}' \
                 COMPILER=${OPTEE_COMPILER} \
                 OPTEE_CLIENT_EXPORT=${STAGING_DIR_HOST}${prefix} \
                 TEEC_EXPORT=${STAGING_DIR_HOST}${prefix} \
                "
# python3-cryptography needs the legacy provider, so set OPENSSL_MODULES to the
# right path until this is relocated automatically.
export OPENSSL_MODULES="${STAGING_LIBDIR_NATIVE}/ossl-modules"

CFLAGS += "--sysroot=${STAGING_DIR_HOST}"

# See the rationale in https://github.com/f-secure-foundry/advisories/blob/master/Security_Advisory-Ref_FSC-HWSEC-VR2021-0001-OP-TEE_TrustZone_bypass.txt.
CVE_STATUS[CVE-2021-36133] = "disputed: devices shipped open for development purposes"
