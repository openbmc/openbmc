# FVP specific TFA parameters

#
# Armv8-A Base Platform FVP
#

FILESEXTRAPATHS:prepend := "${THISDIR}/files/:${THISDIR}/files/fvp-base:"

SRC_URI:append = " \
    file://optee_spmc_maifest.dts;subdir=${BB_GIT_DEFAULT_DESTSUFFIX}/plat/arm/board/fvp/fdts \
"

# OP-TEE SPMC related configuration
SPMC_IS_OPTEE = "${@bb.utils.contains('MACHINE_FEATURES', 'arm-ffa', '0' \
                 if d.getVar('SEL2_SPMC') == '1' else '1', '0', d)}"
# Configure the SPMC manifest file.
TFA_ARM_SPMC_MANIFEST_DTS = "${@oe.utils.conditional('SPMC_IS_OPTEE', '1', \
                '${S}/plat/arm/board/fvp/fdts/optee_spmc_maifest.dts', '', d)}"
EXTRA_OEMAKE += "${@bb.utils.contains('MACHINE_FEATURES','arm-ffa', \
                'ARM_SPMC_MANIFEST_DTS=${TFA_ARM_SPMC_MANIFEST_DTS}' \
                    if d.getVar('TFA_ARM_SPMC_MANIFEST_DTS') else '', '', d)}"

# Set OP-TEE SPMC specific TF-A config settings
TFA_SPMD_SPM_AT_SEL2 := '0'
TFA_SPD := "${@oe.utils.conditional('SPMC_IS_OPTEE', '1', 'spmd', \
            d.getVar('TFA_SPD'), d)}"
DEPENDS += " ${@oe.utils.conditional('SPMC_IS_OPTEE', '1', 'optee-os', '', d)}"

# Configure measured boot if the attestation SP is deployed.
TFA_MB_FLAGS += " \
    ARM_ROTPK_LOCATION=devel_rsa \
    EVENT_LOG_LEVEL=20 \
    GENERATE_COT=1 \
    MBOOT_EL_HASH_ALG=sha256 \
    MEASURED_BOOT=1 \
    ROT_KEY=plat/arm/board/common/rotpk/arm_rotprivk_rsa.pem \
    TRUSTED_BOARD_BOOT=1 \
"
EXTRA_OEMAKE += "${@bb.utils.contains('MACHINE_FEATURES', 'ts-attestation',\
                 '${TFA_MB_FLAGS}','', d)}"

# Add OP-TEE as BL32.
BL32 = "${@oe.utils.conditional('SPMC_IS_OPTEE', '1',\
        '${RECIPE_SYSROOT}/${nonarch_base_libdir}/firmware/tee-pager_v2.bin',\
        '', d)}"
EXTRA_OEMAKE += "${@oe.utils.conditional('SPMC_IS_OPTEE', '1', \
                ' BL32=${BL32}', '', d)}"

# Enable memory safety in TF-A if machine supports it.
EXTRA_OEMAKE += "${@bb.utils.contains('MACHINE_FEATURES', 'arm-branch-protection', ' BRANCH_PROTECTION=1', '', d)}"

# Generic configuration
COMPATIBLE_MACHINE = "fvp-base"
TFA_PLATFORM = "fvp"
# Disable debug build if measured boot is enabled.
TFA_DEBUG := "${@bb.utils.contains('MACHINE_FEATURES', 'ts-attestation', '0',\
              d.getVar('TFA_DEBUG'), d)}"
# Add mbedtls if measured boot is enabled
TFA_MBEDTLS := "${@bb.utils.contains('MACHINE_FEATURES', 'ts-attestation',\
               '1', d.getVar('TFA_MBEDTLS'), d)}"
TFA_UBOOT ?= "1"
TFA_BUILD_TARGET = "bl1 bl2 bl31 dtbs fip"

EXTRA_OEMAKE += "FVP_DT_PREFIX=fvp-base-gicv3-psci-1t FVP_USE_GIC_DRIVER=FVP_GICV3"

# Set the ISA to fvp-base conf file and disable AArch32 system registers
EXTRA_OEMAKE += "ARM_ARCH_MAJOR=${ARM_ISA_MAJOR} ARM_ARCH_MINOR=${ARM_ISA_MINOR} CTX_INCLUDE_AARCH32_REGS=0"

# If GENERATE_COT is set, then tf-a will try to use local poetry install
# to run the python cot-dt2c command.  Disable the local poetry and use
# the provided cot-dt2c.
EXTRA_OEMAKE += "host-poetry=''"
DEPENDS += "cot-dt2c-native"
