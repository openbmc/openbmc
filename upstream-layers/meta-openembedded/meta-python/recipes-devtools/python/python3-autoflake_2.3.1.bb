SUMMARY = "Removes unused imports and unused variables"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=88246be6a34c1496c253f58599f3db85"

SRC_URI[sha256sum] = "c98b75dc5b0a86459c4f01a1d32ac7eb4338ec4317a4469515ff1e687ecd909e"

inherit pypi python_hatchling

RDEPENDS:${PN} += "python3-pyflakes"

BBCLASSEXTEND = "native nativesdk"
