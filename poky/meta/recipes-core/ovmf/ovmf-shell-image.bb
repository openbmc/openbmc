SUMMARY = "boot image with UEFI shell and tools"
COMPATIBLE_HOST:class-target = '(i.86|x86_64).*'

# For this image recipe, only the wic format with a
# single vfat partition makes sense. Because we have no
# boot loader and no rootfs partition, not additional
# tools are needed for this .wks file.
IMAGE_FSTYPES:forcevariable = 'wic'
WKS_FILE = "ovmf/ovmf-shell-image.wks"
WKS_FILE_DEPENDS = ""

inherit image

# We want a minimal image with just ovmf-shell-efi unpacked in it. We
# avoid installing unnecessary stuff as much as possible, but some
# things still get through and need to be removed.
PACKAGE_INSTALL = "ovmf-shell-efi"
LINGUAS_INSTALL = ""
do_image () {
    rm -rf `ls -d ${IMAGE_ROOTFS}/* | grep -v efi`
}
