SUMMARY = "Tool to Detect Surrounding Shell"
SECTION = "devel/python"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=78e1c0248051c32a38a7f820c30bd7a5"

SRC_URI[sha256sum] = "8dbca0739d487e5bd35ab3ca4b36e11c4078f3a234bfce294b0a0291363404de"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
