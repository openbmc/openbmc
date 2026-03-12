SUMMARY = "Setuptools Rust extension plugin"
DESCRIPTION = "setuptools-rust is a plugin for setuptools to build Rust \
Python extensions implemented with PyO3 or rust-cpython.\
\
Compile and distribute Python extensions written in Rust as easily as if they were written in C."
HOMEPAGE = "https://github.com/PyO3/setuptools-rust"
BUGTRACKER = "https://github.com/PyO3/setuptools-rust/issues"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=011cd92e702dd9e6b1a26157b6fd53f5"

SRC_URI[sha256sum] = "d94a93f0c97751c17014565f07bdc324bee45d396cd1bba83d8e7af92b945f0c"

PYPI_PACKAGE = "setuptools_rust"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit cargo pypi python_setuptools_build_meta

DEPENDS += "python3-setuptools-scm-native python3-wheel-native"

RDEPENDS:${PN} += " \
    python3-json \
    python3-semantic-version \
    python3-setuptools \
    python3-setuptools-scm \
    python3-shell \
    python3-typing-extensions \
    python3-wheel \
"

BBCLASSEXTEND = "native nativesdk"
