SUMMARY = "matplotlib: plotting with Python"
DESCRIPTION = "\
Matplotlib is a Python 2D plotting library which produces \
publication-quality figures in a variety of hardcopy formats \
and interactive environments across platforms."
HOMEPAGE = "https://github.com/matplotlib/matplotlib"
SECTION = "devel/python"
LICENSE = "PSF-2.0"
LIC_FILES_CHKSUM = "\
    file://setup.py;beginline=296;endline=296;md5=20e7ab4d2b2b1395a0e4ab800181eb96 \
    file://LICENSE/LICENSE;md5=afec61498aa5f0c45936687da9a53d74 \
"

DEPENDS = "\
    freetype \
    libpng \
    ${PYTHON_PN}-numpy-native \
    ${PYTHON_PN}-pip-native \
    ${PYTHON_PN}-dateutil-native \
    ${PYTHON_PN}-pytz-native \
    ${PYTHON_PN}-certifi-native \
"

SRC_URI[sha256sum] = "b2e9810e09c3a47b73ce9cab5a72243a1258f61e7900969097a817232246ce1c"

inherit pypi setuptools3 pkgconfig

# Stop the component from attempting to download when it detects a missing
# dependency
SRC_URI += "file://matplotlib-disable-download.patch"

# This python module requires a full copy of freetype-2.6.1
SRC_URI += "https://downloads.sourceforge.net/project/freetype/freetype2/2.6.1/freetype-2.6.1.tar.gz;name=freetype;subdir=matplotlib-${PV}/build"
SRC_URI[freetype.sha256sum] = "0a3c7dfbda6da1e8fce29232e8e96d987ababbbf71ebc8c75659e4132c367014"

# This python module requires a full copy of 'qhull-2020'
SRC_URI += "http://www.qhull.org/download/qhull-2020-src-8.0.2.tgz;name=qhull;subdir=matplotlib-${PV}/build"
SRC_URI[qhull.sha256sum] = "b5c2d7eb833278881b952c8a52d20179eab87766b00b865000469a45c1838b7e"

# LTO with clang needs lld
LDFLAGS:append:toolchain-clang = " -fuse-ld=lld"
LDFLAGS:remove:toolchain-clang:mips = "-fuse-ld=lld"

RDEPENDS:${PN} = "\
    freetype \
    libpng \
    ${PYTHON_PN}-numpy \
    ${PYTHON_PN}-pyparsing \
    ${PYTHON_PN}-cycler \
    ${PYTHON_PN}-dateutil \
    ${PYTHON_PN}-kiwisolver \
    ${PYTHON_PN}-pytz \
    ${PYTHON_PN}-pillow \
"

ENABLELTO:toolchain-clang:riscv64 = "echo enable_lto = False >> ${S}/mplsetup.cfg"
ENABLELTO:toolchain-clang:riscv32 = "echo enable_lto = False >> ${S}/mplsetup.cfg"
ENABLELTO:toolchain-clang:mips = "echo enable_lto = False >> ${S}/mplsetup.cfg"
do_compile:prepend() {
    echo [libs] > ${S}/mplsetup.cfg
    echo system_freetype = True >> ${S}/mplsetup.cfg
    ${ENABLELTO}
}

BBCLASSEXTEND = "native"
