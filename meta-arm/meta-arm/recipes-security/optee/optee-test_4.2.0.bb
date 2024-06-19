require recipes-security/optee/optee-test.inc

SRCREV = "526d5bac1b65f907f67c05cd07beca72fbab88dd"
SRC_URI += "file://0001-xtest-stats-remove-unneeded-stat.h-include.patch"

# Include ffa_spmc test group if the SPMC test is enabled.
# Supported after op-tee v3.20
EXTRA_OEMAKE:append = "${@bb.utils.contains('MACHINE_FEATURES', 'optee-spmc-test', \
                                        ' CFG_SPMC_TESTS=y CFG_SECURE_PARTITION=y', '' , d)}"

RDEPENDS:${PN} += "${@bb.utils.contains('MACHINE_FEATURES', 'optee-spmc-test', \
                                              ' arm-ffa-user', '' , d)}"
