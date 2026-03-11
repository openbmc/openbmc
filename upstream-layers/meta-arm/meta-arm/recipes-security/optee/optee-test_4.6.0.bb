require recipes-security/optee/optee-test.inc

# v4.6.0
SRCREV = "a9e9495f4d57b97022008ad11198195e7e044c5d"

LIC_FILES_CHKSUM = "file://LICENSE.md;md5=a8fa504109e4cd7ea575bc49ea4be560"

SRC_URI += " \
    file://0001-build-make-cmake-add-Werror-based-on-CFG_WERROR.patch \
    file://0001-regression_1000-Re-order-the-include-of-sys-stat.h-h.patch \
"

# Include ffa_spmc test group if the SPMC test is enabled.
# Supported after op-tee v3.20
EXTRA_OEMAKE:append = "${@bb.utils.contains('MACHINE_FEATURES', 'optee-spmc-test', \
                                        ' CFG_SPMC_TESTS=y CFG_SECURE_PARTITION=y', '' , d)}"

RDEPENDS:${PN} += "${@bb.utils.contains('MACHINE_FEATURES', 'optee-spmc-test', \
                                              ' arm-ffa-user', '' , d)}"
