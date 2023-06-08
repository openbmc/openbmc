inherit image_version
unset do_get_version[noexec]
do_get_version[depends] = "os-release"

# do_get_version() is copied from meta-phosphor/classes/image_version.bbclass and
# modified to append the date and time to the version if a file named "developer"
# exists in the openbmc/build directory
def do_get_version(d):
    import configparser
    import io
    path = d.getVar('STAGING_DIR_TARGET', True) + d.getVar('sysconfdir', True)
    path = os.path.join(path, 'os-release')
    parser = configparser.SafeConfigParser(strict=False)
    parser.optionxform = str
    version = ''
    try:
        with open(path, 'r') as fd:
            buf = '[root]\n' + fd.read()
            fd = io.StringIO(buf)
            parser.readfp(fd)
            version = parser['root']['VERSION_ID']
            dev_path = d.getVar('PWD', True)
            dev_path = os.path.join(dev_path, 'developer')
            if os.path.isfile(dev_path):
                version = version[:-1] + str(d.getVar('IMAGE_VERSION_SUFFIX', True)).strip()
    except:
        pass
    return version

HPE_GXP_BOOTBLOCK_IMAGE ?= "gxp-bootblock.bin"
HPE_UBOOT_SIGNING_HEADER ?= "hpe-uboot-header.section"
HPE_UBOOT_SIGNING_HEADER_512 ?= "hpe-uboot-header-512.section"
HPE_UBOOT_SIGNING_KEY ?= "hpe-uboot-signing-key.pem"

# Offsets that are the same for the standard image and secure boot image
FLASH_SIZE = "31552"
FLASH_UBOOT_OFFSET = "0"
UBOOT_IMG_SIZE = "393216"
FLASH_KERNEL_OFFSET = "512"
FLASH_ROFS_OFFSET = "5376"
FLASH_RWFS_OFFSET = "29184"

# Standard image offsets
FLASH_STANDARD_SECTION_OFFSET = "31552"
FLASH_STANDARD_SECTION_END = "32768"

# Secure boot offsets
# offset at 0x01f7_0000 / 1024 = 32192
FLASH_SECTION_OFFSET = "32192"
# end is offset + 576
FLASH_SECTION_END = "32768"

# offset at 0x01ee_0000 / 1024 = 31616
FLASH_SECTION2_OFFSET = "31616"
FLASH_SECTION2_END = "32192"

# offset at 0x01c0_0000 / 1024 = 28672
FLASH_UBOOT2_OFFSET = "28672"

do_generate_static[depends] += " \
        gxp-bootblock:do_deploy \
        gxp-bootblock:do_populate_sysroot \
        "
make_image_links:append() {
    ln -sf ${DEPLOY_DIR_IMAGE}/hpe-section image-section

    if  [ -f ${DEPLOY_DIR_IMAGE}/hpe-section2 ]
    then
        ln -sf ${DEPLOY_DIR_IMAGE}/hpe-section2 image-section2
    fi
}

do_mk_static_symlinks:append() {
    ln -sf hpe-section image-section

    if [ -f ${DEPLOY_DIR_IMAGE}/hpe-section2 ]
    then
        ln -sf hpe-section2 image-section2
    fi
}

do_generate_static:prepend() {
    bb.build.exec_func("do_generate_hpe_image", d)
}

do_generate_static:append() {
    # hpe-section2 and u-boot2 only exist in the secure boot image.
    # If hpe-section2 exists, then this is secure boot.
    if os.path.exists(os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), 'hpe-section2')):
        _append_image(os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), 'hpe-section'),
                      int(d.getVar('FLASH_SECTION_OFFSET', True)),
                      int(d.getVar('FLASH_SECTION_END', True))) 

        _append_image(os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), 'hpe-section2'),
                      int(d.getVar('FLASH_SECTION2_OFFSET', True)),
                      int(d.getVar('FLASH_SECTION2_END', True)))

        _append_image(os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), 'u-boot.%s' % d.getVar('UBOOT_SUFFIX',True)),
                      int(d.getVar('FLASH_UBOOT2_OFFSET', True)),
                      int(d.getVar('FLASH_RWFS_OFFSET', True)))
    else:
        _append_image(os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), 'hpe-section'),
                      int(d.getVar('FLASH_STANDARD_SECTION_OFFSET', True)),
                      int(d.getVar('FLASH_STANDARD_SECTION_END', True))) 
}

# Generate the secure boot image by default
do_generate_hpe_image() {
    # Extract uboot 256K
    dd if=/dev/zero bs=1k count=256 > ${DEPLOY_DIR_IMAGE}/u-boot-tmp.${UBOOT_SUFFIX}
    dd bs=1k conv=notrunc seek=0 count=256\
            if=${DEPLOY_DIR_IMAGE}/u-boot.${UBOOT_SUFFIX} \
            of=${DEPLOY_DIR_IMAGE}/u-boot-tmp.${UBOOT_SUFFIX}

    
    # TODO - replace this openssl signing command line with whatever command you need to create a
    # digital signature of ${DEPLOY_DIR_IMAGE}/u-boot-tmp.${UBOOT_SUFFIX}

    openssl sha384 -sign ${DEPLOY_DIR_IMAGE}/${HPE_UBOOT_SIGNING_KEY} -out ${DEPLOY_DIR_IMAGE}/gxp_tmp.sig \
        ${DEPLOY_DIR_IMAGE}/u-boot-tmp.${UBOOT_SUFFIX}

    # Cat U-Boot header+signature
    cat ${DEPLOY_DIR_IMAGE}/${HPE_UBOOT_SIGNING_HEADER_512} ${DEPLOY_DIR_IMAGE}/gxp_tmp.sig \
        > ${DEPLOY_DIR_IMAGE}/gxp-uboot.sig


    # Create hpe-section
    dd if=/dev/zero bs=1k count=576 > ${DEPLOY_DIR_IMAGE}/hpe-section

    # Add U-Boot Header and Signature to hpe-section
    dd bs=1k conv=notrunc seek=0 \
        if=${DEPLOY_DIR_IMAGE}/gxp-uboot.sig \
        of=${DEPLOY_DIR_IMAGE}/hpe-section

    # Add gxp-bootblock to hpe-section
    dd bs=1k conv=notrunc seek=64 \
        if=${DEPLOY_DIR_IMAGE}/${HPE_GXP_BOOTBLOCK_IMAGE} \
        of=${DEPLOY_DIR_IMAGE}/hpe-section

    # hpe-section2 is the same as hpe-section up to this point
    cp ${DEPLOY_DIR_IMAGE}/hpe-section ${DEPLOY_DIR_IMAGE}/hpe-section2


    # Expand uboot to 384K
    dd if=/dev/zero bs=1k count=384 > ${DEPLOY_DIR_IMAGE}/u-boot-tmp.${UBOOT_SUFFIX}
    dd bs=1k conv=notrunc seek=0 count=384 \
            if=${DEPLOY_DIR_IMAGE}/u-boot.${UBOOT_SUFFIX} \
            of=${DEPLOY_DIR_IMAGE}/u-boot-tmp.${UBOOT_SUFFIX}

    # Remove unnecessary files
    rm ${DEPLOY_DIR_IMAGE}/u-boot.${UBOOT_SUFFIX} \
       ${DEPLOY_DIR_IMAGE}/gxp_tmp.sig \
       ${DEPLOY_DIR_IMAGE}/gxp-uboot.sig 

    mv ${DEPLOY_DIR_IMAGE}/u-boot-tmp.${UBOOT_SUFFIX} ${DEPLOY_DIR_IMAGE}/u-boot.${UBOOT_SUFFIX}

   # Check uboot image size equals to 384K
    size="$(wc -c < "${DEPLOY_DIR_IMAGE}/u-boot.${UBOOT_SUFFIX}")"
    if [ ${size} -ne ${UBOOT_IMG_SIZE} ]
    then
      echo "ERROR: STATIC - uBoot image size ${size} incorrect. Please try it again."
      exit 1
    fi
}

make_tar_of_images() {
  type=$1
  shift
  extra_files="$@"

  # Create the tar archive
  tar -h -cvf ${IMGDEPLOYDIR}/${IMAGE_NAME}.$type.mtd.tar \
    image-u-boot image-kernel image-rofs image-rwfs image-section* $extra_files

  # Create the min tar archive
  tar -h -cvf ${IMGDEPLOYDIR}/${IMAGE_NAME}.$type.mtd.min.tar \
    image-kernel image-rofs image-rwfs MANIFEST \
    image-kernel.sig image-rofs.sig image-rwfs.sig MANIFEST.sig publickey

  cd ${IMGDEPLOYDIR}
  ln -sf ${IMAGE_NAME}.$type.mtd.tar ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.$type.mtd.tar
  ln -sf ${IMAGE_NAME}.$type.mtd.min.tar ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.$type.mtd.min.tar
}

do_generate_static_tar[depends] += " obmc-phosphor-image:do_generate_static"

do_generate_static_tar() {

  ln -sf ${S}/MANIFEST MANIFEST
  ln -sf ${S}/publickey publickey
  make_image_links ${OVERLAY_BASETYPE} ${IMAGE_BASETYPE}

  # Check uboot image size equals to 384K
  size="$(wc -c < "image-u-boot")"
  if [ ${size} != ${UBOOT_IMG_SIZE} ]
  then
    echo "ERROR: TAR - uBoot image size ${size} incorrect. Please try it again."
    exit 1
  fi

  if [ -f image-section2 ]
  then
    make_signatures image-u-boot image-kernel image-rofs image-rwfs image-section image-section2 MANIFEST publickey
  else
    make_signatures image-u-boot image-kernel image-rofs image-rwfs image-section MANIFEST publickey
  fi

  make_tar_of_images static MANIFEST publickey ${signature_files}

  # Maintain non-standard legacy link.
  cd ${IMGDEPLOYDIR}
  ln -sf ${IMAGE_NAME}.static.mtd.tar ${IMGDEPLOYDIR}/${MACHINE}-${DATETIME}.tar
  ln -sf ${IMAGE_NAME}.static.mtd.min.tar ${IMGDEPLOYDIR}/${MACHINE}-${DATETIME}.min.tar
}
