SUMMARY = "Setuptools Rust extension plugin"
DESCRIPTION = "setuptools-rust is a plugin for setuptools to build Rust \
Python extensions implemented with PyO3 or rust-cpython.\
\
Compile and distribute Python extensions written in Rust as easily as if they were written in C."
HOMEPAGE = "https://github.com/PyO3/setuptools-rust"
BUGTRACKER = "https://github.com/PyO3/setuptools-rust/issues"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=011cd92e702dd9e6b1a26157b6fd53f5"

SRC_URI = "https://files.pythonhosted.org/packages/67/08/e1aa2c582c62ac76e4d60f8e454bd3bba933781a06a88b4e38797445822a/setuptools-rust-${PV}.tar.gz"
SRC_URI[sha256sum] = "a0adb9b503c0ffc4e8fe80b7c617898cefa78049983aaaea7f747e153a3e65d1"

inherit cargo pypi python_setuptools_build_meta native

DEPENDS += " \
    python3-semantic-version-native \
    python3-setuptools-native \
    python3-setuptools-scm-native \
    python3-toml-native \
    python3-typing-extensions-native \
    python3-wheel-native \
"
