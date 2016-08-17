require initramfs-kexecboot-image.bb

SUMMARY = "Initramfs image for kexecboot kernel (klibc-static binaries)"

# We really need just kexecboot, kexec and ubiattach
# statically compiled against klibc
IMAGE_INSTALL = "kexecboot-klibc kexec-klibc ubiattach-klibc"
