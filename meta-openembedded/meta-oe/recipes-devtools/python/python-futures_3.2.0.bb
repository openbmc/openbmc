DESCRIPTION = "The concurrent.futures module provides a high-level interface for asynchronously executing callables."
SECTION = "devel/python"
LICENSE = "PSF"
LIC_FILES_CHKSUM = "file://LICENSE;md5=834d982f973c48b6d662b5944c5ab567"
HOMEPAGE = "https://github.com/agronholm/pythonfutures"
DEPENDS = "python"

SRC_URI[md5sum] = "d1b299a06b96ccb59f70324716dc0016"
SRC_URI[sha256sum] = "9ec02aa7d674acb8618afb127e27fde7fc68994c0437ad759fa094a574adb265"

inherit pypi setuptools

BBCLASSEXTEND = "native nativesdk"
