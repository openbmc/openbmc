SUMMARY = "matplotlib: plotting with Python"
DESCRIPTION = "\
Matplotlib is a Python 2D plotting library which produces \
publication-quality figures in a variety of hardcopy formats \
and interactive environments across platforms."
HOMEPAGE = "https://github.com/matplotlib/matplotlib"
SECTION = "devel/python"
LICENSE = "PSF"
LIC_FILES_CHKSUM = "\
    file://setup.py;beginline=259;endline=259;md5=e0ef37de7122ce842bcd1fb54482b353 \
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

SRC_URI[md5sum] = "a85791908e78818bd425ba9ab38500fa"
SRC_URI[sha256sum] = "3d2edbf59367f03cd9daf42939ca06383a7d7803e3993eb5ff1bee8e8a3fbb6b"

inherit pypi setuptools3 pkgconfig

# LTO with clang needs lld
LDFLAGS_append_toolchain-clang = " -fuse-ld=lld"

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

do_compile_prepend() {
    echo [libs] > ${S}/setup.cfg
    echo system_freetype = true >> ${S}/setup.cfg
}

BBCLASSEXTEND = "native"
