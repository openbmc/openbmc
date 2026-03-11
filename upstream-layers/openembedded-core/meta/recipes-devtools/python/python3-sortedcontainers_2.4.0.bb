SUMMARY = "Sorted Containers is an Apache2 licensed sorted collections library, written in pure-Python, and fast as C-extensions."
HOMEPAGE = "http://www.grantjenks.com/docs/sortedcontainers/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7c7c6a1a12ec816da16c1839137d53ae"

inherit pypi setuptools3
SRC_URI[sha256sum] = "25caa5a06cc30b6b83d11423433f65d1f9d76c4c6a0c90e3379eaa43b9bfdb88"

BBCLASSEXTEND = "native nativesdk"
