#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

#
# set the ARCH environment variable for kernel compilation (including
# modules). return value must match one of the architecture directories
# in the kernel source "arch" directory
#

valid_archs = "alpha cris ia64 \
               i386 x86 \
               m68knommu m68k ppc powerpc powerpc64 ppc64  \
               sparc sparc64 \
               arm aarch64 \
               m32r mips \
               sh sh64 um h8300   \
               parisc s390  v850 \
               avr32 blackfin \
               loongarch64 \
               microblaze \
               nios2 arc riscv xtensa"

def map_kernel_arch(a, d):
    import re

    valid_archs = d.getVar('valid_archs').split()

    if   re.match('(i.86|athlon|x86.64)$', a):  return 'x86'
    elif re.match('arceb$', a):                 return 'arc'
    elif re.match('armeb$', a):                 return 'arm'
    elif re.match('aarch64$', a):               return 'arm64'
    elif re.match('aarch64_be$', a):            return 'arm64'
    elif re.match('aarch64_ilp32$', a):         return 'arm64'
    elif re.match('aarch64_be_ilp32$', a):      return 'arm64'
    elif re.match('loongarch(32|64|)$', a):     return 'loongarch'
    elif re.match('mips(isa|)(32|64|)(r6|)(el|)$', a):      return 'mips'
    elif re.match('mcf', a):                    return 'm68k'
    elif re.match('riscv(32|64|)(eb|)$', a):    return 'riscv'
    elif re.match('p(pc|owerpc)(|64)', a):      return 'powerpc'
    elif re.match('sh(3|4)$', a):               return 'sh'
    elif re.match('bfin', a):                   return 'blackfin'
    elif re.match('microblazee[bl]', a):        return 'microblaze'
    elif a in valid_archs:                      return a
    else:
        if not d.getVar("TARGET_OS").startswith("linux"):
            return a
        bb.error("cannot map '%s' to a linux kernel architecture" % a)

export ARCH = "${@map_kernel_arch(d.getVar('TARGET_ARCH'), d)}"

def map_uboot_arch(a, d):
    import re

    if   re.match('p(pc|owerpc)(|64)', a): return 'ppc'
    elif re.match('i.86$', a): return 'x86'
    return a

export UBOOT_ARCH = "${@map_uboot_arch(d.getVar('ARCH'), d)}"

# Set TARGET_??_KERNEL_ARCH in the machine .conf to set architecture
# specific options necessary for building the kernel and modules.
TARGET_CC_KERNEL_ARCH ?= ""
HOST_CC_KERNEL_ARCH ?= "${TARGET_CC_KERNEL_ARCH}"
TARGET_LD_KERNEL_ARCH ?= ""
HOST_LD_KERNEL_ARCH ?= "${TARGET_LD_KERNEL_ARCH}"
TARGET_AR_KERNEL_ARCH ?= ""
HOST_AR_KERNEL_ARCH ?= "${TARGET_AR_KERNEL_ARCH}"
TARGET_OBJCOPY_KERNEL_ARCH ?= ""
HOST_OBJCOPY_KERNEL_ARCH ?= "${TARGET_OBJCOPY_KERNEL_ARCH}"

KERNEL_CC = "${CCACHE}${HOST_PREFIX}gcc ${HOST_CC_KERNEL_ARCH} -fuse-ld=bfd ${DEBUG_PREFIX_MAP} -fdebug-prefix-map=${STAGING_KERNEL_DIR}=${KERNEL_SRC_PATH} -fdebug-prefix-map=${STAGING_KERNEL_BUILDDIR}=${KERNEL_SRC_PATH}"
KERNEL_LD = "${HOST_PREFIX}ld.bfd ${HOST_LD_KERNEL_ARCH}"
KERNEL_AR = "${HOST_PREFIX}ar ${HOST_AR_KERNEL_ARCH}"
KERNEL_OBJCOPY = "${HOST_PREFIX}objcopy ${HOST_OBJCOPY_KERNEL_ARCH}"
# Code in package.py can't handle options on KERNEL_STRIP
KERNEL_STRIP = "${HOST_PREFIX}strip"
TOOLCHAIN ?= "gcc"
