SUMMARY = "Removes unused imports and unused variables"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=88246be6a34c1496c253f58599f3db85"

SRC_URI[sha256sum] = "62b7b6449a692c3c9b0c916919bbc21648da7281e8506bcf8d3f8280e431ebc1"

inherit pypi python_hatchling

RDEPENDS:${PN} += "python3-pyflakes"

BBCLASSEXTEND = "native nativesdk"
