
valid_xen_archs = " \
        x86_64 x86_32 \
        arm32 arm64 \
        "

def map_xen_arch(a, d):
    import re
    valid_archs = d.getVar('valid_xen_archs').split()

    if   re.match("i.86", a):        return "x86_32"
    elif re.match("x86.64", a):      return "x86_64"
    elif re.match("arm.*", a):       return "arm32"
    elif re.match("aarch64.*", a):   return "arm64"
    elif a in valid_archs:           return a
    else:
        return "INVALID"

