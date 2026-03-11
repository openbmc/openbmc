SUMMARY = "The Linux kernel as a FIT image (optionally with initramfs)"
SECTION = "kernel"

# If an initramfs is included in the FIT image more licenses apply.
# But also the kernel uses more than one license (see Documentation/process/license-rules.rst)
LICENSE = "GPL-2.0-with-Linux-syscall-note"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/GPL-2.0-with-Linux-syscall-note;md5=0bad96c422c41c3a94009dcfe1bff992"

inherit linux-kernel-base kernel-fit-image

# Set the version of this recipe to the version of the included kernel
# (without taking the long way around via PV)
PKGV = "${@get_kernelversion_file("${STAGING_KERNEL_BUILDDIR}")}"
