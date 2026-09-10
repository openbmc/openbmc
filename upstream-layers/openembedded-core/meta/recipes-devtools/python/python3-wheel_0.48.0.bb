SUMMARY = "The official binary distribution format for Python "
HOMEPAGE = "https://github.com/pypa/wheel"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=7ffb0db04527cfe380e4f2726bd05ebf"

SRC_URI[sha256sum] = "94800765601e9171bf5d58d066e640662842bcedcbab982b2c90787a2c987322"

inherit python_flit_core pypi ptest-python-pytest

CVE_PRODUCT = "wheel_project:wheel"

RDEPENDS:${PN} += "python3-packaging"

# One test is skipped but requires the "full" python3-flit, not just python3-flit-core
RDEPENDS:${PN}-ptest += "python3-setuptools"

BBCLASSEXTEND = "native nativesdk"

# This used to use the bootstrap install which didn't compile. Until we bump the
# tmpdir version we can't compile the native otherwise the sysroot unpack fails
INSTALL_WHEEL_COMPILE_BYTECODE:class-native = "--no-compile-bytecode"
