HOMEPAGE = "https://github.com/python/typing"
LICENSE = "PSF-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=64fc2b30b67d0a8423c250e0386ed72f"

# The name on PyPi is slightly different.
PYPI_PACKAGE = "typing_extensions"

SRC_URI[sha256sum] = "f1c24655a0da0d1b67f07e17a5e6b2a105894e6824b92096378bb3668ef02376"

inherit pypi python_flit_core

BBCLASSEXTEND = "native nativesdk"

UPSTREAM_CHECK_REGEX = "/typing-extensions/(?P<pver>(\d+[\.\-_]*)+)/"
