SUMMARY = "High level compatibility layer for multiple asynchronous event loop implementations"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c0a769411d2af7894099e8ff75058c9f"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "41cfcc3a4c85d3f05c932da7c26d0201ac36f72abd4435ba90d0464a3ffed703"

DEPENDS += " \
	python3-setuptools-scm-native \
"

# Don't provide "trio" PACKAGECONFIG as nothing provides "python3-trio" currently.
# If somebody needs this please feel free to add python3-trio and enable the
# packageconfig below:
#PACKAGECONFIG ??= ""
#PACKAGECONFIG[trio] = ",,,python3-trio"

RDEPENDS:${PN} += "\
    python3-idna \
    python3-sniffio \
    python3-core \
    python3-numbers \
    python3-io \
    python3-asyncio \
"
