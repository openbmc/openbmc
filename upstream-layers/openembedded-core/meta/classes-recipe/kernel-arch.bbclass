#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

# Set TARGET_??_KERNEL_ARCH in the machine .conf to set architecture
# specific options necessary for building the kernel and modules.
TARGET_CC_KERNEL_ARCH ?= ""
TARGET_LD_KERNEL_ARCH ?= ""
TARGET_AR_KERNEL_ARCH ?= ""
TARGET_OBJCOPY_KERNEL_ARCH ?= ""
KERNEL_MODULE_INSTALL_PREFIX ?= "${nonarch_base_libdir}/modules/${KERNEL_VERSION}"

KERNEL_CC:toolchain-gcc = "${CCACHE}${HOST_PREFIX}gcc ${TARGET_CC_KERNEL_ARCH} \
 -fuse-ld=bfd ${DEBUG_PREFIX_MAP} \
 -ffile-prefix-map=${STAGING_KERNEL_DIR}=${KERNEL_SRC_PATH} \
 -ffile-prefix-map=${STAGING_KERNEL_BUILDDIR}=${KERNEL_SRC_PATH} \
"
KERNEL_LD:toolchain-gcc = "${HOST_PREFIX}ld.bfd ${TARGET_LD_KERNEL_ARCH}"
KERNEL_AR:toolchain-gcc = "${HOST_PREFIX}ar ${TARGET_AR_KERNEL_ARCH}"
KERNEL_OBJCOPY:toolchain-gcc = "${HOST_PREFIX}objcopy ${TARGET_OBJCOPY_KERNEL_ARCH}"
# Code in package.py can't handle options on KERNEL_STRIP
KERNEL_STRIP:toolchain-gcc = "${HOST_PREFIX}strip"


KERNEL_CC:toolchain-clang = "${CCACHE}clang ${TARGET_CC_KERNEL_ARCH} \
 ${DEBUG_PREFIX_MAP} \
 -ffile-prefix-map=${STAGING_KERNEL_DIR}=${KERNEL_SRC_PATH} \
 -ffile-prefix-map=${STAGING_KERNEL_BUILDDIR}=${KERNEL_SRC_PATH} \
"
KERNEL_LD:toolchain-clang = "${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-lld', 'ld.lld', '${HOST_PREFIX}ld.bfd', d)} ${TARGET_LD_KERNEL_ARCH}"
KERNEL_AR:toolchain-clang = "llvm-ar ${TARGET_AR_KERNEL_ARCH}"
KERNEL_OBJCOPY:toolchain-clang = "llvm-objcopy ${TARGET_OBJCOPY_KERNEL_ARCH}"
KERNEL_STRIP:toolchain-clang = "llvm-strip"
KERNEL_TOOLCHAIN ?= "gcc"
TOOLCHAIN = "${KERNEL_TOOLCHAIN}"

export KBUILD_BUILD_VERSION = "1"
export KBUILD_BUILD_USER ?= "oe-user"
export KBUILD_BUILD_HOST ?= "oe-host"
