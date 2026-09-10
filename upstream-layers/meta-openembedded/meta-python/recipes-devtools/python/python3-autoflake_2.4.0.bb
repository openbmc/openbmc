SUMMARY = "Removes unused imports and unused variables"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=88246be6a34c1496c253f58599f3db85"

SRC_URI[sha256sum] = "ef7c496d9bce9d2cef049f24e482d1d3090c37fbd44e5e85dfb00db3c78ee16c"

inherit pypi python_hatchling

RDEPENDS:${PN} += "python3-pyflakes"

BBCLASSEXTEND = "native nativesdk"
