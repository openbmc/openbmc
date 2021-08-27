LICENSE = "CLOSED"
LIC_FILES_CHKSUM = ""

# TODO:  Manually copy the U-Boot signing key and customer-key-block here:
HPE_GXP_KEY_FILES_DIR = "${COREBASE}/meta-hpe/meta-gxp/recipes-bsp/image/files"

inherit deploy

do_deploy () {
  install -d ${DEPLOYDIR}

  # Copy in the bootblock
  install -m 644 ${HPE_GXP_KEY_FILES_DIR}/gxp-bootblock.bin ${DEPLOYDIR}/gxp-bootblock.bin

  # Copy in files from the files subdirectory
  install -m 644 ${HPE_GXP_KEY_FILES_DIR}/header.sig ${DEPLOYDIR}/hpe-uboot-header.section

  # Copy in the U-Boot signing key
  install -m 644 ${HPE_GXP_KEY_FILES_DIR}/private_key.pem ${DEPLOYDIR}/hpe-uboot-signing-key.pem

  # Copy in the customer keyblock
  install -m 644 ${HPE_GXP_KEY_FILES_DIR}/customer-key-block ${DEPLOYDIR}/customer-key-block
}

addtask deploy before do_build after do_compile

