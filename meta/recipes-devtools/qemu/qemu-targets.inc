# possible arch values are arm aarch64 mips mipsel mips64 mips64el ppc ppc64 ppc64abi32
# ppcemb armeb alpha sparc32plus i386 x86_64 cris m68k microblaze sparc sparc32
# sparc32plus

def get_qemu_target_list(d):
    import bb
    archs = d.getVar('QEMU_TARGETS', True).split()
    tos = d.getVar('HOST_OS', True)
    softmmuonly = ""
    for arch in ['mips64', 'mips64el', 'ppcemb']:
        if arch in archs:
            softmmuonly += arch + "-softmmu,"
            archs.remove(arch)
    linuxuseronly = ""
    for arch in ['armeb', 'alpha', 'ppc64abi32', 'sparc32plus']:
        if arch in archs:
            linuxuseronly += arch + "-linux-user,"
            archs.remove(arch)
    if 'linux' not in tos:
        return softmmuonly + ''.join([arch + "-softmmu" + "," for arch in archs]).rstrip(',')
    return softmmuonly + linuxuseronly + ''.join([arch + "-linux-user" + "," + arch + "-softmmu" + "," for arch in archs]).rstrip(',')

