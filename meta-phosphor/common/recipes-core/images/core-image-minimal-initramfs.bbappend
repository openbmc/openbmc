PACKAGE_INSTALL_remove = "initramfs-live-boot initramfs-live-install initramfs-live-install-efi"
PACKAGE_INSTALL += "obmc-phosphor-initfs"
INITRAMFS_CTYPE ?= "lzma"
INITRAMFS_FSTYPES = "cpio.${INITRAMFS_CTYPE}"
