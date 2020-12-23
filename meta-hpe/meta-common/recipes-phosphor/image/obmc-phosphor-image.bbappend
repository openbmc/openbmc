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
HPE_UBOOT_SIGNING_KEY ?= "hpe-uboot-signing-key.pem"

FLASH_SIZE = "31552"
FLASH_UBOOT_OFFSET = "0"
FLASH_KERNEL_OFFSET = "512"
FLASH_ROFS_OFFSET = "5376"
FLASH_RWFS_OFFSET = "29184"
FLASH_SECTION_OFFSET = "31552"
FLASH_SECTION_END = "32768"

UBOOT_IMG_SIZE = "393216"

do_generate_static[depends] += " \
   		  gxp-bootblock:do_deploy \
        gxp-bootblock:do_populate_sysroot \
        "


make_image_links_append() {
    ln -sf ${DEPLOY_DIR_IMAGE}/hpe-section image-section
}

do_mk_static_symlinks_append() {
    ln -sf hpe-section image-section
}

do_generate_static_prepend() {
    bb.build.exec_func("do_generate_hpe_image", d)
}

do_generate_static_append() {
    _append_image(os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True),
                               'hpe-section'),
                  int(d.getVar('FLASH_SECTION_OFFSET', True)),
                  int(d.getVar('FLASH_SECTION_END', True))) 
}

do_generate_hpe_image() {
    # Extract uboot 256K
    dd if=/dev/zero bs=1k count=256 > ${DEPLOY_DIR_IMAGE}/u-boot-tmp.${UBOOT_SUFFIX}
    dd bs=1k conv=notrunc seek=0 count=256\
            if=${DEPLOY_DIR_IMAGE}/u-boot.${UBOOT_SUFFIX} \
            of=${DEPLOY_DIR_IMAGE}/u-boot-tmp.${UBOOT_SUFFIX}

    # Sign uboot 256K
    openssl sha256 -sign ${DEPLOY_DIR_IMAGE}/${HPE_UBOOT_SIGNING_KEY} -out ${DEPLOY_DIR_IMAGE}/gxp_tmp.sig \
            ${DEPLOY_DIR_IMAGE}/u-boot-tmp.${UBOOT_SUFFIX}

    # Expand (header+signature) to 4K
    cat ${DEPLOY_DIR_IMAGE}/${HPE_UBOOT_SIGNING_HEADER} ${DEPLOY_DIR_IMAGE}/gxp_tmp.sig \
         > ${DEPLOY_DIR_IMAGE}/gxp.sig

    # Add Header and Signature to hpe-section (from 60K)
    dd bs=1k conv=notrunc seek=60 \
          if=${DEPLOY_DIR_IMAGE}/gxp.sig \
          of=${DEPLOY_DIR_IMAGE}/hpe-section

    # Add ubb to hpe-section
    dd bs=1k conv=notrunc seek=64 \
          if=${DEPLOY_DIR_IMAGE}/${HPE_GXP_BOOTBLOCK_IMAGE} \
          of=${DEPLOY_DIR_IMAGE}/hpe-section

    # Expand uboot to 384K
    dd if=/dev/zero bs=1k count=384 > ${DEPLOY_DIR_IMAGE}/u-boot-tmp.${UBOOT_SUFFIX}
    dd bs=1k conv=notrunc seek=0 count=384\
            if=${DEPLOY_DIR_IMAGE}/u-boot.${UBOOT_SUFFIX} \
            of=${DEPLOY_DIR_IMAGE}/u-boot-tmp.${UBOOT_SUFFIX}

    # Remove unnecessary files
    rm ${DEPLOY_DIR_IMAGE}/u-boot.${UBOOT_SUFFIX} \
       ${DEPLOY_DIR_IMAGE}/gxp_tmp.sig \
       ${DEPLOY_DIR_IMAGE}/gxp.sig

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
    image-u-boot image-kernel image-rofs image-rwfs image-section $extra_files

  cd ${IMGDEPLOYDIR}
  ln -sf ${IMAGE_NAME}.$type.mtd.tar ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.$type.mtd.tar
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

  make_signatures image-u-boot image-kernel image-rofs image-rwfs image-section MANIFEST publickey
  make_tar_of_images static MANIFEST publickey ${signature_files}

  # Maintain non-standard legacy link.
  cd ${IMGDEPLOYDIR}
  ln -sf ${IMAGE_NAME}.static.mtd.tar ${IMGDEPLOYDIR}/${MACHINE}-${DATETIME}.tar
}
