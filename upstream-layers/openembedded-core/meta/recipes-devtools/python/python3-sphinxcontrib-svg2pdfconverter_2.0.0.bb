SUMMARY = "Sphinx SVG to PDF or PNG converter extension"
HOMEPAGE = "https://github.com/missinglinkelectronics/sphinxcontrib-svg2pdfconverter"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=b11cf936853a71258d4b57bb1849a3f9"

SRC_URI[sha256sum] = "ab9c8f1080391e231812d20abf2657a69ee35574563b1014414f953964a95fa3"

inherit pypi python_setuptools_build_meta

PYPI_PACKAGE = "sphinxcontrib_svg2pdfconverter"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

RDEPENDS:${PN} = "python3-sphinx"
# Only support sphinxcontrib.rsvgconverter for now.
# sphinxcontrib.cairosvgconverter depends on cairosvg module, no recipe yet
# sphinxcontrib.inkscapeconverter depends on inkscape, recipe in meta-office,
# 3rd-party layer not updated in years
RDEPENDS:${PN} += "librsvg"

BBCLASSEXTEND = "native nativesdk"
