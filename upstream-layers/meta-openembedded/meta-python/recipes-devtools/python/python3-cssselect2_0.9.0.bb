SUMMARY = "CSS selectors for Python ElementTree."
DESCRIPTION = "cssselect2 is a straightforward implementation of CSS4 Selectors \
    for markup documents (HTML, XML, etc.) that can be read by ElementTree-like \
    parsers (including cElementTree, lxml, html5lib, etc.)"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=aa7228954285c7398bb6711fee73b4ac"

SRC_URI[sha256sum] = "759aa22c216326356f65e62e791d66160a0f9c91d1424e8d8adc5e74dddfc6fb"

inherit pypi python_setuptools_build_meta

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} = "python3-tinycss2"
