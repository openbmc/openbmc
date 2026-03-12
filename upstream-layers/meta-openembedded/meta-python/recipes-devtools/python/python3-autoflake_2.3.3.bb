SUMMARY = "Removes unused imports and unused variables"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=88246be6a34c1496c253f58599f3db85"

SRC_URI[sha256sum] = "c24809541e23999f7a7b0d2faadf15deb0bc04cdde49728a2fd943a0c8055504"

inherit pypi python_hatchling

RDEPENDS:${PN} += "python3-pyflakes"

BBCLASSEXTEND = "native nativesdk"
