UBOOT_BINARY := "u-boot.${UBOOT_SUFFIX}"
BB_HEADER_BINARY := "BootBlockAndHeader.bin"
BL31_HEADER_BINARY := "bl31AndHeader.bin"
OPTEE_HEADER_BINARY := "teeAndHeader.bin"
KMT_TIPFW_BINARY := "Kmt_TipFwL0_Skmt_TipFwL1.bin"
KMT_TIPFW_BB_BINARY = "Kmt_TipFw_BootBlock.bin"
KMT_TIPFW_BB_BL31_BINARY = "Kmt_TipFw_BootBlock_BL31.bin"
KMT_TIPFW_BB_BL31_TEE_BINARY = "Kmt_TipFw_BootBlock_BL31_Tee.bin"
KMT_TIPFW_BB_UBOOT_BINARY = "u-boot.bin.merged"

BB_BL31_BINARY = "BootBlock_BL31_no_tip.bin"
BB_BL31_TEE_BINARY = "BootBlock_BL31_Tee_no_tip.bin"
BB_BL31_TEE_UBOOT_BINARY = "u-boot.bin.merged"

FULL_SUFFIX = "full"
MERGED_SUFFIX = "merged"
UBOOT_SUFFIX:append = ".${MERGED_SUFFIX}"
UBOOT_HEADER_BINARY := "${UBOOT_BINARY}.${FULL_SUFFIX}"

IGPS_DIR = "${STAGING_DIR_NATIVE}/${datadir}/npcm8xx-igps"

BB_BIN = "arbel_a35_bootblock.bin"
BL31_BIN = "bl31.bin"
OPTEE_BIN = "tee.bin"
UBOOT_BIN = "u-boot.bin"
BB_NO_TIP_BIN = "arbel_a35_bootblock_no_tip.bin"

# Align images if needed
python do_pad_binary() {
    TIP_IMAGE = d.getVar('TIP_IMAGE', True)
    def Pad_bin_file_inplace(inF, align):
        padding_size = 0

        F_size = os.path.getsize(inF)

        if ((F_size % align) == 0):
            return

        padding_size = align - (F_size % align)

        infile = open(inF, "ab")
        infile.seek(0, 2)
        infile.write(b'\x00' * padding_size)
        infile.close()

    if TIP_IMAGE == "True":
        Pad_bin_file_inplace(os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True),
        '%s' % d.getVar('BB_BIN',True)), int(d.getVar('PAD_ALIGN', True)))
    else:
        Pad_bin_file_inplace(os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True),
        '%s' % d.getVar('BB_NO_TIP_BIN',True)), int(d.getVar('PAD_ALIGN', True)))

    Pad_bin_file_inplace(os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True),
    '%s' % d.getVar('BL31_BIN',True)), int(d.getVar('PAD_ALIGN', True)))

    Pad_bin_file_inplace(os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True),
    '%s' % d.getVar('OPTEE_BIN',True)), int(d.getVar('PAD_ALIGN', True)))

    Pad_bin_file_inplace(os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True),
        '%s' % d.getVar('UBOOT_BIN',True)), int(d.getVar('PAD_ALIGN', True)))
}

# Prepare the Bootblock and U-Boot images using npcm8xx-bingo
do_prepare_bootloaders() {
    local olddir="$(pwd)"
    cd ${DEPLOY_DIR_IMAGE}

    bingo ${IGPS_DIR}/BL31_AndHeader.xml \
            -o ${BL31_HEADER_BINARY}

    bingo ${IGPS_DIR}/OpTeeAndHeader.xml \
            -o ${OPTEE_HEADER_BINARY}

    if [ "${TIP_IMAGE}" = "True" ]; then
    bingo ${IGPS_DIR}/BootBlockAndHeader_${DEVICE_GEN}_${IGPS_MACHINE}.xml \
            -o ${BB_HEADER_BINARY}
    else
    bingo ${IGPS_DIR}/BootBlockAndHeader_${DEVICE_GEN}_${IGPS_MACHINE}_NoTip.xml \
            -o ${BB_HEADER_BINARY}
    fi

    bingo ${IGPS_DIR}/UbootHeader_${DEVICE_GEN}.xml \
            -o ${UBOOT_HEADER_BINARY}

    cd "$olddir"
}

check_keys() {
    if [ -n "${KEY_FOLDER}" ]; then
        echo "local"
    else
        echo "default"
    fi
}

# Sign images for secure os be enabled and TIP mode only
do_sign_binary() {
    if [ "${SECURED_IMAGE}" != "True" -o "${TIP_IMAGE}" != "True" ]; then
       return
    fi
    checked=`check_keys`
    if [ "${checked}" = "local" ]; then
        bbnote "Sign image with local keys"
        key_bb=${KEY_FOLDER}/${KEY_BB}
        key_bl31=${KEY_FOLDER}/${KEY_BL31}
        key_optee=${KEY_FOLDER}/${KEY_OPTEE}
        key_uboot=${KEY_FOLDER}/${KEY_UBOOT}
    else
        bbnote "Sign image with default keys"
        key_bb=${KEY_FOLDER_DEFAULT}/${KEY_BB}
        key_bl31=${KEY_FOLDER_DEFAULT}/${KEY_BL31}
        key_optee=${KEY_FOLDER_DEFAULT}/${KEY_OPTEE}
        key_uboot=${KEY_FOLDER_DEFAULT}/${KEY_UBOOT}
    fi
    bbnote "BB sign key from ${checked}: ${key_bb}"
    bbnote "BL31 sign key from ${checked}: ${key_bl31}"
    bbnote "OPTEE sign key from ${checked}: ${key_optee}"
    bbnote "UBOOT sign key from ${checked}: ${key_uboot}"
    # Used to embed the key index inside the image, usually at offset 0x140
    python3 ${IGPS_DIR}/BinarySignatureGenerator.py Replace_binary_single_byte \
        ${DEPLOY_DIR_IMAGE}/${BB_HEADER_BINARY} 140 ${KEY_BB_INDEX}

    python3 ${IGPS_DIR}/BinarySignatureGenerator.py Replace_binary_single_byte \
        ${DEPLOY_DIR_IMAGE}/${BL31_HEADER_BINARY} 140 ${SKMT_BL31_KEY_INDEX}

    python3 ${IGPS_DIR}/BinarySignatureGenerator.py Replace_binary_single_byte \
        ${DEPLOY_DIR_IMAGE}/${OPTEE_HEADER_BINARY} 140 ${SKMT_BL32_KEY_INDEX}

    python3 ${IGPS_DIR}/BinarySignatureGenerator.py Replace_binary_single_byte \
        ${DEPLOY_DIR_IMAGE}/${UBOOT_HEADER_BINARY} 140 ${SKMT_BL33_KEY_INDEX}

    # Sign specific image with specific key
    res=`python3 ${IGPS_DIR}/BinarySignatureGenerator.py Sign_binary \
        ${DEPLOY_DIR_IMAGE}/${BB_HEADER_BINARY} 112 ${key_bb} 16 \
        ${DEPLOY_DIR_IMAGE}/${BB_HEADER_BINARY} ${SIGN_TYPE} 0 ${KEY_BB_ID}

        python3 ${IGPS_DIR}/BinarySignatureGenerator.py Sign_binary \
        ${DEPLOY_DIR_IMAGE}/${BL31_HEADER_BINARY} 112 ${key_bl31} 16 \
        ${DEPLOY_DIR_IMAGE}/${BL31_HEADER_BINARY} ${SIGN_TYPE} 0 ${KEY_BL31_ID}

        python3 ${IGPS_DIR}/BinarySignatureGenerator.py Sign_binary \
        ${DEPLOY_DIR_IMAGE}/${OPTEE_HEADER_BINARY} 112 ${key_optee} 16 \
        ${DEPLOY_DIR_IMAGE}/${OPTEE_HEADER_BINARY} ${SIGN_TYPE} 0 ${KEY_OPTEE_ID}

        python3 ${IGPS_DIR}/BinarySignatureGenerator.py Sign_binary \
        ${DEPLOY_DIR_IMAGE}/${UBOOT_HEADER_BINARY} 112 ${key_uboot} 16 \
        ${DEPLOY_DIR_IMAGE}/${UBOOT_HEADER_BINARY} ${SIGN_TYPE} 0 ${KEY_UBOOT_ID}`

    # Stop full image build process when sign binary got failed
    set +e
    err=`echo $res | grep -E "missing|Invalid|failed"`
    if [ -n "${err}" ]; then
        bbfatal "Sign binary failed: keys are not found or invalid. Please check your KEY_FOLDER and KEY definition."
    fi
    set -e
}

python do_merge_bootloaders() {
    TIP_IMAGE = d.getVar('TIP_IMAGE', True)
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

    if TIP_IMAGE == "True":
        Merge_bin_files_and_pad(os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('KMT_TIPFW_BINARY',True)),
        os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('BB_HEADER_BINARY',True)),
        os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('KMT_TIPFW_BB_BINARY',True)),
        int(d.getVar('BB_ALIGN', True)), int(d.getVar('ALIGN_END', True)))

        Merge_bin_files_and_pad(os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('KMT_TIPFW_BB_BINARY',True)),
        os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('BL31_HEADER_BINARY',True)),
        os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('KMT_TIPFW_BB_BL31_BINARY',True)),
        int(d.getVar('ATF_ALIGN', True)), int(d.getVar('ALIGN_END', True)))

        Merge_bin_files_and_pad(os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('KMT_TIPFW_BB_BL31_BINARY',True)),
        os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('OPTEE_HEADER_BINARY',True)),
        os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('KMT_TIPFW_BB_BL31_TEE_BINARY',True)),
        int(d.getVar('OPTEE_ALIGN', True)), int(d.getVar('ALIGN_END', True)))

        Merge_bin_files_and_pad(os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('KMT_TIPFW_BB_BL31_TEE_BINARY',True)),
        os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('UBOOT_HEADER_BINARY',True)),
        os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('KMT_TIPFW_BB_UBOOT_BINARY',True)),
        int(d.getVar('UBOOT_ALIGN', True)), int(d.getVar('ALIGN_END', True)))
    else:
        Merge_bin_files_and_pad(os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('BB_HEADER_BINARY',True)),
        os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('BL31_HEADER_BINARY',True)),
        os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('BB_BL31_BINARY',True)),
        int(d.getVar('ATF_ALIGN', True)), int(d.getVar('ALIGN_END', True)))

        Merge_bin_files_and_pad(os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('BB_BL31_BINARY',True)),
        os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('OPTEE_HEADER_BINARY',True)),
        os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('BB_BL31_TEE_BINARY',True)),
        int(d.getVar('OPTEE_ALIGN', True)), int(d.getVar('ALIGN_END', True)))

        Merge_bin_files_and_pad(os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('BB_BL31_TEE_BINARY',True)),
        os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('UBOOT_HEADER_BINARY',True)),
        os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('BB_BL31_TEE_UBOOT_BINARY',True)),
        int(d.getVar('UBOOT_ALIGN', True)), int(d.getVar('ALIGN_END', True)))
}

do_pad_binary[depends] += " \
    ${@'npcm8xx-tip-fw:do_deploy' if d.getVar('TIP_IMAGE', True) == 'True' else ''} \
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

addtask do_pad_binary before do_prepare_bootloaders
addtask do_sign_binary before do_merge_bootloaders after do_prepare_bootloaders
addtask do_prepare_bootloaders before do_generate_static after do_generate_rwfs_static
addtask do_merge_bootloaders before do_generate_static after do_sign_binary
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
