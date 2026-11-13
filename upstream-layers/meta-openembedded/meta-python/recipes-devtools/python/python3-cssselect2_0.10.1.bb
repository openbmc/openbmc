SUMMARY = "CSS selectors for Python ElementTree."
DESCRIPTION = "cssselect2 is a straightforward implementation of CSS4 Selectors \
    for markup documents (HTML, XML, etc.) that can be read by ElementTree-like \
    parsers (including cElementTree, lxml, html5lib, etc.)"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=aa7228954285c7398bb6711fee73b4ac"

SRC_URI[sha256sum] = "83b0d820ef589dabaf693289b647c2f5b410f76d285f56deba911ffa75a7b9d1"

inherit pypi python_flit_core

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} = " \
    python3-tinycss2 \
    python3-webencodings \
"
