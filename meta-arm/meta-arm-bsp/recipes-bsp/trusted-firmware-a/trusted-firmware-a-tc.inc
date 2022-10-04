# TC0 specific TFA configuration

# Intermediate SHA with 2.7 baseline version
SRCREV_tfa = "e95abc4c01822ef43e9e874d63d6596dc0b57279"
PV = "2.7+git${SRCPV}"

DEPENDS += "scp-firmware util-linux-native gptfdisk-native"

FILESEXTRAPATHS:prepend := "${THISDIR}/files/tc:"
SRC_URI:append = " \
    file://0001-plat-tc-Increase-maximum-BL2-size.patch \
    file://0002-Makefile-add-trusty_sp_fw_config-build-option.patch \
    file://0003-fix-plat-arm-increase-sp-max-image-size.patch \
    file://0004-fix-plat-tc-increase-tc_tzc_dram1_size.patch \
    file://0005-feat-plat-tc-add-spmc-manifest-with-trusty-sp.patch \
    file://0006-feat-plat-tc-update-dts-with-trusty-compatible-strin.patch \
    file://0007-feat-arm-tc-Update-trusty-load-address-in-dts-files.patch \
    file://0008-feat-plat-tc-add-firmware-update-secure-partition.patch \
    file://0009-feat-plat-tc-reserve-4-MB-for-stmm-communication-use.patch \
    file://generate_metadata.py \
    "

COMPATIBLE_MACHINE = "(tc?)"

TFA_PLATFORM = "tc"
TFA_BUILD_TARGET = "all fip"
TFA_UBOOT = "1"
TFA_INSTALL_TARGET = "bl1 fip"
TFA_MBEDTLS = "1"
TFA_DEBUG = "1"

TFA_SPD = "spmd"
TFA_SPMD_SPM_AT_SEL2 = "1"

TFA_TARGET_PLATFORM:tc0 = "0"
TFA_TARGET_PLATFORM:tc1 = "1"

EXTRA_OEMAKE += "TARGET_PLATFORM=${TFA_TARGET_PLATFORM}"

# Set optee as SP. Set spmc manifest and sp layout file to optee
DEPENDS += "optee-os"

TFA_SP_LAYOUT_FILE = "${RECIPE_SYSROOT}/lib/firmware/sp_layout.json"
TFA_ARM_SPMC_MANIFEST_DTS = "plat/arm/board/tc/fdts/tc_spmc_optee_sp_manifest.dts"

EXTRA_OEMAKE += "SCP_BL2=${RECIPE_SYSROOT}/firmware/scp_ramfw.bin"
EXTRA_OEMAKE += "TRUSTED_BOARD_BOOT=1 GENERATE_COT=1 ARM_ROTPK_LOCATION=devel_rsa \
                     ROT_KEY=plat/arm/board/common/rotpk/arm_rotprivk_rsa.pem"
EXTRA_OEMAKE += "PSA_FWU_SUPPORT=1 ARM_GPT_SUPPORT=1"
EXTRA_OEMAKE += "CTX_INCLUDE_MTE_REGS=1"

do_generate_gpt() {
    gpt_image="${BUILD_DIR}/fip_gpt.bin"
    fip_bin="${BUILD_DIR}/fip.bin"
    # the FIP partition type is not standardized, so generate one
    fip_type_uuid=`uuidgen --sha1 --namespace @dns --name "fip_type_uuid"`
    # metadata partition type UUID, specified by the document:
    # Platform Security Firmware Update for the A-profile Arm Architecture
    # version: 1.0BET0
    metadata_type_uuid="8a7a84a0-8387-40f6-ab41-a8b9a5a60d23"
    location_uuid=`uuidgen`
    FIP_A_uuid=`uuidgen`
    FIP_B_uuid=`uuidgen`

    # maximum FIP size 4MB. This is the current size of the FIP rounded up to an integer number of MB.
    fip_max_size=4194304
    fip_bin_size=$(stat -c %s $fip_bin)
    if [ $fip_max_size -lt $fip_bin_size ]; then
        bberror "FIP binary ($fip_bin_size bytes) is larger than the GPT partition ($fip_max_size bytes)"
    fi

    # maximum metadata size 512B. This is the current size of the metadata rounded up to an integer number of sectors.
    metadata_max_size=512
    metadata_file="${BUILD_DIR}/metadata.bin"
    python3 ${WORKDIR}/generate_metadata.py --metadata_file $metadata_file \
                                            --img_type_uuids $fip_type_uuid \
                                            --location_uuids $location_uuid \
                                            --img_uuids $FIP_A_uuid $FIP_B_uuid

    # create GPT image. The GPT contains 2 FIP partitions: FIP_A and FIP_B, and 2 metadata partitions: FWU-Metadata and Bkup-FWU-Metadata.
    # the GPT layout is the following:
    # -----------------------
    # Protective MBR
    # -----------------------
    # Primary GPT Header
    # -----------------------
    # FIP_A
    # -----------------------
    # FIP_B
    # -----------------------
    # FWU-Metadata
    # -----------------------
    # Bkup-FWU-Metadata
    # -----------------------
    # Secondary GPT Header
    # -----------------------

    sector_size=512
    gpt_header_size=33 # valid only for 512-byte sectors
    num_sectors_fip=`expr $fip_max_size / $sector_size`
    num_sectors_metadata=`expr $metadata_max_size / $sector_size`
    start_sector_1=`expr 1 + $gpt_header_size` # size of MBR is 1 sector
    start_sector_2=`expr $start_sector_1 + $num_sectors_fip`
    start_sector_3=`expr $start_sector_2 + $num_sectors_fip`
    start_sector_4=`expr $start_sector_3 + $num_sectors_metadata`
    num_sectors_gpt=`expr $start_sector_4 + $num_sectors_metadata + $gpt_header_size`
    gpt_size=`expr $num_sectors_gpt \* $sector_size`

    # create raw image
    dd if=/dev/zero of=$gpt_image bs=$gpt_size count=1

    # create the GPT layout
    sgdisk $gpt_image \
           --set-alignment 1 \
           --disk-guid $location_uuid \
           \
           --new 1:$start_sector_1:+$num_sectors_fip \
           --change-name 1:FIP_A \
           --typecode 1:$fip_type_uuid \
           --partition-guid 1:$FIP_A_uuid \
           \
           --new 2:$start_sector_2:+$num_sectors_fip \
           --change-name 2:FIP_B \
           --typecode 2:$fip_type_uuid \
           --partition-guid 2:$FIP_B_uuid \
           \
           --new 3:$start_sector_3:+$num_sectors_metadata \
           --change-name 3:FWU-Metadata \
           --typecode 3:$metadata_type_uuid \
           \
           --new 4:$start_sector_4:+$num_sectors_metadata \
           --change-name 4:Bkup-FWU-Metadata \
           --typecode 4:$metadata_type_uuid

    # populate the GPT partitions
    dd if=$fip_bin of=$gpt_image bs=$sector_size seek=$start_sector_1 count=$num_sectors_fip conv=notrunc
    dd if=$fip_bin of=$gpt_image bs=$sector_size seek=$start_sector_2 count=$num_sectors_fip conv=notrunc
    dd if=$metadata_file of=$gpt_image bs=$sector_size seek=$start_sector_3 count=$num_sectors_metadata conv=notrunc
    dd if=$metadata_file of=$gpt_image bs=$sector_size seek=$start_sector_4 count=$num_sectors_metadata conv=notrunc
}

addtask do_generate_gpt after do_compile before do_install

do_install:append() {
    install -m 0644 ${BUILD_DIR}/fip_gpt.bin ${D}/firmware/fip_gpt-tc.bin
    ln -sf fip_gpt-tc.bin ${D}/firmware/fip_gpt.bin
}
