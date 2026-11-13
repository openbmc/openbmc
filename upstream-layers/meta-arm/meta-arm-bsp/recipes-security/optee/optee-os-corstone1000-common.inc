COMPATIBLE_MACHINE = "corstone1000"

OPTEEMACHINE = "corstone1000"
# Enable optee memory layout and boot logs
EXTRA_OEMAKE += " CFG_TEE_CORE_LOG_LEVEL=4"

# default disable latency benchmarks (over all OP-TEE layers)
EXTRA_OEMAKE += " CFG_TEE_BENCHMARK=n"

EXTRA_OEMAKE += " CFG_CORE_SEL1_SPMC=y CFG_CORE_FFA=y"

EXTRA_OEMAKE += " CFG_WITH_SP=y"

EXTRA_OEMAKE:append = "${@bb.utils.contains('MACHINE_FEATURES', 'corstone1000_smp', ' CFG_TEE_CORE_NB_CORE=4', '', d)}"

# Override OP-TEE OS ARM64 core architecture based on MACHINE_FEATURES
CPUARCH = "cortex-a35"
CPUARCH:cortexa320 = "cortex-a320"
EXTRA_OEMAKE:append:corstone1000 = " arm64-platform-cpuarch=${CPUARCH}"
