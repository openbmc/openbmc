SUMMARY = "The official binary distribution format for Python "
HOMEPAGE = "https://github.com/pypa/wheel"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=10;endline=10;md5=8227180126797a0148f94f483f3e1489"

SRC_URI[sha256sum] = "e9a504e793efbca1b8e0e9cb979a249cf4a0a7b5b8c9e8b65a5e39d49529c1c4"

inherit python_flit_core pypi

SRC_URI += "file://0001-Backport-pyproject.toml-from-flit-backend-branch.patch \
            file://0001-Fixed-potential-DoS-attack-via-WHEEL_INFO_RE.patch \
           "

BBCLASSEXTEND = "native nativesdk"

# This used to use the bootstrap install which didn't compile. Until we bump the
# tmpdir version we can't compile the native otherwise the sysroot unpack fails
INSTALL_WHEEL_COMPILE_BYTECODE:class-native = "--no-compile-bytecode"
