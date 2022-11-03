# Include Trusted Services SPs accordingly to defined machine features

# Please notice that OPTEE will load SPs in the order listed in this file.
# If an SP requires another SP to be already loaded it must be listed lower.

# TS SPs UUIDs definitions
require recipes-security/trusted-services/ts-uuid.inc

TS_ENV = "opteesp"
TS_BIN = "${RECIPE_SYSROOT}/usr/${TS_ENV}/bin"

# ITS SP
DEPENDS:append  = "${@bb.utils.contains('MACHINE_FEATURES', 'ts-its', \
                                        ' ts-sp-its', '' , d)}"
SP_PATHS:append = "${@bb.utils.contains('MACHINE_FEATURES', 'ts-its', \
                                        ' ${TS_BIN}/${ITS_UUID}.stripped.elf', '', d)}"

# Storage SP
DEPENDS:append  = "${@bb.utils.contains('MACHINE_FEATURES', 'ts-storage', \
                                        ' ts-sp-storage', '' , d)}"
SP_PATHS:append = "${@bb.utils.contains('MACHINE_FEATURES', 'ts-storage', \
                                        ' ${TS_BIN}/${STORAGE_UUID}.stripped.elf', '', d)}"

# Crypto SP.
DEPENDS:append  = "${@bb.utils.contains('MACHINE_FEATURES', 'ts-crypto', \
                                        ' ts-sp-crypto', '' , d)}"
SP_PATHS:append = "${@bb.utils.contains('MACHINE_FEATURES', 'ts-crypto', \
                                        ' ${TS_BIN}/${CRYPTO_UUID}.stripped.elf', '', d)}"

# Attestation SP
DEPENDS:append  = "${@bb.utils.contains('MACHINE_FEATURES', 'ts-attestation', \
                                        ' ts-sp-attestation', '' , d)}"
SP_PATHS:append = "${@bb.utils.contains('MACHINE_FEATURES', 'ts-attestation', \
                                        ' ${TS_BIN}/${ATTESTATION_UUID}.stripped.elf', '', d)}"

# Env-test SP
DEPENDS:append  = "${@bb.utils.contains('MACHINE_FEATURES', 'ts-env-test', \
                                        ' ts-sp-env-test', '' , d)}"
SP_PATHS:append = "${@bb.utils.contains('MACHINE_FEATURES', 'ts-env-test', \
                                        ' ${TS_BIN}/${ENV_TEST_UUID}.stripped.elf', '', d)}"

# SE-Proxy SP
DEPENDS:append  = "${@bb.utils.contains('MACHINE_FEATURES', 'ts-se-proxy', \
                                        ' ts-sp-se-proxy', '' , d)}"
SP_PATHS:append = "${@bb.utils.contains('MACHINE_FEATURES', 'ts-se-proxy', \
                                        ' ${TS_BIN}/${SE_PROXY_UUID}.stripped.elf', '', d)}"

# SMM Gateway
DEPENDS:append  = "${@bb.utils.contains('MACHINE_FEATURES', 'ts-smm-gateway', \
                                        ' ts-sp-smm-gateway', '' , d)}"
SP_PATHS:append = "${@bb.utils.contains('MACHINE_FEATURES', 'ts-smm-gateway', \
                                        ' ${TS_BIN}/${SMM_GATEWAY_UUID}.stripped.elf', '', d)}"

EXTRA_OEMAKE:append = "${@oe.utils.conditional('SP_PATHS', '', '', ' CFG_SECURE_PARTITION=y SP_PATHS="${SP_PATHS}" ', d)}"
