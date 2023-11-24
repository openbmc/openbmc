require recipes-security/optee/optee-test.inc

SRC_URI += " \
    file://0001-xtest-regression_1000-remove-unneeded-stat.h-include.patch \
   "
SRCREV = "a286b57f1721af215ace318d5807e63f40186df6"

# Include ffa_spmc test group if the SPMC test is enabled.
# Supported after op-tee v3.20
EXTRA_OEMAKE:append = "${@bb.utils.contains('MACHINE_FEATURES', 'optee-spmc-test', \
                                        ' CFG_SPMC_TESTS=y CFG_SECURE_PARTITION=y', '' , d)}"

RDEPENDS:${PN} += "${@bb.utils.contains('MACHINE_FEATURES', 'optee-spmc-test', \
                                              ' arm-ffa-user', '' , d)}"
