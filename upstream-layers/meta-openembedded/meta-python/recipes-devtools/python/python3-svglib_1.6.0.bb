SUMMARY = "A pure-Python library for reading and converting SVG."
DESCRIPTION = "Svglib is a Python library for reading SVG files and \
    converting them (to a reasonable degree) to other formats using the \
    ReportLab Open Source toolkit."

LICENSE = "LGPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=b52f2d57d10c4f7ee67a7eb9615d5d24"

CVE_PRODUCT = "svglib"

SRC_URI[sha256sum] = "4c38a274a744ef0d1677f55d5d62fc0fb798819f813e52872a796e615741733d"

inherit pypi python_hatchling

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} = "python3-pillow python3-html python3-cssselect2 python3-webencodings \
    python3-lxml"
