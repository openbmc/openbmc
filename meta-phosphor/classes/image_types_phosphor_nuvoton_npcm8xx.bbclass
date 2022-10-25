UBOOT_BINARY := "u-boot.${UBOOT_SUFFIX}"
BOOTBLOCK = "BootBlockAndHeader.bin"
ATF_BINARY := "bl31AndHeader.bin"
OPTEE_BINARY := "teeAndHeader.bin"
KMT_TIPFW_BINARY := "Kmt_TipFwL0_Skmt_TipFwL1.bin"
KMT_TIPFW_BB_BINARY = "Kmt_TipFw_BootBlock.bin"
KMT_TIPFW_BB_BL31_BINARY = "Kmt_TipFw_BootBlock_BL31.bin"
KMT_TIPFW_BB_BL31_TEE_BINARY = "Kmt_TipFw_BootBlock_BL31_Tee.bin"
KMT_TIPFW_BB_UBOOT_BINARY = "u-boot.bin.merged"
FULL_SUFFIX = "full"
MERGED_SUFFIX = "merged"
UBOOT_SUFFIX:append = ".${MERGED_SUFFIX}"

IGPS_DIR = "${STAGING_DIR_NATIVE}/${datadir}/npcm8xx-igps"

# Prepare the Bootblock and U-Boot images using npcm8xx-bingo
do_prepare_bootloaders() {
    local olddir="$(pwd)"
    cd ${DEPLOY_DIR_IMAGE}

    bingo ${IGPS_DIR}/BL31_AndHeader.xml \
            -o ${DEPLOY_DIR_IMAGE}/${ATF_BINARY}

    bingo ${IGPS_DIR}/OpTeeAndHeader.xml \
            -o ${DEPLOY_DIR_IMAGE}/${OPTEE_BINARY}

    bingo ${IGPS_DIR}/BootBlockAndHeader_${DEVICE_GEN}_${IGPS_MACHINE}.xml \
            -o ${DEPLOY_DIR_IMAGE}/${BOOTBLOCK}

    bingo ${IGPS_DIR}/UbootHeader_${DEVICE_GEN}.xml \
            -o ${UBOOT_BINARY}.${FULL_SUFFIX}

    cd "$olddir"
}

python do_merge_bootloaders() {

    def Merge_bin_files_and_pad(inF1, inF2, outF, align, align_end):
        padding_size = 0
        padding_size_end = 0
        F1_size = os.path.getsize(inF1)
        F2_size = os.path.getsize(inF2)

        if ((F1_size % align) != 0):
            padding_size = align - (F1_size % align)

        if ((F2_size % align_end) != 0):
            padding_size_end = align_end - (F2_size % align_end)

        with open(outF, "wb") as file3:
            with open(inF1, "rb") as file1:
                data = file1.read()
                file3.write(data)

            file3.write(b'\xFF' * padding_size)

            with open(inF2, "rb") as file2:
                data = file2.read()
                file3.write(data)

            file3.write(b'\xFF' * padding_size_end)

    Merge_bin_files_and_pad(os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('KMT_TIPFW_BINARY',True)),
        os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('BOOTBLOCK',True)),
        os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('KMT_TIPFW_BB_BINARY',True)),
        int(d.getVar('BB_ALIGN', True)), int(d.getVar('ALIGN_END', True)))

    Merge_bin_files_and_pad(os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('KMT_TIPFW_BB_BINARY',True)),
        os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('ATF_BINARY',True)),
        os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('KMT_TIPFW_BB_BL31_BINARY',True)),
        int(d.getVar('ATF_ALIGN', True)), int(d.getVar('ALIGN_END', True)))

    Merge_bin_files_and_pad(os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('KMT_TIPFW_BB_BL31_BINARY',True)),
        os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('OPTEE_BINARY',True)),
        os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('KMT_TIPFW_BB_BL31_TEE_BINARY',True)),
        int(d.getVar('OPTEE_ALIGN', True)), int(d.getVar('ALIGN_END', True)))

    Merge_bin_files_and_pad(os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('KMT_TIPFW_BB_BL31_TEE_BINARY',True)),
        os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s.full' % d.getVar('UBOOT_BINARY',True)),
        os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('KMT_TIPFW_BB_UBOOT_BINARY',True)),
        int(d.getVar('UBOOT_ALIGN', True)), int(d.getVar('ALIGN_END', True)))
}

do_prepare_bootloaders[depends] += " \
    npcm8xx-tip-fw:do_deploy \
    npcm8xx-bootblock:do_deploy \
    u-boot-nuvoton:do_deploy \
    trusted-firmware-a:do_deploy \
    optee-os:do_deploy \
    npcm7xx-bingo-native:do_populate_sysroot \
    npcm8xx-igps-native:do_populate_sysroot \
    "

# link images for we only need to flash partial image with idea name
do_generate_ext4_tar:append() {
    cd ${DEPLOY_DIR_IMAGE}
    ln -sf ${UBOOT_BINARY}.${MERGED_SUFFIX} image-u-boot
    ln -sf ${DEPLOY_DIR_IMAGE}/${FLASH_KERNEL_IMAGE} image-kernel
    ln -sf ${S}/ext4/${IMAGE_LINK_NAME}.${FLASH_EXT4_BASETYPE}.zst image-rofs
    ln -sf ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.rwfs.${FLASH_EXT4_OVERLAY_BASETYPE} image-rwfs
    ln -sf ${IMAGE_NAME}.rootfs.wic.gz image-emmc.gz
}

addtask do_prepare_bootloaders before do_generate_static after do_generate_rwfs_static
addtask do_merge_bootloaders before do_generate_static after do_prepare_bootloaders
addtask do_merge_bootloaders before do_generate_ext4_tar after do_prepare_bootloaders

# Include the full bootblock and u-boot in the final static image
python do_generate_static:append() {
    _append_image(os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True),
                               'u-boot.%s' % d.getVar('UBOOT_SUFFIX',True)),
                  int(d.getVar('FLASH_UBOOT_OFFSET', True)),
                  int(d.getVar('FLASH_KERNEL_OFFSET', True)))
}

do_make_ubi:append() {
    # Concatenate the uboot and ubi partitions
    dd bs=1k conv=notrunc seek=${FLASH_UBOOT_OFFSET} \
        if=${DEPLOY_DIR_IMAGE}/u-boot.${UBOOT_SUFFIX} \
        of=${IMGDEPLOYDIR}/${IMAGE_NAME}.ubi.mtd
}

do_make_ubi[depends] += "${PN}:do_prepare_bootloaders"
do_generate_ubi_tar[depends] += "${PN}:do_prepare_bootloaders"
do_generate_ubi_tar[depends] += "${PN}:do_merge_bootloaders"
do_generate_static_tar[depends] += "${PN}:do_prepare_bootloaders"
do_generate_static_tar[depends] += "${PN}:do_merge_bootloaders"
do_generate_ext4_tar[depends] += "${PN}:do_prepare_bootloaders"
do_generate_ext4_tar[depends] += "${PN}:do_merge_bootloaders"
