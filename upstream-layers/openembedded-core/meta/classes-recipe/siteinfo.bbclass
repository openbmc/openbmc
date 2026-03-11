#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

# This class exists to provide information about the targets that
# may be needed by other classes and/or recipes. If you add a new
# target this will probably need to be updated.

#
# Returns information about 'what' for the named target 'target'
# where 'target' == "<arch>-<os>"
#
# 'what' can be one of
# * target: Returns the target name ("<arch>-<os>")
# * endianness: Return "be" for big endian targets, "le" for little endian
# * bits: Returns the bit size of the target, either "32" or "64"
# * libc: Returns the name of the c library used by the target
#
# It is an error for the target not to exist.
# If 'what' doesn't exist then an empty value is returned
#
def siteinfo_data_for_machine(arch, os, d):
    archinfo = {
        "allarch": "endian-little bit-32", # bogus, but better than special-casing the checks below for allarch
        "aarch64": "endian-little bit-64 arm-common arm-64",
        "aarch64_be": "endian-big bit-64 arm-common arm-64",
        "arc": "endian-little bit-32 arc-common",
        "arceb": "endian-big bit-32 arc-common",
        "arm": "endian-little bit-32 arm-common arm-32",
        "armeb": "endian-big bit-32 arm-common arm-32",
        "avr32": "endian-big bit-32 avr32-common",
        "bfin": "endian-little bit-32 bfin-common",
        "epiphany": "endian-little bit-32",
        "i386": "endian-little bit-32 ix86-common",
        "i486": "endian-little bit-32 ix86-common",
        "i586": "endian-little bit-32 ix86-common",
        "i686": "endian-little bit-32 ix86-common",
        "ia64": "endian-little bit-64",
        "lm32": "endian-big bit-32",
        "loongarch32": "endian-little bit-32 loongarch",
        "loongarch64": "endian-little bit-64 loongarch",
        "m68k": "endian-big bit-32",
        "microblaze": "endian-big bit-32 microblaze-common",
        "microblazeel": "endian-little bit-32 microblaze-common",
        "mips": "endian-big bit-32 mips-common",
        "mips64": "endian-big bit-64 mips-common",
        "mips64el": "endian-little bit-64 mips-common",
        "mipsisa64r6": "endian-big bit-64 mips-common",
        "mipsisa64r6el": "endian-little bit-64 mips-common",
        "mipsel": "endian-little bit-32 mips-common",
        "mipsisa32r6": "endian-big bit-32 mips-common",
        "mipsisa32r6el": "endian-little bit-32 mips-common",
        "powerpc": "endian-big bit-32 powerpc-common",
        "powerpcle": "endian-little bit-32 powerpc-common",
        "nios2": "endian-little bit-32 nios2-common",
        "powerpc64": "endian-big bit-64 powerpc-common",
        "powerpc64le": "endian-little bit-64 powerpc-common",
        "ppc": "endian-big bit-32 powerpc-common",
        "ppc64": "endian-big bit-64 powerpc-common",
        "ppc64le" : "endian-little bit-64 powerpc-common",
        "riscv32": "endian-little bit-32 riscv-common",
        "riscv64": "endian-little bit-64 riscv-common",
        "sh3": "endian-little bit-32 sh-common",
        "sh3eb": "endian-big bit-32 sh-common",
        "sh4": "endian-little bit-32 sh-common",
        "sh4eb": "endian-big bit-32 sh-common",
        "sparc": "endian-big bit-32",
        "viac3": "endian-little bit-32 ix86-common",
        "x86_64": "endian-little", # bitinfo specified in targetinfo
    }
    osinfo = {
        "darwin": "common-darwin",
        "darwin9": "common-darwin",
        "darwin19": "common-darwin",
        "darwin21": "common-darwin",
        "linux": "common-linux common-glibc",
        "linux-gnu": "common-linux common-glibc",
        "linux-gnu_ilp32": "common-linux common-glibc",
        "linux-gnux32": "common-linux common-glibc",
        "linux-gnun32": "common-linux common-glibc",
        "linux-gnueabi": "common-linux common-glibc",
        "linux-gnuspe": "common-linux common-glibc",
        "linux-musl": "common-linux common-musl",
        "linux-muslx32": "common-linux common-musl",
        "linux-musleabi": "common-linux common-musl",
        "linux-muslspe": "common-linux common-musl",
        "uclinux-uclibc": "common-uclibc",
        "cygwin": "common-cygwin",
        "mingw32": "common-mingw",
    }
    targetinfo = {
        "aarch64-linux-gnu": "aarch64-linux",
        "aarch64_be-linux-gnu": "aarch64_be-linux",
        "aarch64-linux-gnu_ilp32": "bit-32 aarch64_be-linux arm-32",
        "aarch64_be-linux-gnu_ilp32": "bit-32 aarch64_be-linux arm-32",
        "aarch64-linux-musl": "aarch64-linux",
        "aarch64_be-linux-musl": "aarch64_be-linux",
        "arm-linux-gnueabi": "arm-linux",
        "arm-linux-musleabi": "arm-linux",
        "armeb-linux-gnueabi": "armeb-linux",
        "armeb-linux-musleabi": "armeb-linux",
        "loongarch32-linux": "loongarch32-linux",
        "loongarch64-linux": "loongarch64-linux",
        "microblazeel-linux" : "microblaze-linux",
        "microblazeel-linux-musl" : "microblaze-linux",
        "mips-linux-musl": "mips-linux",
        "mipsel-linux-musl": "mipsel-linux",
        "mips64-linux-musl": "mips64-linux",
        "mips64el-linux-musl": "mips64el-linux",
        "mips64-linux-gnun32": "mips-linux bit-32",
        "mips64el-linux-gnun32": "mipsel-linux bit-32",
        "mipsisa64r6-linux-gnun32": "mipsisa32r6-linux bit-32",
        "mipsisa64r6el-linux-gnun32": "mipsisa32r6el-linux bit-32",
        "powerpc-linux": "powerpc32-linux powerpc32-linux-glibc",
        "powerpc-linux-musl": "powerpc-linux powerpc32-linux powerpc32-linux-musl",
        "powerpcle-linux": "powerpc32-linux powerpc32-linux-glibc",
        "powerpcle-linux-musl": "powerpc-linux powerpc32-linux powerpc32-linux-musl",
        "powerpc-linux-gnuspe": "powerpc-linux powerpc32-linux powerpc32-linux-glibc",
        "powerpc-linux-muslspe": "powerpc-linux powerpc32-linux powerpc32-linux-musl",
        "powerpc64-linux-gnuspe": "powerpc-linux powerpc64-linux powerpc64-linux-glibc",
        "powerpc64-linux-muslspe": "powerpc-linux powerpc64-linux powerpc64-linux-musl",
        "powerpc64-linux": "powerpc-linux powerpc64-linux powerpc64-linux-glibc",
        "powerpc64-linux-musl": "powerpc-linux powerpc64-linux powerpc64-linux-musl",
        "powerpc64le-linux": "powerpc-linux powerpc64-linux powerpc64-linux-glibc",
        "powerpc64le-linux-musl": "powerpc-linux powerpc64-linux powerpc64-linux-musl",
        "riscv32-linux": "riscv32-linux",
        "riscv32-linux-musl": "riscv32-linux",
        "riscv64-linux": "riscv64-linux",
        "riscv64-linux-musl": "riscv64-linux",
        "x86_64-cygwin": "bit-64",
        "x86_64-darwin": "bit-64",
        "x86_64-darwin9": "bit-64",
        "x86_64-darwin19": "bit-64",
        "x86_64-darwin21": "bit-64",
        "x86_64-linux": "bit-64",
        "x86_64-linux-musl": "x86_64-linux bit-64",
        "x86_64-linux-muslx32": "bit-32 ix86-common x32-linux",
        "x86_64-elf": "bit-64",
        "x86_64-linux-gnu": "bit-64 x86_64-linux",
        "x86_64-linux-gnux32": "bit-32 ix86-common x32-linux",
        "x86_64-mingw32": "bit-64",
    }

    # Add in any extra user supplied data which may come from a BSP layer, removing the
    # need to always change this class directly
    extra_siteinfo = (d.getVar("SITEINFO_EXTRA_DATAFUNCS") or "").split()
    for m in extra_siteinfo:
        call = m + "(archinfo, osinfo, targetinfo, d)"
        locs = { "archinfo" : archinfo, "osinfo" : osinfo, "targetinfo" : targetinfo, "d" : d}
        archinfo, osinfo, targetinfo = bb.utils.better_eval(call, locs)

    target = "%s-%s" % (arch, os)

    sitedata = []
    if arch in archinfo:
        sitedata.extend(archinfo[arch].split())
    if os in osinfo:
        sitedata.extend(osinfo[os].split())
    if target in targetinfo:
        sitedata.extend(targetinfo[target].split())
    sitedata.append(target)
    sitedata.append("common")

    bb.debug(1, "SITE files %s" % sitedata);
    return sitedata

def siteinfo_data(d):
    return siteinfo_data_for_machine(d.getVar("HOST_ARCH"), d.getVar("HOST_OS"), d)

python () {
    sitedata = set(siteinfo_data(d))
    if "endian-little" in sitedata:
        d.setVar("SITEINFO_ENDIANNESS", "le")
    elif "endian-big" in sitedata:
        d.setVar("SITEINFO_ENDIANNESS", "be")
    else:
        bb.error("Unable to determine endianness for architecture '%s'" %
                 d.getVar("HOST_ARCH"))
        bb.fatal("Please add your architecture to siteinfo.bbclass")

    if "bit-32" in sitedata:
        d.setVar("SITEINFO_BITS", "32")
    elif "bit-64" in sitedata:
        d.setVar("SITEINFO_BITS", "64")
    else:
        bb.error("Unable to determine bit size for architecture '%s'" %
                 d.getVar("HOST_ARCH"))
        bb.fatal("Please add your architecture to siteinfo.bbclass")
}

# Layers with siteconfig need to add a replacement path to this variable so the
# sstate isn't path specific
SITEINFO_PATHVARS = "COREBASE"

def siteinfo_get_files(d, sysrootcache=False):
    sitedata = siteinfo_data(d)
    sitefiles = []
    searched = []
    for path in d.getVar("BBPATH").split(":"):
        for element in sitedata:
            filename = os.path.join(path, "site", element)
            if os.path.exists(filename):
                searched.append(filename + ":True")
                sitefiles.append(filename)
            else:
                searched.append(filename + ":False")

    # Have to parameterise out hardcoded paths such as COREBASE for the main site files
    for var in d.getVar("SITEINFO_PATHVARS").split():
        searched2 = []
        replace = os.path.normpath(d.getVar(var))
        for s in searched:
            searched2.append(s.replace(replace, "${" + var + "}"))
        searched = searched2

    if bb.data.inherits_class('native', d) or bb.data.inherits_class('cross', d) or bb.data.inherits_class('crosssdk', d):
        # We need sstate sigs for native/cross not to vary upon arch so we can't depend on the site files.
        # In future we may want to depend upon all site files?
        # This would show up as breaking sstatetests.SStateTests.test_sstate_32_64_same_hash for example
        searched = []

    return sitefiles, searched


