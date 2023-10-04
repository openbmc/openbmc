SUMMARY = "Unbearably fast runtime type checking in pure Python."
HOMEPAGE = "https://beartype.readthedocs.io"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e40b52d8eb5553aa8f705cdd3f979d69"

SRC_URI[sha256sum] = "2af6a8d8a7267ccf7d271e1a3bd908afbc025d2a09aa51123567d7d7b37438df"

inherit setuptools3 pypi

BBCLASSEXTEND = "native nativesdk"
