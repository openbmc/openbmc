# possible arch values are:
#    aarch64 arm armeb alpha cris i386 x86_64 m68k microblaze
#    mips mipsel mips64 mips64el ppc ppc64 ppc64abi32 ppcemb
#    riscv32 riscv64 sparc sparc32 sparc32plus

def get_qemu_target_list(d):
    import bb
    archs = d.getVar('QEMU_TARGETS').split()
    tos = d.getVar('HOST_OS')
    softmmuonly = ""
    for arch in ['ppcemb', 'lm32']:
        if arch in archs:
            softmmuonly += arch + "-softmmu,"
            archs.remove(arch)
    linuxuseronly = ""
    for arch in ['armeb', 'alpha', 'ppc64abi32', 'ppc64le', 'sparc32plus', 'aarch64_be']:
        if arch in archs:
            linuxuseronly += arch + "-linux-user,"
            archs.remove(arch)
    if 'linux' not in tos:
        return softmmuonly + ''.join([arch + "-softmmu" + "," for arch in archs]).rstrip(',')
    return softmmuonly + linuxuseronly + ''.join([arch + "-linux-user" + "," + arch + "-softmmu" + "," for arch in archs]).rstrip(',')

def get_qemu_usermode_target_list(d):
    return ",".join(filter(lambda i: "-linux-user" in i, get_qemu_target_list(d).split(',')))

def get_qemu_system_target_list(d):
    return ",".join(filter(lambda i: "-linux-user" not in i, get_qemu_target_list(d).split(',')))
