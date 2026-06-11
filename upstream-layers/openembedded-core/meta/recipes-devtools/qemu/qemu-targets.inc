# possible arch values are:
#    aarch64 arm armeb alpha cris i386 x86_64 m68k microblaze
#    mips mipsel mips64 mips64el ppc ppc64 ppc64abi32 ppcemb
#    riscv32 riscv64 sparc sparc32 sparc32plus

def get_qemu_target_list(d):
    import bb
    archs = d.getVar('QEMU_TARGETS').split()
    tos = d.getVar('HOST_OS')
    tarch = d.getVar('HOST_ARCH')
    # 32 bit hosts can't handle 64 bit targets
    if "64" not in tarch:
        for arch in archs.copy():
            if "64" in arch:
                archs.remove(arch)
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
    # QEMU 11.0.0+ only supports linux-user on 64-bit host architectures
    linux_user_hosts = ['aarch64', 'loongarch64', 'mips64', 'ppc64', 'riscv64', 's390x', 'sparc64', 'x86_64']
    if tarch not in linux_user_hosts:
        return softmmuonly + ''.join([arch + "-softmmu" + "," for arch in archs]).rstrip(',')
    return softmmuonly + linuxuseronly + ''.join([arch + "-linux-user" + "," + arch + "-softmmu" + "," for arch in archs]).rstrip(',')

def get_qemu_usermode_target_list(d):
    return ",".join(filter(lambda i: "-linux-user" in i, get_qemu_target_list(d).split(',')))

def get_qemu_system_target_list(d):
    return ",".join(filter(lambda i: "-linux-user" not in i, get_qemu_target_list(d).split(',')))
