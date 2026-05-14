SUMMARY = "The official binary distribution format for Python "
HOMEPAGE = "https://github.com/pypa/wheel"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=7ffb0db04527cfe380e4f2726bd05ebf"

SRC_URI[sha256sum] = "cc72bd1009ba0cf63922e28f94d9d83b920aa2bb28f798a31d0691b02fa3c9b3"

inherit python_flit_core pypi ptest-python-pytest

RDEPENDS:${PN} += "python3-packaging"

# One test is skipped but requires the "full" python3-flit, not just python3-flit-core
RDEPENDS:${PN}-ptest += "python3-setuptools"

BBCLASSEXTEND = "native nativesdk"

# This used to use the bootstrap install which didn't compile. Until we bump the
# tmpdir version we can't compile the native otherwise the sysroot unpack fails
INSTALL_WHEEL_COMPILE_BYTECODE:class-native = "--no-compile-bytecode"
