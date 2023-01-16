SUMMARY = "GN is a meta-build system that generates build files for Ninja"
DEPENDS += "ninja-native"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0fca02217a5d49a14dfe2d11837bb34d"

SRC_URI = "git://gn.googlesource.com/gn;protocol=https;branch=main \
           file://0001-Replace-lstat64-stat64-functions-on-linux.patch"
SRCREV = "bf4e17dc67b2a2007475415e3f9e1d1cf32f6e35"
PV = "0+git${SRCPV}"

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

# Map from our _OS strings to the GN's platform values.
def gn_platform(variable, d):
    os = d.getVar(variable)
    if "linux" in os:
        return "linux"
    elif "mingw" in os:
        return "mingw"
    else:
        return os

do_configure[cleandirs] += "${B}"
do_configure() {
    python3 ${S}/build/gen.py \
        --platform=${@gn_platform("TARGET_OS", d)} \
        --out-path=${B} \
        --no-static-libstdc++ \
        --no-strip
}

do_compile() {
    ninja -C ${B} --verbose
}

do_install() {
    install -d ${D}${bindir}
    install ${B}/gn ${D}${bindir}
}

BBCLASSEXTEND = "native"

COMPATIBLE_HOST = "^(?!riscv32).*"
