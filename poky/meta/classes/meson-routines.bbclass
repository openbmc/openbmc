inherit siteinfo

def meson_array(var, d):
    items = d.getVar(var).split()
    return repr(items[0] if len(items) == 1 else items)

# Map our ARCH values to what Meson expects:
# http://mesonbuild.com/Reference-tables.html#cpu-families
def meson_cpu_family(var, d):
    import re
    arch = d.getVar(var)
    if arch == 'powerpc':
        return 'ppc'
    elif arch == 'powerpc64' or arch == 'powerpc64le':
        return 'ppc64'
    elif arch == 'armeb':
        return 'arm'
    elif arch == 'aarch64_be':
        return 'aarch64'
    elif arch == 'mipsel':
        return 'mips'
    elif arch == 'mips64el':
        return 'mips64'
    elif re.match(r"i[3-6]86", arch):
        return "x86"
    elif arch == "microblazeel":
        return "microblaze"
    else:
        return arch

# Map our OS values to what Meson expects:
# https://mesonbuild.com/Reference-tables.html#operating-system-names
def meson_operating_system(var, d):
    os = d.getVar(var)
    if "mingw" in os:
        return "windows"
    # avoid e.g 'linux-gnueabi'
    elif "linux" in os:
        return "linux"
    else:
        return os

def meson_endian(prefix, d):
    arch, os = d.getVar(prefix + "_ARCH"), d.getVar(prefix + "_OS")
    sitedata = siteinfo_data_for_machine(arch, os, d)
    if "endian-little" in sitedata:
        return "little"
    elif "endian-big" in sitedata:
        return "big"
    else:
        bb.fatal("Cannot determine endianism for %s-%s" % (arch, os))
