UBOOT_BINARY := "u-boot.${UBOOT_SUFFIX}"
BOOTBLOCK = "BootBlockAndHeader.bin"
ATF_BINARY := "bl31AndHeader.bin"
OPTEE_BINARY := "teeAndHeader.bin"
KMT_BINARY = "KmtAndHeader.bin"
TIPFWL0_BINARY = "TipFwAndHeader_L0.bin"
TIPFWL1_BINARY = "TipFwAndHeader_L1.bin"
KMT_TIPFWL0_BINARY = "Kmt_TipFwL0.bin"
KMT_TIPFWL0L1_BINARY = "Kmt_TipFwL0L1.bin"
KMT_TIPFW_BB_BINARY = "Kmt_TipFw_BootBlock.bin"
KMT_TIPFW_BB_BL31_BINARY = "Kmt_TipFw_BootBlock_BL31.bin"
KMT_TIPFW_BB_BL31_TEE_BINARY = "Kmt_TipFw_BootBlock_BL31_Tee.bin"
KMT_TIPFW_BB_BL31_TEE_UBOOT_BINARY = "Kmt_TipFw_BootBlock_BL31_Tee_uboot.bin"
KMT_TIPFW_BB_UBOOT_BINARY = "u-boot.bin.merged"
FULL_SUFFIX = "full"
MERGED_SUFFIX = "merged"
UBOOT_SUFFIX:append = ".${MERGED_SUFFIX}"

IGPS_DIR = "${STAGING_DIR_NATIVE}/${datadir}/npcm8xx-igps"
inherit logging

# Prepare the Bootblock and U-Boot images using npcm8xx-bingo
do_prepare_bootloaders() {
    local olddir="$(pwd)"
    cd ${DEPLOY_DIR_IMAGE}

    if [ "${SECURED_TIPFW}" = "False" ]; then
        bingo ${IGPS_DIR}/KmtAndHeader_${IGPS_MACHINE}.xml \
                -o ${DEPLOY_DIR_IMAGE}/${KMT_BINARY}

        bingo ${IGPS_DIR}/TipFwAndHeader_L0_${IGPS_MACHINE}.xml \
                -o ${DEPLOY_DIR_IMAGE}/${TIPFWL0_BINARY}

        bingo ${IGPS_DIR}/TipFwAndHeader_L1_${IGPS_MACHINE}.xml \
                -o ${DEPLOY_DIR_IMAGE}/${TIPFWL1_BINARY}
    fi

    if [ "${SECURED_OS}" = "True" ]; then
        bingo ${IGPS_DIR}/BL31_AndHeader_${IGPS_MACHINE}.xml \
                -o ${DEPLOY_DIR_IMAGE}/${ATF_BINARY}

        bingo ${IGPS_DIR}/OpTeeAndHeader_${IGPS_MACHINE}.xml \
                -o ${DEPLOY_DIR_IMAGE}/${OPTEE_BINARY}
    fi

    bingo ${IGPS_DIR}/BootBlockAndHeader_${IGPS_MACHINE}.xml \
            -o ${DEPLOY_DIR_IMAGE}/${BOOTBLOCK}

    bingo ${IGPS_DIR}/UbootHeader_${IGPS_MACHINE}.xml \
            -o ${UBOOT_BINARY}.${FULL_SUFFIX}
             
    cd "$olddir"
}

python do_merge_bootloaders() {

    def crc32_tab_val( c ):
        crc = c  % (1<<32)
        for x in range(0, 8):
            if ( crc & 0x00000001 ):
                crc = ( (crc >> 1)  % (1<<32) ) ^ 0xEDB88320
            else:
                crc =   crc >> 1
            crc = crc  % (1<<32)
        return crc

    def update_crc( crc, c ):
        long_c = (0x000000ff & c)   % (1<<32)
        tmp = (crc ^ long_c)    % (1<<32)
        crc = ((crc >> 8) ^ crc32_tab_val( tmp & 0xff ))   % (1<<32)
        crc = crc  % (1<<32)
        return crc;

    def CalcCRC32(bin_filename, begin_offset, embed_ecc, output_filename):
        try:
            input_size = os.path.getsize(bin_filename)
            if (begin_offset >= input_size):
                print("\nfile too small\n")

            crc = 0
            with open(bin_filename, "rb") as binary_file:
                tmp = binary_file.read(begin_offset)
                while True:
                    va = binary_file.read(1)
                    if va:
                        crc = update_crc(crc, ord(va))
                    else:
                        break

            crc = crc  & 0xffffffff
            with open(bin_filename, "rb") as binary_file:
                input = binary_file.read()
            crc_arr = bytearray(4)
            for ind in range(4):
                crc_arr[ind] = (crc >> (ind*8) ) & 255
            output = input[:embed_ecc] + crc_arr + input[(embed_ecc + 4):]
            output_file = open(output_filename, "w+b")
            output_file.write(output)
            output_file.close()

        except:
            raise
        finally:
            return

    def CRC32_binary(binfile, begin_offset, embed_ecc, outputFile):
        CalcCRC32(binfile, begin_offset, embed_ecc, outputFile)

    def Merge_bin_files_and_pad(inF1, inF2, outF, align, padding_at_end):
        padding_size = 0
        padding_size_end = 0
        F1_size = os.path.getsize(inF1)
        F2_size = os.path.getsize(inF2)

        if ((F1_size % align) != 0):
            padding_size = align - (F1_size % align)

        if ((F2_size % align) != 0):
            padding_size_end = align - (F2_size % align)

        with open(outF, "wb") as file3:
            with open(inF1, "rb") as file1:
                data = file1.read()
                file3.write(data)

            file3.write(b'\xFF' * padding_size)

            with open(inF2, "rb") as file2:
                data = file2.read()
                file3.write(data)

            file3.write(b'\xFF' * padding_size_end)

        file1.close()
        file2.close()
        file3.close()

    if d.getVar('SECURED_TIPFW', True) == "True":
        d.setVar('KMT_TIPFW_BINARY', "Kmt_TipFwL0_TipFwL1.bin")

        Merge_bin_files_and_pad(os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('KMT_TIPFW_BINARY',True)),
            os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('BOOTBLOCK',True)),
            os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('KMT_TIPFW_BB_BINARY',True)),
            0x1000, 0x20)
    else:
        CRC32_binary(os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('KMT_BINARY',True)),
            112, 12,
            os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('KMT_BINARY',True)))

        CRC32_binary(os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('TIPFWL0_BINARY',True)),
            112, 12,
            os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('TIPFWL0_BINARY',True)))

        CRC32_binary(os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('TIPFWL1_BINARY',True)),
            112, 12,
            os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('TIPFWL1_BINARY',True)))

        Merge_bin_files_and_pad(os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('KMT_BINARY',True)),
            os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('TIPFWL0_BINARY',True)),
            os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('KMT_TIPFWL0_BINARY',True)),
            0x1000, 0x20)

        Merge_bin_files_and_pad(os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('KMT_TIPFWL0_BINARY',True)),
            os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('TIPFWL1_BINARY',True)),
            os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('KMT_TIPFWL0L1_BINARY',True)),
            0x1000, 0x20)

        os.remove(os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('KMT_TIPFWL0_BINARY',True)))

        Merge_bin_files_and_pad(os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('KMT_TIPFWL0L1_BINARY',True)),
            os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('BOOTBLOCK',True)),
            os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('KMT_TIPFW_BB_BINARY',True)),
            0x1000, 0x20)

    if d.getVar('SECURED_OS', True) == "True":
        Merge_bin_files_and_pad(os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('KMT_TIPFW_BB_BINARY',True)),
            os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('ATF_BINARY',True)),
            os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('KMT_TIPFW_BB_BL31_BINARY',True)),
            0x1000, 0x20)

        Merge_bin_files_and_pad(os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('KMT_TIPFW_BB_BL31_BINARY',True)),
            os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('OPTEE_BINARY',True)),
            os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('KMT_TIPFW_BB_BL31_TEE_BINARY',True)),
            0x1000, 0x20)

        Merge_bin_files_and_pad(os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('KMT_TIPFW_BB_BL31_TEE_BINARY',True)),
            os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s.full' % d.getVar('UBOOT_BINARY',True)),
            os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('KMT_TIPFW_BB_UBOOT_BINARY',True)),
            0x1000, 0x20)
    else:
        Merge_bin_files_and_pad(os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('KMT_TIPFW_BB_BINARY',True)),
            os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s.full' % d.getVar('UBOOT_BINARY',True)),
            os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), '%s' % d.getVar('KMT_TIPFW_BB_UBOOT_BINARY',True)),
            0x1000, 0x20)
}

prepare_secureos = "${@ "arm-trusted-firmware:do_deploy optee-os:do_deploy" if bb.utils.to_boolean(d.getVar('SECURED_OS')) else "" }"

do_prepare_bootloaders[depends] += " \
    npcm8xx-kmt:do_deploy \
    npcm8xx-kmt-tipfwl0l1:do_deploy \
    npcm8xx-tipfw-l0:do_deploy \
    npcm8xx-tipfw-l1:do_deploy \
    npcm8xx-bootblock:do_deploy \
    ${prepare_secureos} \
    npcm7xx-bingo-native:do_populate_sysroot \
    npcm8xx-igps-native:do_populate_sysroot \
    "

# link images for we only need to flash partial image with idea name
do_generate_ext4_tar:append() {
    cd ${DEPLOY_DIR_IMAGE}
    ln -sf ${UBOOT_BINARY} image-u-boot
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
