SUMMARY = "GN is a meta-build system that generates build files for Ninja"
HOMEPAGE = "https://gn.googlesource.com/gn/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0fca02217a5d49a14dfe2d11837bb34d"

DEPENDS += "ninja-native"
UPSTREAM_CHECK_COMMITS = "1"

SRC_URI = "git://gn.googlesource.com/gn;protocol=https;branch=main"
SRCREV = "9d19a7870add65151ff91bcc26252bb7521065cf"
PV = "0+git"

BB_GIT_SHALLOW = ""

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

# Catch build progress from ninja
do_compile[progress] = "outof:^\[(\d+)/(\d+)\]\s+"

do_compile() {
    ninja -C ${B} --verbose
}

do_install() {
    install -d ${D}${bindir}
    install ${B}/gn ${D}${bindir}
}

BBCLASSEXTEND = "native"

COMPATIBLE_HOST = "^(?!riscv32).*"

CFLAGS:append:toolchain-gcc = " -Wno-error=maybe-uninitialized"
