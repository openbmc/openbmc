require recipes-security/optee/optee-test.inc

# v4.9.0
SRCREV = "b27648ea8472cceceb8dda368a965c709066f7aa"

LIC_FILES_CHKSUM = "file://LICENSE.md;md5=a8fa504109e4cd7ea575bc49ea4be560 \
                    file://LICENSE-BSD;md5=dca16d6efa93b55d0fd662ae5cd6feeb \
                    file://LICENSE-GPL;md5=10e86b5d2a6cb0e2b9dcfdd26a9ac58d \
                   "

# Include ffa_spmc test group if the SPMC test is enabled.
# Supported after op-tee v3.20
EXTRA_OEMAKE:append = "${@bb.utils.contains('MACHINE_FEATURES', 'optee-spmc-test', \
                                        ' CFG_SPMC_TESTS=y CFG_SECURE_PARTITION=y', '' , d)}"

RDEPENDS:${PN} += "${@bb.utils.contains('MACHINE_FEATURES', 'optee-spmc-test', \
                                              ' arm-ffa-user', '' , d)}"
