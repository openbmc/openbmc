FILESEXTRAPATHS:prepend := "${THISDIR}/files/optee-os/corstone1000:"
SRC_URI:append = " \
                  file://0001-Handle-logging-syscall.patch \
                  file://0002-increase-tzdram-size.patch \
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
