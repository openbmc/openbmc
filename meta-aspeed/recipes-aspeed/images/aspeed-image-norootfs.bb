SUMMARY = "Two partition MTD image with u-boot and kernel"
HOMEPAGE = "https://github.com/openbmc/meta-aspeed"
LICENSE = "MIT"

inherit deploy

UBOOT_SUFFIX ?= "bin"
ASPEED_IMAGE_KERNEL_OFFSET_KB ?= "512"
ASPEED_IMAGE_SIZE_KB ?= "32768"
ASPEED_IMAGE_KERNEL_IMAGE ?= "fitImage-${INITRAMFS_IMAGE}-${MACHINE}-${MACHINE}"
ASPEED_IMAGE_NAME ?= "aspeed-norootfs-${MACHINE}.bin"

do_compile() {
    dd if=/dev/zero bs=1k count=${ASPEED_IMAGE_SIZE_KB} | \
        tr '\000' '\377' > ${B}/aspeed-norootfs.bin
    dd if=${DEPLOY_DIR_IMAGE}/u-boot.${UBOOT_SUFFIX} of=${B}/aspeed-norootfs.bin \
        conv=notrunc
    dd if=${DEPLOY_DIR_IMAGE}/${ASPEED_IMAGE_KERNEL_IMAGE} \
        of=${B}/aspeed-norootfs.bin conv=notrunc \
        seek=${ASPEED_IMAGE_KERNEL_OFFSET_KB} bs=1k
}

do_deploy() {
    install -m644 -D ${B}/aspeed-norootfs.bin ${DEPLOYDIR}/${ASPEED_IMAGE_NAME}
}

do_compile[depends] = "virtual/kernel:do_deploy u-boot:do_deploy"
do_fetch[noexec] = "1"
do_unpack[noexec] = "1"
do_patch[noexec] = "1"
do_configure[noexec] = "1"
do_install[noexec] = "1"
deltask do_populate_sysroot
do_package[noexec] = "1"
deltask do_package_qa
do_packagedata[noexec] = "1"
deltask do_package_write_ipk
deltask do_package_write_deb
deltask do_package_write_rpm
addtask deploy before do_build after do_compile
