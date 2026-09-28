DESCRIPTION = "Firmware Image for Juno to be copied to the Configuration \
microSD card"

LICENSE = "BSD-3-Clause"
SECTION = "firmware"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9"

INHIBIT_DEFAULT_DEPS = "1"
DEPENDS = "trusted-firmware-a virtual/kernel virtual/control-processor-firmware"

PACKAGE_ARCH = "${MACHINE_ARCH}"

COMPATIBLE_MACHINE = "juno"

SRC_URI = " \
    file://mbb_v151.ebf \
    file://io_b118.bit \
    file://tapid.arm \
    file://pms_v103.bin \
    file://pms_v104.bin \
    file://pms_v105.bin \
    file://mb-board.txt \
    file://site1-HBI0262B-board.txt \
    file://site1-HBI0262C-board.txt \
    file://site1-HBI0262D-board.txt \
    file://site1-images.txt \
    file://hdlcdclk.dat \
    file://startup.nsh \
    file://selftest \
    file://config.txt \
    file://uEnv.txt \
"

FIRMWARE_DIR = "juno-firmware"
S = "${UNPACKDIR}"

inherit deploy nopackages

do_configure[noexec] = "1"
do_compile[noexec] = "1"

# The ${D} is used as a temporary directory and we don't generate any
# packages for this recipe.
do_install() {
    install -d ${D}/${FIRMWARE_DIR}/
    cp -f ${UNPACKDIR}/config.txt ${D}/${FIRMWARE_DIR}/

    # Files in the MB directory are mostly the same, except for the PMS file and references to the PMS file by name
    install -d ${D}/${FIRMWARE_DIR}/MB/HBI0262B/
    cp -f ${UNPACKDIR}/mbb_v151.ebf ${D}/${FIRMWARE_DIR}/MB/HBI0262B/
    cp -f ${UNPACKDIR}/io_b118.bit ${D}/${FIRMWARE_DIR}/MB/HBI0262B/
    cp -f ${UNPACKDIR}/tapid.arm  ${D}/${FIRMWARE_DIR}/MB/HBI0262B/
    cp -f ${UNPACKDIR}/pms_v103.bin ${D}/${FIRMWARE_DIR}/MB/HBI0262B/
    cp -f ${UNPACKDIR}/mb-board.txt ${D}/${FIRMWARE_DIR}/MB/HBI0262B/board.txt

    install -d ${D}/${FIRMWARE_DIR}/MB/HBI0262C/
    cp -f ${UNPACKDIR}/mbb_v151.ebf ${D}/${FIRMWARE_DIR}/MB/HBI0262C/
    cp -f ${UNPACKDIR}/io_b118.bit ${D}/${FIRMWARE_DIR}/MB/HBI0262C/
    cp -f ${UNPACKDIR}/tapid.arm  ${D}/${FIRMWARE_DIR}/MB/HBI0262C/
    cp -f ${UNPACKDIR}/pms_v104.bin ${D}/${FIRMWARE_DIR}/MB/HBI0262C/
    sed 's/pms_v103/pms_v104/g' ${UNPACKDIR}/mb-board.txt > ${D}/${FIRMWARE_DIR}/MB/HBI0262C/board.txt

    install -d ${D}/${FIRMWARE_DIR}/MB/HBI0262D/
    cp -f ${UNPACKDIR}/mbb_v151.ebf ${D}/${FIRMWARE_DIR}/MB/HBI0262D/
    cp -f ${UNPACKDIR}/io_b118.bit ${D}/${FIRMWARE_DIR}/MB/HBI0262D/
    cp -f ${UNPACKDIR}/tapid.arm  ${D}/${FIRMWARE_DIR}/MB/HBI0262D/
    cp -f ${UNPACKDIR}/pms_v105.bin ${D}/${FIRMWARE_DIR}/MB/HBI0262D/
    sed 's/pms_v103/pms_v105/g' ${UNPACKDIR}/mb-board.txt > ${D}/${FIRMWARE_DIR}/MB/HBI0262D/board.txt

    # The SITE1 image files are mostly the same except for the DTB they reference
    install -d ${D}/${FIRMWARE_DIR}/SITE1/HBI0262B/
    cp -f ${UNPACKDIR}/site1-HBI0262B-board.txt ${D}/${FIRMWARE_DIR}/SITE1/HBI0262B/board.txt
    cp -f ${UNPACKDIR}/site1-images.txt ${D}/${FIRMWARE_DIR}/SITE1/HBI0262B/images.txt

    install -d ${D}/${FIRMWARE_DIR}/SITE1/HBI0262C/
    cp -f ${UNPACKDIR}/site1-HBI0262C-board.txt ${D}/${FIRMWARE_DIR}/SITE1/HBI0262C/board.txt
    sed 's/juno\.dtb/juno-r1.dtb/g' ${UNPACKDIR}/site1-images.txt > ${D}/${FIRMWARE_DIR}/SITE1/HBI0262C/images.txt

    install -d ${D}/${FIRMWARE_DIR}/SITE1/HBI0262D/
    cp -f ${UNPACKDIR}/site1-HBI0262D-board.txt ${D}/${FIRMWARE_DIR}/SITE1/HBI0262D/board.txt
    sed 's/juno\.dtb/juno-r2.dtb/g' ${UNPACKDIR}/site1-images.txt > ${D}/${FIRMWARE_DIR}/SITE1/HBI0262D/images.txt

    # The files in SOFTWARE are listed in the SITE1 board.txt file by name
    install -d ${D}/${FIRMWARE_DIR}/SOFTWARE/
    cp -f ${RECIPE_SYSROOT}/firmware/trusted-firmware-a/fip.bin \
        ${D}/${FIRMWARE_DIR}/SOFTWARE/fip.bin

    cp -f ${RECIPE_SYSROOT}/firmware/trusted-firmware-a/bl1.bin \
        ${D}/${FIRMWARE_DIR}/SOFTWARE/bl1.bin

    cp -f ${UNPACKDIR}/hdlcdclk.dat ${D}/${FIRMWARE_DIR}/SOFTWARE/

    cp -f ${RECIPE_SYSROOT}/firmware/scp-firmware/scp_romfw_bypass.bin \
        ${D}/${FIRMWARE_DIR}/SOFTWARE/scp_bl1.bin

    cp -f ${UNPACKDIR}/startup.nsh ${D}/${FIRMWARE_DIR}/SOFTWARE/
    cp -f ${UNPACKDIR}/selftest ${D}/${FIRMWARE_DIR}/SOFTWARE/
    dd if=/dev/zero of="${D}/${FIRMWARE_DIR}/SOFTWARE/blank.img" bs=1024 count=192 status=none

    # u-boot environment file
    cp -f ${UNPACKDIR}/uEnv.txt ${D}/${FIRMWARE_DIR}/SOFTWARE/
}

do_deploy() {
    # To avoid dependency loop between firmware-image-juno:do_install
    # and virtual/kernel:do_deploy when INITRAMFS_IMAGE_BUNDLE = "1",
    # we need to handle the kernel binaries copying in the do_deploy
    # task.
    for f in ${KERNEL_DEVICETREE}; do
        install -m 755 -c ${DEPLOY_DIR_IMAGE}/$(basename $f) \
            ${D}/${FIRMWARE_DIR}/SOFTWARE/
    done

    if [ "${INITRAMFS_IMAGE_BUNDLE}" -eq 1 ]; then
        cp -L -f ${DEPLOY_DIR_IMAGE}/Image.gz-initramfs-juno.bin \
            ${D}/${FIRMWARE_DIR}/SOFTWARE/Image
    else
        cp -L -f ${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE} ${D}/${FIRMWARE_DIR}/SOFTWARE/
    fi

    # Compress the files
    tar -C ${D}/${FIRMWARE_DIR} -zcvf ${WORKDIR}/${PN}.tar.gz ./

    # Deploy the compressed archive to the deploy folder
    install -D -p -m0644 ${WORKDIR}/${PN}.tar.gz ${DEPLOYDIR}/${PN}.tar.gz
}
do_deploy[depends] += "virtual/kernel:do_deploy"
addtask deploy after do_install
