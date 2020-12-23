SUMMARY = "Sorted Containers is an Apache2 licensed sorted collections library, written in pure-Python, and fast as C-extensions."
HOMEPAGE = "http://www.grantjenks.com/docs/sortedcontainers/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7c7c6a1a12ec816da16c1839137d53ae"

inherit pypi setuptools3
SRC_URI[sha256sum] = "59cc937650cf60d677c16775597c89a960658a09cf7c1a668f86e1e4464b10a1"

BBCLASSEXTEND = "native nativesdk"
