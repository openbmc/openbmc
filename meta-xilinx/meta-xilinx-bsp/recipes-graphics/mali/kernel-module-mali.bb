SUMMARY = "A Mali 400 Linux Kernel module"
SECTION = "kernel/modules"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = " \
	file://linux/license/gpl/mali_kernel_license.h;md5=436192a2c5cfd47df0ad1987dffc4ae6 \
	"

PV = "r8p0-01rel0"

SRC_URI = " \
	https://developer.arm.com/-/media/Files/downloads/mali-drivers/kernel/mali-utgard-gpu/DX910-SW-99002-${PV}.tgz \
	file://0001-Change-Makefile-to-be-compatible-with-Yocto.patch \
	file://0002-staging-mali-r8p0-01rel0-Add-the-ZYNQ-ZYNQMP-platfor.patch \
	file://0003-staging-mali-r8p0-01rel0-Remove-unused-trace-macros.patch \
	file://0004-staging-mali-r8p0-01rel0-Don-t-include-mali_read_phy.patch \
	file://0005-linux-mali_kernel_linux.c-Handle-clock-when-probed-a.patch \
	file://0006-arm.c-global-variable-dma_ops-is-removed-from-the-ke.patch \
	file://0007-Replace-__GFP_REPEAT-by-__GFP_RETRY_MAYFAIL.patch \
	file://0008-mali_internal_sync-Rename-wait_queue_t-with-wait_que.patch \
	file://0009-mali_memory_swap_alloc.c-Rename-global_page_state-wi.patch \
	file://0010-common-mali_pm.c-Add-PM-runtime-barrier-after-removi.patch \
	file://0011-linux-mali_kernel_linux.c-Enable-disable-clock-for-r.patch\
	"
SRC_URI[md5sum] = "8f04ae86957fd56197ad5a9d017b84ff"
SRC_URI[sha256sum] = "bfd14fa3f75a71d4ba313534e651ca1c58dc354e882c0b39867e335882a06350"

inherit module

do_make_scripts[depends] += "virtual/kernel:do_unpack"

S = "${WORKDIR}/DX910-SW-99002-${PV}/driver/src/devicedrv/mali"

COMPATIBLE_MACHINE = "^$"
COMPATIBLE_MACHINE_zynqmpeg = "zynqmpeg"
COMPATIBLE_MACHINE_zynqmpev = "zynqmpev"

PACKAGE_ARCH = "${SOC_FAMILY}${SOC_VARIANT}"

EXTRA_OEMAKE = 'KDIR="${STAGING_KERNEL_DIR}" \
		ARCH="${ARCH}" \
		BUILD=release \
		MALI_PLATFORM="arm" \
		USING_DT=1 \
		MALI_SHARED_INTERRUPTS=1 \
		CROSS_COMPILE="${TARGET_PREFIX}" \
		O=${STAGING_KERNEL_BUILDDIR} \
		MALI_QUIET=1 \
		'
