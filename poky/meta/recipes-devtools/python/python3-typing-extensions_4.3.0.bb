HOMEPAGE = "https://github.com/python/typing"
LICENSE = "PSF-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=64fc2b30b67d0a8423c250e0386ed72f"

# The name on PyPi is slightly different.
PYPI_PACKAGE = "typing_extensions"

SRC_URI[sha256sum] = "e6d2677a32f47fc7eb2795db1dd15c1f34eff616bcaf2cfb5e997f854fa1c4a6"

inherit pypi python_flit_core

BBCLASSEXTEND = "native nativesdk"

UPSTREAM_CHECK_REGEX = "/typing-extensions/(?P<pver>(\d+[\.\-_]*)+)/"
