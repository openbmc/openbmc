#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

import os
import re

# Return a value for the ARCH environment variable for kernel compilation (including
# modules). return value must match one of the architecture directories
# in the kernel source "arch" directory
def map_kernel_arch(d):
    a = d.getVar('TARGET_ARCH')

    valid_archs = ["alpha", "cris", "ia64", "i386", "x86", "m68knommu", "m68k", "ppc", "powerpc",
                   "powerpc64", "ppc64", "sparc", "sparc64", "arm", "aarch64", "m32r", "mips",
                   "sh", "sh64", "um", "h8300", "parisc", "s390", "v850", "avr32", "blackfin",
                   "loongarch64", "microblaze", "nios2", "arc", "riscv", "xtensa"]

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

# Return a value for the -A parameter to u-boot's mkimage
# This would be the value from the table in boot/image.c:uimage_arch
def map_uboot_arch(d):
    return map_kernel_arch(d)

# parse kernel ABI version out of <linux/version.h>
def get_version_headers(p):
    fn = p + '/include/linux/utsrelease.h'
    if not os.path.isfile(fn):
        # after 2.6.33-rc1
        fn = p + '/include/generated/utsrelease.h'
    if not os.path.isfile(fn):
        fn = p + '/include/linux/version.h'

    try:
        f = open(fn, 'r')
    except IOError:
        return None

    l = f.readlines()
    f.close()
    r = re.compile("#define UTS_RELEASE \"(.*)\"")
    for s in l:
        m = r.match(s)
        if m:
            return m.group(1)
    return None


def get_version_file(p):
    fn = p + '/kernel-abiversion'

    try:
        with open(fn, 'r') as f:
            return f.readlines()[0].strip()
    except IOError:
        return None

def get_localversion_file(p):
    fn = p + '/kernel-localversion'

    try:
        with open(fn, 'r') as f:
            return f.readlines()[0].strip()
    except IOError:
        return ""

    return ""
