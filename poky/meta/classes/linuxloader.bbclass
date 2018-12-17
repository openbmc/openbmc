def get_musl_loader(d):
    import re
    dynamic_loader = None

    targetarch = d.getVar("TARGET_ARCH")
    if targetarch.startswith("microblaze"):
        dynamic_loader = "${base_libdir}/ld-musl-microblaze${@bb.utils.contains('TUNE_FEATURES', 'bigendian', '', 'el' ,d)}.so.1"
    elif targetarch.startswith("mips"):
        dynamic_loader = "${base_libdir}/ld-musl-mips${ABIEXTENSION}${MIPSPKGSFX_BYTE}${MIPSPKGSFX_R6}${MIPSPKGSFX_ENDIAN}${@['', '-sf'][d.getVar('TARGET_FPU') == 'soft']}.so.1"
    elif targetarch == "powerpc":
        dynamic_loader = "${base_libdir}/ld-musl-powerpc${@['', '-sf'][d.getVar('TARGET_FPU') == 'soft']}.so.1"
    elif targetarch == "powerpc64":
        dynamic_loader = "${base_libdir}/ld-musl-powerpc64.so.1"
    elif targetarch == "x86_64":
        dynamic_loader = "${base_libdir}/ld-musl-x86_64.so.1"
    elif re.search("i.86", targetarch):
        dynamic_loader = "${base_libdir}/ld-musl-i386.so.1"
    elif targetarch.startswith("arm"):
        dynamic_loader = "${base_libdir}/ld-musl-arm${ARMPKGSFX_ENDIAN}${ARMPKGSFX_EABI}.so.1"
    elif targetarch.startswith("aarch64"):
        dynamic_loader = "${base_libdir}/ld-musl-aarch64${ARMPKGSFX_ENDIAN_64}.so.1"
    return dynamic_loader

def get_glibc_loader(d):
    import re

    dynamic_loader = None
    targetarch = d.getVar("TARGET_ARCH")
    if targetarch in ["powerpc", "microblaze"]:
        dynamic_loader = "${base_libdir}/ld.so.1"
    elif targetarch in ["mipsisa32r6el", "mipsisa32r6", "mipsisa64r6el", "mipsisa64r6"]:
        dynamic_loader = "${base_libdir}/ld-linux-mipsn8.so.1"
    elif targetarch.startswith("mips"):
        dynamic_loader = "${base_libdir}/ld.so.1"
    elif targetarch == "powerpc64":
        dynamic_loader = "${base_libdir}/ld64.so.1"
    elif targetarch == "x86_64":
        dynamic_loader = "${base_libdir}/ld-linux-x86-64.so.2"
    elif re.search("i.86", targetarch):
        dynamic_loader = "${base_libdir}/ld-linux.so.2"
    elif targetarch == "arm":
        dynamic_loader = "${base_libdir}/ld-linux.so.3"
    elif targetarch.startswith("aarch64"):
        dynamic_loader = "${base_libdir}/ld-linux-aarch64${ARMPKGSFX_ENDIAN_64}.so.1"
    return dynamic_loader

def get_linuxloader(d):
    overrides = d.getVar("OVERRIDES").split(":")

    if "libc-baremetal" in overrides:
        return None

    if "libc-musl" in overrides:
        dynamic_loader = get_musl_loader(d)
    else:
        dynamic_loader = get_glibc_loader(d)
    return dynamic_loader

get_linuxloader[vardepvalue] = "${@get_linuxloader(d)}"
get_musl_loader[vardepvalue] = "${@get_musl_loader(d)}"
get_glibc_loader[vardepvalue] = "${@get_glibc_loader(d)}"
