#
# SPDX-License-Identifier: GPL-2.0-only
#

def machine_dict(d):
#           TARGET_OS  TARGET_ARCH   MACHINE, OSABI, ABIVERSION, Little Endian, 32bit?
    machdata = {
            "darwin9" : { 
                        "arm" :       (40,     0,    0,          True,          32),
                      },
            "eabi" : {
                        "arm" :       (40,     0,    0,          True,          32),
                      },
            "elf" : {
                        "aarch64" :   (183,    0,    0,          True,          64),
                        "aarch64_be" :(183,    0,    0,          False,         64),
                        "i586" :      (3,      0,    0,          True,          32),
                        "i686" :      (3,      0,    0,          True,          32),
                        "x86_64":     (62,     0,    0,          True,          64),
                        "epiphany":   (4643,   0,    0,          True,          32),
                        "lm32":       (138,    0,    0,          False,         32),
                        "mips":       ( 8,     0,    0,          False,         32),
                        "mipsel":     ( 8,     0,    0,          True,          32),
                        "microblaze":  (189,   0,    0,          False,         32),
                        "microblazeel":(189,   0,    0,          True,          32),
                        "powerpc":    (20,     0,    0,          False,         32),
                        "riscv32":    (243,    0,    0,          True,          32),
                        "riscv64":    (243,    0,    0,          True,          64),
                      },
            "linux" : { 
                        "aarch64" :   (183,    0,    0,          True,          64),
                        "aarch64_be" :(183,    0,    0,          False,         64),
                        "arm" :       (40,    97,    0,          True,          32),
                        "armeb":      (40,    97,    0,          False,         32),
                        "powerpc":    (20,     0,    0,          False,         32),
                        "powerpc64":  (21,     0,    0,          False,         64),
                        "powerpc64le":  (21,     0,    0,          True,         64),
                        "i386":       ( 3,     0,    0,          True,          32),
                        "i486":       ( 3,     0,    0,          True,          32),
                        "i586":       ( 3,     0,    0,          True,          32),
                        "i686":       ( 3,     0,    0,          True,          32),
                        "x86_64":     (62,     0,    0,          True,          64),
                        "ia64":       (50,     0,    0,          True,          64),
                        "alpha":      (36902,  0,    0,          True,          64),
                        "hppa":       (15,     3,    0,          False,         32),
                        "m68k":       ( 4,     0,    0,          False,         32),
                        "mips":       ( 8,     0,    0,          False,         32),
                        "mipsel":     ( 8,     0,    0,          True,          32),
                        "mips64":     ( 8,     0,    0,          False,         64),
                        "mips64el":   ( 8,     0,    0,          True,          64),
                        "mipsisa32r6":   ( 8,  0,    0,          False,         32),
                        "mipsisa32r6el": ( 8,  0,    0,          True,          32),
                        "mipsisa64r6":   ( 8,  0,    0,          False,         64),
                        "mipsisa64r6el": ( 8,  0,    0,          True,          64),
                        "nios2":      (113,    0,    0,          True,          32),
                        "riscv32":    (243,    0,    0,          True,          32),
                        "riscv64":    (243,    0,    0,          True,          64),
                        "s390":       (22,     0,    0,          False,         32),
                        "sh4":        (42,     0,    0,          True,          32),
                        "sparc":      ( 2,     0,    0,          False,         32),
                        "microblaze":  (189,   0,    0,          False,         32),
                        "microblazeel":(189,   0,    0,          True,          32),
                      },
            "linux-android" : {
                        "aarch64" :   (183,    0,    0,          True,          64),
                        "i686":       ( 3,     0,    0,          True,          32),
                        "x86_64":     (62,     0,    0,          True,          64),
                      },
            "linux-androideabi" : {
                        "arm" :       (40,    97,    0,          True,          32),
                      },
            "linux-musl" : { 
                        "aarch64" :   (183,    0,    0,            True,          64),
                        "aarch64_be" :(183,    0,    0,            False,         64),
                        "arm" :       (  40,    97,    0,          True,          32),
                        "armeb":      (  40,    97,    0,          False,         32),
                        "powerpc":    (  20,     0,    0,          False,         32),
                        "powerpc64":  (  21,     0,    0,          False,         64),
                        "powerpc64le":  (21,     0,    0,          True,         64),
                        "i386":       (   3,     0,    0,          True,          32),
                        "i486":       (   3,     0,    0,          True,          32),
                        "i586":       (   3,     0,    0,          True,          32),
                        "i686":       (   3,     0,    0,          True,          32),
                        "x86_64":     (  62,     0,    0,          True,          64),
                        "mips":       (   8,     0,    0,          False,         32),
                        "mipsel":     (   8,     0,    0,          True,          32),
                        "mips64":     (   8,     0,    0,          False,         64),
                        "mips64el":   (   8,     0,    0,          True,          64),
                        "microblaze":  (189,     0,    0,          False,         32),
                        "microblazeel":(189,     0,    0,          True,          32),
                        "riscv32":    (243,      0,    0,          True,          32),
                        "riscv64":    (243,      0,    0,          True,          64),
                        "sh4":        (  42,     0,    0,          True,          32),
                      },
            "uclinux-uclibc" : {
                        "bfin":       ( 106,     0,    0,          True,         32),
                      }, 
            "linux-gnueabi" : {
                        "arm" :       (40,     0,    0,          True,          32),
                        "armeb" :     (40,     0,    0,          False,         32),
                      },
            "linux-musleabi" : {
                        "arm" :       (40,     0,    0,          True,          32),
                        "armeb" :     (40,     0,    0,          False,         32),
                      },
            "linux-gnuspe" : {
                        "powerpc":    (20,     0,    0,          False,         32),
                      },
            "linux-muslspe" : {
                        "powerpc":    (20,     0,    0,          False,         32),
                      },
            "linux-gnu" :       {
                        "powerpc":    (20,     0,    0,          False,         32),
                        "sh4":        (42,     0,    0,          True,          32),
                      },
            "linux-gnu_ilp32" :     {
                        "aarch64" :   (183,    0,    0,          True,          32),
                      },
            "linux-gnux32" :       {
                        "x86_64":     (62,     0,    0,          True,          32),
                      },
            "linux-muslx32" :       {
                        "x86_64":     (62,     0,    0,          True,          32),
                      },
            "linux-gnun32" :       {
                        "mips64":       ( 8,     0,    0,          False,         32),
                        "mips64el":     ( 8,     0,    0,          True,          32),
                        "mipsisa64r6":  ( 8,     0,    0,          False,         32),
                        "mipsisa64r6el":( 8,     0,    0,          True,          32),
                      },
        }

    # Add in any extra user supplied data which may come from a BSP layer, removing the
    # need to always change this class directly
    extra_machdata = (d and d.getVar("PACKAGEQA_EXTRA_MACHDEFFUNCS" or None) or "").split()
    for m in extra_machdata:
        call = m + "(machdata, d)"
        locs = { "machdata" : machdata, "d" : d}
        machdata = bb.utils.better_eval(call, locs)

    return machdata
