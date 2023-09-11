# TC0 specific configuration

# Total Compute (tc) specific configuration for optee-os and optee-os-tadevkit

FILESEXTRAPATHS:prepend := "${THISDIR}/files/optee-os/tc:"
SRC_URI:append:tc = " file://sp_layout.json \
    file://0001-WIP-Enable-managed-exit.patch \
    file://0002-plat-totalcompute-fix-TZDRAM-start-and-size.patch \
    "

COMPATIBLE_MACHINE = "(tc?)"

OPTEEMACHINE:tc1 = "totalcompute-tc1"

# Enable optee memory layout and boot logs
EXTRA_OEMAKE += " CFG_TEE_CORE_LOG_LEVEL=3"

# default disable latency benchmarks (over all OP-TEE layers)
EXTRA_OEMAKE += " CFG_TEE_BENCHMARK=n"

# Enable stats
EXTRA_OEMAKE += " CFG_WITH_STATS=y"

EXTRA_OEMAKE += " CFG_CORE_SEL2_SPMC=y"

# Copy optee manifest file
do_install:append() {
    install -d ${D}${nonarch_base_libdir}/firmware/
    install -m 644 ${WORKDIR}/sp_layout.json ${D}${nonarch_base_libdir}/firmware/
    install -m 644 \
        ${S}/core/arch/arm/plat-totalcompute/fdts/optee_sp_manifest.dts \
        ${D}${nonarch_base_libdir}/firmware/
}
