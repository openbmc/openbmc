SUMMARY = "Tool to Detect Surrounding Shell"
SECTION = "devel/python"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=78e1c0248051c32a38a7f820c30bd7a5"

SRC_URI[sha256sum] = "cb4a6fec583535bc6da17b647dd2330cf7ef30239e05d547d99ae3705fd0f7f8"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
