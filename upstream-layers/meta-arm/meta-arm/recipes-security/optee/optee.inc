UPSTREAM_CHECK_GITTAGREGEX = "^(?P<pver>\d+(\.\d+)+)$"

# Use tags for versioned recipes, but keep _git recipes on master.
OPTEE_SRC_URI_BRANCH_OR_TAG ?= "${@'branch=master' if d.getVar('PV') == 'git' or d.getVar('PV').endswith('+git') else 'tag=%s;nobranch=1' % d.getVar('PV')}"

COMPATIBLE_MACHINE ?= "invalid"
COMPATIBLE_MACHINE:genericarm64 ?= "genericarm64"
COMPATIBLE_MACHINE:qemuarm64 ?= "qemuarm64"
COMPATIBLE_MACHINE:qemuarm ?= "qemuarm"
# Please add supported machines below or set it in .bbappend or .conf

OPTEEMACHINE ?= "${MACHINE}"
OPTEEMACHINE:genericarm64 ?= "vexpress-qemu_armv8a"
OPTEEMACHINE:aarch64:qemuall ?= "vexpress-qemu_armv8a"
OPTEEMACHINE:arm:qemuall ?= "vexpress-qemu_virt"

OPTEE_ARCH = "null"
OPTEE_ARCH:arm = "arm32"
OPTEE_ARCH:aarch64 = "arm64"
OPTEE_CORE = "${@d.getVar('OPTEE_ARCH').upper()}"

# Set here but not passed to EXTRA_OEMAKE by default as that breaks
# the optee-os build
TA_DEV_KIT_DIR = "${STAGING_INCDIR}/optee/export-user_ta"

# Assume that our TOOLCHAIN matches OP-TEE's COMPILER
EXTRA_OEMAKE += "V=1 \
                 COMPILER=${TOOLCHAIN} \
                 OPTEE_CLIENT_EXPORT=${STAGING_DIR_HOST}${prefix} \
                 TEEC_EXPORT=${STAGING_DIR_HOST}${prefix} \
                "
# python3-cryptography needs the legacy provider, so set OPENSSL_MODULES to the
# right path until this is relocated automatically.
export OPENSSL_MODULES = "${STAGING_LIBDIR_NATIVE}/ossl-modules"

CFLAGS += "--sysroot=${STAGING_DIR_HOST}"

# See the rationale in https://github.com/f-secure-foundry/advisories/blob/master/Security_Advisory-Ref_FSC-HWSEC-VR2021-0001-OP-TEE_TrustZone_bypass.txt.
CVE_STATUS[CVE-2021-36133] = "disputed: devices shipped open for development purposes"
