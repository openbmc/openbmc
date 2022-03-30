HOMEPAGE = "https://github.com/python/typing"
LICENSE = "PSF-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=64fc2b30b67d0a8423c250e0386ed72f"

# The name on PyPi is slightly different.
PYPI_PACKAGE = "typing_extensions"

SRC_URI[sha256sum] = "50b6f157849174217d0656f99dc82fe932884fb250826c18350e159ec6cdf342"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"

UPSTREAM_CHECK_REGEX = "/typing-extensions/(?P<pver>(\d+[\.\-_]*)+)/"
