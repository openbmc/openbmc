SUMMARY = "matplotlib: plotting with Python"
DESCRIPTION = "\
Matplotlib is a Python 2D plotting library which produces \
publication-quality figures in a variety of hardcopy formats \
and interactive environments across platforms."
HOMEPAGE = "https://github.com/matplotlib/matplotlib"
SECTION = "devel/python"
LICENSE = "PSF"
LIC_FILES_CHKSUM = "\
    file://setup.py;beginline=251;endline=251;md5=e0ef37de7122ce842bcd1fb54482b353 \
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

SRC_URI[md5sum] = "f3a405f340be5b151cb2042c4d8d16f7"
SRC_URI[sha256sum] = "87f53bcce90772f942c2db56736788b39332d552461a5cb13f05ff45c1680f0e"

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
