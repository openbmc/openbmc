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
def siteinfo_data(d):
    archinfo = {
        "allarch": "endian-little bit-32", # bogus, but better than special-casing the checks below for allarch
        "aarch64": "endian-little bit-64 arm-common arm-64",
        "aarch64_be": "endian-big bit-64 arm-common arm-64",
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
        "microblaze": "endian-big bit-32 microblaze-common",
        "microblazeeb": "endian-big bit-32 microblaze-common",
        "microblazeel": "endian-little bit-32 microblaze-common",
        "mips": "endian-big bit-32 mips-common",
        "mips64": "endian-big bit-64 mips-common",
        "mips64el": "endian-little bit-64 mips-common",
        "mipsel": "endian-little bit-32 mips-common",
        "powerpc": "endian-big bit-32 powerpc-common",
        "nios2": "endian-little bit-32 nios2-common",
        "powerpc64": "endian-big bit-64 powerpc-common",
        "ppc": "endian-big bit-32 powerpc-common",
        "ppc64": "endian-big bit-64 powerpc-common",
        "ppc64le" : "endian-little bit-64 powerpc-common",
        "sh3": "endian-little bit-32 sh-common",
        "sh4": "endian-little bit-32 sh-common",
        "sparc": "endian-big bit-32",
        "viac3": "endian-little bit-32 ix86-common",
        "x86_64": "endian-little", # bitinfo specified in targetinfo
    }
    osinfo = {
        "darwin": "common-darwin",
        "darwin9": "common-darwin",
        "linux": "common-linux common-glibc",
        "linux-gnu": "common-linux common-glibc",
        "linux-gnux32": "common-linux common-glibc",
        "linux-gnun32": "common-linux common-glibc",
        "linux-gnueabi": "common-linux common-glibc",
        "linux-gnuspe": "common-linux common-glibc",
        "linux-uclibc": "common-linux common-uclibc",
        "linux-uclibceabi": "common-linux common-uclibc",
        "linux-uclibcspe": "common-linux common-uclibc",
        "linux-musl": "common-linux common-musl",
        "linux-musleabi": "common-linux common-musl",
        "linux-muslspe": "common-linux common-musl",
        "uclinux-uclibc": "common-uclibc",
        "cygwin": "common-cygwin",
        "mingw32": "common-mingw",
    }
    targetinfo = {
        "aarch64-linux-gnu": "aarch64-linux",
        "aarch64_be-linux-gnu": "aarch64_be-linux",
        "aarch64-linux-musl": "aarch64-linux",
        "aarch64_be-linux-musl": "aarch64_be-linux",
        "arm-linux-gnueabi": "arm-linux",
        "arm-linux-musleabi": "arm-linux",
        "arm-linux-uclibceabi": "arm-linux-uclibc",
        "armeb-linux-gnueabi": "armeb-linux",
        "armeb-linux-uclibceabi": "armeb-linux-uclibc",
        "armeb-linux-musleabi": "armeb-linux",
        "mips-linux-musl": "mips-linux",
        "mipsel-linux-musl": "mipsel-linux",
        "mips64-linux-musl": "mips64-linux",
        "mips64el-linux-musl": "mips64el-linux",
        "mips64-linux-gnun32": "mips-linux bit-32",
        "mips64el-linux-gnun32": "mipsel-linux bit-32",
        "powerpc-linux": "powerpc32-linux",
        "powerpc-linux-musl": "powerpc-linux powerpc32-linux",
        "powerpc-linux-uclibc": "powerpc-linux powerpc32-linux",
        "powerpc-linux-gnuspe": "powerpc-linux powerpc32-linux",
        "powerpc-linux-muslspe": "powerpc-linux powerpc32-linux",
        "powerpc-linux-uclibcspe": "powerpc-linux powerpc32-linux powerpc-linux-uclibc",
        "powerpc64-linux-gnuspe": "powerpc-linux powerpc64-linux",
        "powerpc64-linux-muslspe": "powerpc-linux powerpc64-linux",
        "powerpc64-linux": "powerpc-linux",
        "powerpc64-linux-musl": "powerpc-linux",
        "x86_64-cygwin": "bit-64",
        "x86_64-darwin": "bit-64",
        "x86_64-darwin9": "bit-64",
        "x86_64-linux": "bit-64",
        "x86_64-linux-musl": "x86_64-linux bit-64",
        "x86_64-linux-uclibc": "bit-64",
        "x86_64-elf": "bit-64",
        "x86_64-linux-gnu": "bit-64 x86_64-linux",
        "x86_64-linux-gnux32": "bit-32 ix86-common x32-linux",
        "x86_64-mingw32": "bit-64",
    }

    hostarch = d.getVar("HOST_ARCH", True)
    hostos = d.getVar("HOST_OS", True)
    target = "%s-%s" % (hostarch, hostos)

    sitedata = []
    if hostarch in archinfo:
        sitedata.extend(archinfo[hostarch].split())
    if hostos in osinfo:
        sitedata.extend(osinfo[hostos].split())
    if target in targetinfo:
        sitedata.extend(targetinfo[target].split())
    sitedata.append(target)
    sitedata.append("common")

    bb.debug(1, "SITE files %s" % sitedata);
    return sitedata

python () {
    sitedata = set(siteinfo_data(d))
    if "endian-little" in sitedata:
        d.setVar("SITEINFO_ENDIANNESS", "le")
    elif "endian-big" in sitedata:
        d.setVar("SITEINFO_ENDIANNESS", "be")
    else:
        bb.error("Unable to determine endianness for architecture '%s'" %
                 d.getVar("HOST_ARCH", True))
        bb.fatal("Please add your architecture to siteinfo.bbclass")

    if "bit-32" in sitedata:
        d.setVar("SITEINFO_BITS", "32")
    elif "bit-64" in sitedata:
        d.setVar("SITEINFO_BITS", "64")
    else:
        bb.error("Unable to determine bit size for architecture '%s'" %
                 d.getVar("HOST_ARCH", True))
        bb.fatal("Please add your architecture to siteinfo.bbclass")
}

def siteinfo_get_files(d, no_cache = False):
    sitedata = siteinfo_data(d)
    sitefiles = ""
    for path in d.getVar("BBPATH", True).split(":"):
        for element in sitedata:
            filename = os.path.join(path, "site", element)
            if os.path.exists(filename):
                sitefiles += filename + " "

    if no_cache: return sitefiles

    # Now check for siteconfig cache files
    # Use the files copied to the aclocal cache generated by autotools.bbclass
    # to avoid races
    path_siteconfig = d.getVar('ACLOCALDIR', True)
    if path_siteconfig and os.path.isdir(path_siteconfig):
        for i in os.listdir(path_siteconfig):
            if not i.endswith("_config"):
                continue
            filename = os.path.join(path_siteconfig, i)
            sitefiles += filename + " "

    return sitefiles

#
# Make some information available via variables
#
SITECONFIG_SYSROOTCACHE = "${STAGING_DATADIR}/${TARGET_SYS}_config_site.d"
