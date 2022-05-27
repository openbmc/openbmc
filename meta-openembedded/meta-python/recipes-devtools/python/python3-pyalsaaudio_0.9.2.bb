SUMMARY = "ALSA bindings"
SECTION = "devel/python"
LICENSE = "PSF-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1a3b161aa0fcec32a0c8907a2219ad9d"

inherit pypi setuptools3

SRC_URI[sha256sum] = "e74a66d6c7a6bcceb990df66d3ebc0fe382fc9d765f35f050f9d98c695304b36"

DEPENDS += "alsa-lib"

RDEPENDS:${PN} += "libasound"
