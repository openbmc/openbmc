SUMMARY = "Filesystem events monitoring"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=56bd93578433bb99b4fdf7ff481722df"

SRC_URI[sha256sum] = "630c1edc09ef93abea40fc06b10067e5734d8f38cc85867bc61d1a5c9eb7796f"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
