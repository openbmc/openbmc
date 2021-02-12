SUMMARY = "matplotlib: plotting with Python"
DESCRIPTION = "\
Matplotlib is a Python 2D plotting library which produces \
publication-quality figures in a variety of hardcopy formats \
and interactive environments across platforms."
HOMEPAGE = "https://github.com/matplotlib/matplotlib"
SECTION = "devel/python"
LICENSE = "PSF"
LIC_FILES_CHKSUM = "\
    file://setup.py;beginline=273;endline=273;md5=e0ef37de7122ce842bcd1fb54482b353 \
    file://LICENSE/LICENSE;md5=afec61498aa5f0c45936687da9a53d74 \
"
DEPENDS = "\
    freetype \
    libpng \
    python3-numpy-native \
    python3-dateutil-native \
    python3-pytz-native \
    python3-certifi-native \
"

SRC_URI[md5sum] = "0b48f34ec623e765a1bda15924ce0b56"
SRC_URI[sha256sum] = "3e477db76c22929e4c6876c44f88d790aacdf3c3f8f3a90cb1975c0bf37825b0"

inherit pypi setuptools3 pkgconfig

# LTO with clang needs lld
LDFLAGS_append_toolchain-clang = " -fuse-ld=lld"
LDFLAGS_remove_toolchain-clang_mips = "-fuse-ld=lld"

RDEPENDS_${PN} = "\
    freetype \
    libpng \
    python3-numpy \
    python3-pyparsing \
    python3-cycler \
    python3-dateutil \
    python3-kiwisolver \
    python3-pytz \
"

ENABLELTO_toolchain-clang_riscv64 = "echo enable_lto = False >> ${S}/setup.cfg"
ENABLELTO_toolchain-clang_riscv32 = "echo enable_lto = False >> ${S}/setup.cfg"
ENABLELTO_toolchain-clang_mips = "echo enable_lto = False >> ${S}/setup.cfg"
do_compile_prepend() {
    echo [libs] > ${S}/setup.cfg
    echo system_freetype = true >> ${S}/setup.cfg
    ${ENABLELTO}
}

BBCLASSEXTEND = "native"
