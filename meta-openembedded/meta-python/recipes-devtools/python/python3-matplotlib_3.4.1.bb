SUMMARY = "matplotlib: plotting with Python"
DESCRIPTION = "\
Matplotlib is a Python 2D plotting library which produces \
publication-quality figures in a variety of hardcopy formats \
and interactive environments across platforms."
HOMEPAGE = "https://github.com/matplotlib/matplotlib"
SECTION = "devel/python"
LICENSE = "PSF"
LIC_FILES_CHKSUM = "\
    file://setup.py;beginline=282;endline=282;md5=20e7ab4d2b2b1395a0e4ab800181eb96 \
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

SRC_URI[sha256sum] = "84d4c4f650f356678a5d658a43ca21a41fca13f9b8b00169c0b76e6a6a948908"

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
