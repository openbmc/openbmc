UBOOT_BINARY := "u-boot.${UBOOT_SUFFIX}"
BOOTBLOCK = "Poleg_bootblock.bin"
FULL_SUFFIX = "full"
MERGED_SUFFIX = "merged"
UBOOT_SUFFIX:append = ".${MERGED_SUFFIX}"

IGPS_DIR = "${STAGING_DIR_NATIVE}/${datadir}/npcm7xx-igps"

# Prepare the Bootblock and U-Boot images using npcm7xx-bingo
do_prepare_bootloaders() {
    local olddir="$(pwd)"
    cd ${DEPLOY_DIR_IMAGE}
    bingo ${IGPS_DIR}/BootBlockAndHeader_${IGPS_MACHINE}.xml \
            -o ${DEPLOY_DIR_IMAGE}/${BOOTBLOCK}.${FULL_SUFFIX}

    bingo ${IGPS_DIR}/UbootHeader_${IGPS_MACHINE}.xml \
            -o ${DEPLOY_DIR_IMAGE}/${UBOOT_BINARY}.${FULL_SUFFIX}

    bingo ${IGPS_DIR}/mergedBootBlockAndUboot.xml \
            -o ${DEPLOY_DIR_IMAGE}/${UBOOT_BINARY}.${MERGED_SUFFIX}
    cd "$olddir"
}

do_prepare_bootloaders[depends] += " \
    u-boot:do_deploy \
    npcm7xx-bootblock:do_deploy \
    npcm7xx-bingo-native:do_populate_sysroot \
    npcm7xx-igps-native:do_populate_sysroot \
    "

addtask do_prepare_bootloaders before do_generate_static after do_generate_rwfs_static

do_make_ubi:append() {
    # Concatenate the uboot and ubi partitions
    dd bs=1k conv=notrunc seek=${FLASH_UBOOT_OFFSET} \
        if=${DEPLOY_DIR_IMAGE}/u-boot.${UBOOT_SUFFIX} \
        of=${IMGDEPLOYDIR}/${IMAGE_NAME}.ubi.mtd
}

do_make_ubi[depends] += "${PN}:do_prepare_bootloaders"
do_generate_ubi_tar[depends] += "${PN}:do_prepare_bootloaders"
do_generate_static_tar[depends] += "${PN}:do_prepare_bootloaders"
