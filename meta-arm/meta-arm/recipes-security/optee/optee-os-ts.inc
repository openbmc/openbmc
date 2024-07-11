# Include Trusted Services SPs accordingly to defined machine features

# Please notice that OPTEE will load SPs in the order listed in this file.
# If an SP requires another SP to be already loaded it must be listed lower.

# TS SPs UUIDs definitions
require recipes-security/trusted-services/ts-uuid.inc

TS_ENV ?= "opteesp"
TS_BIN = "${RECIPE_SYSROOT}/usr/${TS_ENV}/bin"
TS_BIN_SPM_TEST= "${RECIPE_SYSROOT}/usr/opteesp/bin"

SP_EXT = "${@oe.utils.conditional('TS_ENV','opteesp','.stripped.elf','.bin',d)}"

# ITS SP
DEPENDS:append  = "${@bb.utils.contains('MACHINE_FEATURES', 'ts-its', \
                                        ' ts-sp-its', '' , d)}"
SP_PATHS:append = "${@bb.utils.contains('MACHINE_FEATURES', 'ts-its', \
                                        ' ${TS_BIN}/${ITS_UUID}${SP_EXT}', '', d)}"

# Storage SP
DEPENDS:append  = "${@bb.utils.contains('MACHINE_FEATURES', 'ts-storage', \
                                        ' ts-sp-storage', '' , d)}"
SP_PATHS:append = "${@bb.utils.contains('MACHINE_FEATURES', 'ts-storage', \
                                        ' ${TS_BIN}/${STORAGE_UUID}${SP_EXT}', '', d)}"

# Crypto SP.
DEPENDS:append  = "${@bb.utils.contains('MACHINE_FEATURES', 'ts-crypto', \
                                        ' ts-sp-crypto', '' , d)}"
SP_PATHS:append = "${@bb.utils.contains('MACHINE_FEATURES', 'ts-crypto', \
                                        ' ${TS_BIN}/${CRYPTO_UUID}${SP_EXT}', '', d)}"

# Attestation SP
DEPENDS:append  = "${@bb.utils.contains('MACHINE_FEATURES', 'ts-attestation', \
                                        ' ts-sp-attestation', '' , d)}"
SP_PATHS:append = "${@bb.utils.contains('MACHINE_FEATURES', 'ts-attestation', \
                                        ' ${TS_BIN}/${ATTESTATION_UUID}${SP_EXT}', '', d)}"

# Env-test SP
DEPENDS:append  = "${@bb.utils.contains('MACHINE_FEATURES', 'ts-env-test', \
                                        ' ts-sp-env-test', '' , d)}"
SP_PATHS:append = "${@bb.utils.contains('MACHINE_FEATURES', 'ts-env-test', \
                                        ' ${TS_BIN}/${ENV_TEST_UUID}${SP_EXT}', '', d)}"

# SE-Proxy SP
DEPENDS:append  = "${@bb.utils.contains('MACHINE_FEATURES', 'ts-se-proxy', \
                                        ' ts-sp-se-proxy', '' , d)}"
SP_PATHS:append = "${@bb.utils.contains('MACHINE_FEATURES', 'ts-se-proxy', \
                                        ' ${TS_BIN}/${SE_PROXY_UUID}${SP_EXT}', '', d)}"

# SMM Gateway
DEPENDS:append  = "${@bb.utils.contains('MACHINE_FEATURES', 'ts-smm-gateway', \
                                        ' ts-sp-smm-gateway', '' , d)}"
SP_PATHS:append = "${@bb.utils.contains('MACHINE_FEATURES', 'ts-smm-gateway', \
                                        ' ${TS_BIN}/${SMM_GATEWAY_UUID}${SP_EXT}', '', d)}"

# SPM test SPs
DEPENDS:append  = "${@bb.utils.contains('MACHINE_FEATURES', 'optee-spmc-test', \
                                        ' ts-sp-spm-test1 ts-sp-spm-test2 \
                                          ts-sp-spm-test3 ts-sp-spm-test4', '' , d)}"
SP_PATHS:append = "${@bb.utils.contains('MACHINE_FEATURES', 'optee-spmc-test', \
                        ' ${TS_BIN_SPM_TEST}/${SPM_TEST1_UUID}.stripped.elf \
                          ${TS_BIN_SPM_TEST}/${SPM_TEST2_UUID}.stripped.elf \
                          ${TS_BIN_SPM_TEST}/${SPM_TEST3_UUID}.stripped.elf \
                          ${TS_BIN_SPM_TEST}/${SPM_TEST4_UUID}.stripped.elf', \
                          '', d)}"
EXTRA_OEMAKE:append = "${@bb.utils.contains('MACHINE_FEATURES', 'optee-spmc-test', \
                                        ' CFG_SPMC_TESTS=y', '' , d)}"

# Firmware Update SP
DEPENDS:append  = "${@bb.utils.contains('MACHINE_FEATURES', 'ts-fwu', \
                                        ' ts-sp-fwu', '' , d)}"
SP_PATHS:append = "${@bb.utils.contains('MACHINE_FEATURES', 'ts-fwu', \
                                        ' ${TS_BIN}/${FWU_UUID}${SP_EXT}', '', d)}"

# Block Storage SP
DEPENDS:append  = "${@bb.utils.contains('MACHINE_FEATURES', 'ts-block-storage', \
                                        ' ts-sp-block-storage', '' , d)}"

SP_PATHS:append = "${@bb.utils.contains('MACHINE_FEATURES', 'ts-block-storage', \
                                        ' ${TS_BIN}/${BLOCK_STORAGE_UUID}${SP_EXT}', '', d)}"

EXTRA_OEMAKE:append = "${@oe.utils.conditional('SP_PATHS', '', '', \
                        ' CFG_MAP_EXT_DT_SECURE=y CFG_SECURE_PARTITION=y \
                         SP_PATHS="${SP_PATHS}" ', d)}"
