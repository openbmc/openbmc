SUMMARY = "Sphinx SVG to PDF or PNG converter extension"
HOMEPAGE = "https://github.com/missinglinkelectronics/sphinxcontrib-svg2pdfconverter"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=b11cf936853a71258d4b57bb1849a3f9"

SRC_URI[sha256sum] = "9756e82d5f3bf11629ffcbafb1f8a1092d3bb4789e33494032cdce9a9c8459d3"

inherit pypi python_setuptools_build_meta

PYPI_PACKAGE = "sphinxcontrib_svg2pdfconverter"

RDEPENDS:${PN} = "python3-sphinx"
# Only support sphinxcontrib.rsvgconverter for now.
# sphinxcontrib.cairosvgconverter depends on cairosvg module, no recipe yet
# sphinxcontrib.inkscapeconverter depends on inkscape, recipe in meta-office,
# 3rd-party layer not updated in years
RDEPENDS:${PN} += "librsvg"

BBCLASSEXTEND = "native nativesdk"
