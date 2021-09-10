do_generate_hpe_image() {
    # Add gxp-bootblock to hpe-section
    dd bs=1k conv=notrunc seek=64 \
          if=${DEPLOY_DIR_IMAGE}/${HPE_GXP_BOOTBLOCK_IMAGE} \
          of=${DEPLOY_DIR_IMAGE}/hpe-section
}

do_generate_static_tar() {

  ln -sf ${S}/MANIFEST MANIFEST
  ln -sf ${S}/publickey publickey
  make_image_links ${OVERLAY_BASETYPE} ${IMAGE_BASETYPE}

  make_signatures image-u-boot image-kernel image-rofs image-rwfs image-section MANIFEST publickey
  make_tar_of_images static MANIFEST publickey ${signature_files}

  # Maintain non-standard legacy link.
  cd ${IMGDEPLOYDIR}
  ln -sf ${IMAGE_NAME}.static.mtd.tar ${IMGDEPLOYDIR}/${MACHINE}-${DATETIME}.tar
}

