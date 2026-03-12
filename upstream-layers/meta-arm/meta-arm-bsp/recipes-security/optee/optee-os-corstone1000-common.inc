FILESEXTRAPATHS:prepend := "${THISDIR}/files/optee-os/corstone1000:"

SRC_URI:append = " \
        file://0001-plat-corstone1000-Specify-GIC-version-in-plat-specif.patch \
        file://0002-gic-Refactor-implementation-of-GICv3-to-add-GICv4-su.patch \
        file://0003-plat-corstone1000-Swap-GIC-600-for-GIC-700-for-Corte.patch \
"

COMPATIBLE_MACHINE = "corstone1000"

OPTEEMACHINE = "corstone1000"
# Enable optee memory layout and boot logs
EXTRA_OEMAKE += " CFG_TEE_CORE_LOG_LEVEL=4"

# default disable latency benchmarks (over all OP-TEE layers)
EXTRA_OEMAKE += " CFG_TEE_BENCHMARK=n"

EXTRA_OEMAKE += " CFG_CORE_SEL1_SPMC=y CFG_CORE_FFA=y"

EXTRA_OEMAKE += " CFG_WITH_SP=y"

EXTRA_OEMAKE:append:corstone1000-fvp = "${@bb.utils.contains('MACHINE_FEATURES', 'corstone1000_fvp_smp', ' CFG_TEE_CORE_NB_CORE=4', '', d)}"

# Override OP-TEE OS ARM64 core architecture based on MACHINE_FEATURES
CPUARCH = "cortex-a35"
CPUARCH:cortexa320 = "cortex-a320"
EXTRA_OEMAKE:append:corstone1000 = " arm64-platform-cpuarch=${CPUARCH}"
