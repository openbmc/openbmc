SRCREV = "42f6617108fa43712652ef52f9d5b4ec5b2665f8"
PV = "3.18.0+git${SRCPV}"

SRC_URI:remove = " \
                  file://0008-no-warn-rwx-segments.patch \
                 "

FILESEXTRAPATHS:prepend := "${THISDIR}/files/optee-os/corstone1000:"
SRC_URI:append = " \
                  file://0004-Handle-logging-syscall.patch \
		  "

COMPATIBLE_MACHINE = "corstone1000"

OPTEEMACHINE = "corstone1000"
# Enable optee memory layout and boot logs
EXTRA_OEMAKE += " CFG_TEE_CORE_LOG_LEVEL=4"

# default disable latency benchmarks (over all OP-TEE layers)
EXTRA_OEMAKE += " CFG_TEE_BENCHMARK=n"

EXTRA_OEMAKE += " CFG_CORE_SEL1_SPMC=y CFG_CORE_FFA=y"

EXTRA_OEMAKE += " CFG_WITH_SP=y"
