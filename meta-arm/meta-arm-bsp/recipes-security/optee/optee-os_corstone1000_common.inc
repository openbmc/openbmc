SRC_URI = "git://git.trustedfirmware.org/OP-TEE/optee_os.git;protocol=https;branch=psa-development"
SRCREV = "f9de2c9520ed97b89760cc4c99424aae440b63f4"
PV = "3.10.0+git${SRCPV}"

DEPENDS += "python3-pycryptodomex-native dtc-native"

FILESEXTRAPATHS:prepend := "${THISDIR}/files/optee-os/corstone1000:"

SRC_URI:append = " \
                  file://0001-plat-corstone1000-add-corstone1000-platform.patch \
                  file://0002-plat-corstone1000-reserve-3MB-CVM-memory-for-optee.patch \
                  file://0003-plat-corstone1000-add-a-rule-in-Makefile-to-SP_MAKEF.patch \
                  file://0004-plat-corstone1000-increase-OPTEE-core-heap-size.patch \
                  file://0005-Fix-add-missing-error-check-during-SP-init.patch \
                   "

COMPATIBLE_MACHINE = "corstone1000"

OPTEEMACHINE = "corstone1000"
# Enable optee memory layout and boot logs
EXTRA_OEMAKE += " CFG_TEE_CORE_LOG_LEVEL=4"

# default disable latency benchmarks (over all OP-TEE layers)
EXTRA_OEMAKE += " CFG_TEE_BENCHMARK=n"

EXTRA_OEMAKE += " CFG_CORE_SEL1_SPMC=y CFG_CORE_FFA=y"

EXTRA_OEMAKE += " CFG_WITH_SP=y"

EXTRA_OEMAKE += " HOST_PREFIX=${HOST_PREFIX}"
EXTRA_OEMAKE += " CROSS_COMPILE64=${HOST_PREFIX}"
