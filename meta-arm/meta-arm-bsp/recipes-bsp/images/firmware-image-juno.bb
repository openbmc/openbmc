DESCRIPTION = "Firmware Image for Juno to be copied to the Configuration \
microSD card"

LICENSE = "BSD-3-Clause"
SECTION = "firmware"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9"

INHIBIT_DEFAULT_DEPS = "1"
DEPENDS = "trusted-firmware-a virtual/kernel virtual/control-processor-firmware"

PACKAGE_ARCH = "${MACHINE_ARCH}"

COMPATIBLE_MACHINE = "juno"

LINARO_RELEASE = "19.06"

SRC_URI = "http://releases.linaro.org/members/arm/platforms/${LINARO_RELEASE}/juno-latest-oe-uboot.zip;subdir=${S} \
    file://images-r0.txt \
    file://images-r1.txt \
    file://images-r2.txt \
    file://uEnv.txt \
"
SRC_URI[md5sum] = "01b662b81fa409d55ff298238ad24003"
SRC_URI[sha256sum] = "b8a3909bb3bc4350a8771b863193a3e33b358e2a727624a77c9ecf13516cec82"

FIRMWARE_DIR = "juno-firmware-${LINARO_RELEASE}"
S = "${UNPACKDIR}/${FIRMWARE_DIR}"

inherit deploy nopackages

do_configure[noexec] = "1"
do_compile[noexec] = "1"

# The ${D} is used as a temporary directory and we don't generate any
# packages for this recipe.
do_install() {
    cp -a ${S} ${D}/
    cp -f ${RECIPE_SYSROOT}/firmware/bl1-juno.bin \
        ${D}/${FIRMWARE_DIR}/SOFTWARE/bl1.bin

    cp -f ${RECIPE_SYSROOT}/firmware/fip-juno.bin \
        ${D}/${FIRMWARE_DIR}/SOFTWARE/fip.bin

    cp -f ${RECIPE_SYSROOT}/firmware/scp_romfw_bypass.bin \
        ${D}/${FIRMWARE_DIR}/SOFTWARE/scp_bl1.bin

    # u-boot environment file
    cp -f ${UNPACKDIR}/uEnv.txt ${D}/${FIRMWARE_DIR}/SOFTWARE/

    # Juno images list file
    cp -f ${UNPACKDIR}/images-r0.txt ${D}/${FIRMWARE_DIR}/SITE1/HBI0262B/images.txt
    cp -f ${UNPACKDIR}/images-r1.txt ${D}/${FIRMWARE_DIR}/SITE1/HBI0262C/images.txt
    cp -f ${UNPACKDIR}/images-r2.txt ${D}/${FIRMWARE_DIR}/SITE1/HBI0262D/images.txt
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
