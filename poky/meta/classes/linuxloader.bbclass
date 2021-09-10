def get_musl_loader_arch(d):
    import re
    ldso_arch = "NotSupported"

    targetarch = d.getVar("TARGET_ARCH")
    if targetarch.startswith("microblaze"):
        ldso_arch = "microblaze${@bb.utils.contains('TUNE_FEATURES', 'bigendian', '', 'el', d)}"
    elif targetarch.startswith("mips"):
        ldso_arch = "mips${ABIEXTENSION}${MIPSPKGSFX_BYTE}${MIPSPKGSFX_R6}${MIPSPKGSFX_ENDIAN}${@['', '-sf'][d.getVar('TARGET_FPU') == 'soft']}"
    elif targetarch == "powerpc":
        ldso_arch = "powerpc${@['', '-sf'][d.getVar('TARGET_FPU') == 'soft']}"
    elif targetarch.startswith("powerpc64"):
        ldso_arch = "powerpc64${@bb.utils.contains('TUNE_FEATURES', 'bigendian', '', 'le', d)}"
    elif targetarch == "x86_64":
        ldso_arch = "x86_64"
    elif re.search("i.86", targetarch):
        ldso_arch = "i386"
    elif targetarch.startswith("arm"):
        ldso_arch = "arm${ARMPKGSFX_ENDIAN}${ARMPKGSFX_EABI}"
    elif targetarch.startswith("aarch64"):
        ldso_arch = "aarch64${ARMPKGSFX_ENDIAN_64}"
    elif targetarch.startswith("riscv64"):
        ldso_arch = "riscv64${@['', '-sf'][d.getVar('TARGET_FPU') == 'soft']}"
    elif targetarch.startswith("riscv32"):
        ldso_arch = "riscv32${@['', '-sf'][d.getVar('TARGET_FPU') == 'soft']}"
    return ldso_arch

def get_musl_loader(d):
    import re
    return "/lib/ld-musl-" + get_musl_loader_arch(d) + ".so.1"

def get_glibc_loader(d):
    import re

    dynamic_loader = "NotSupported"
    targetarch = d.getVar("TARGET_ARCH")
    if targetarch in ["powerpc", "microblaze"]:
        dynamic_loader = "${base_libdir}/ld.so.1"
    elif targetarch in ["mipsisa32r6el", "mipsisa32r6", "mipsisa64r6el", "mipsisa64r6"]:
        dynamic_loader = "${base_libdir}/ld-linux-mipsn8.so.1"
    elif targetarch.startswith("mips"):
        dynamic_loader = "${base_libdir}/ld.so.1"
    elif targetarch == "powerpc64le":
        dynamic_loader = "${base_libdir}/ld64.so.2"
    elif targetarch == "powerpc64":
        dynamic_loader = "${base_libdir}/ld64.so.1"
    elif targetarch == "x86_64":
        dynamic_loader = "${base_libdir}/ld-linux-x86-64.so.2"
    elif re.search("i.86", targetarch):
        dynamic_loader = "${base_libdir}/ld-linux.so.2"
    elif targetarch == "arm":
        dynamic_loader = "${base_libdir}/ld-linux${@['-armhf', ''][d.getVar('TARGET_FPU') == 'soft']}.so.3"
    elif targetarch.startswith("aarch64"):
        dynamic_loader = "${base_libdir}/ld-linux-aarch64${ARMPKGSFX_ENDIAN_64}.so.1"
    elif targetarch.startswith("riscv64"):
        dynamic_loader = "${base_libdir}/ld-linux-riscv64-lp64${@['d', ''][d.getVar('TARGET_FPU') == 'soft']}.so.1"
    elif targetarch.startswith("riscv32"):
        dynamic_loader = "${base_libdir}/ld-linux-riscv32-ilp32${@['d', ''][d.getVar('TARGET_FPU') == 'soft']}.so.1"
    return dynamic_loader

def get_linuxloader(d):
    overrides = d.getVar("OVERRIDES").split(":")

    if "libc-baremetal" in overrides:
        return "NotSupported"

    if "libc-musl" in overrides:
        dynamic_loader = get_musl_loader(d)
    else:
        dynamic_loader = get_glibc_loader(d)
    return dynamic_loader

get_linuxloader[vardepvalue] = "${@get_linuxloader(d)}"
get_musl_loader[vardepvalue] = "${@get_musl_loader(d)}"
get_musl_loader_arch[vardepvalue] = "${@get_musl_loader_arch(d)}"
get_glibc_loader[vardepvalue] = "${@get_glibc_loader(d)}"
