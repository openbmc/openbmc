DESCRIPTION = "Provide XPath 1.0 and 2.0 selectors for Python's ElementTree XML data structures, both for the standard ElementTree library and for the lxml.etree library."
HOMEPAGE = "https://github.com/sissaschool/elementpath"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c04ab6ce886cc843af8941bb199406ba"

SRC_URI[sha256sum] = "3c912a9c41311ac5244c038867670d7c0b0fec0e444813608e5b846dbb3428ac"


inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += " \
    python3-xml \
    python3-core \
    python3-numbers \
    python3-datetime \
    python3-stringold \
"

BBCLASSEXTEND = "native nativesdk"
