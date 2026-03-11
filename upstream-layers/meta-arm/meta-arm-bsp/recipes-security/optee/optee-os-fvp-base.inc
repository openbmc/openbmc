COMPATIBLE_MACHINE = "fvp-base"

OPTEEMACHINE = "vexpress-fvp"
# Enable boot logs
EXTRA_OEMAKE += " CFG_TEE_CORE_LOG_LEVEL=4"

# default disable latency benchmarks (over all OP-TEE layers)
EXTRA_OEMAKE += " CFG_TEE_BENCHMARK=n"

# If FF-A is enabled configure to be the SPMC.
EXTRA_OEMAKE += "${@bb.utils.contains('MACHINE_FEATURES', 'arm-ffa', ' CFG_CORE_HEAP_SIZE=131072 CFG_CORE_SEL1_SPMC=y CFG_DT=y', '' ,d)}"

EXTRA_OEMAKE += " CFG_ARM_GICV3=y"