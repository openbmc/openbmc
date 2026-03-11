SUMMARY = "Python bindings for interfacing with the Zstandard compression library"
DESCRIPTION = "This project provides Python bindings for interfacing with the \
               Zstandard compression library. A C extension and CFFI interface \
	       are provided."
HOMEPAGE = "https://github.com/indygreg/python-zstandard"
BUGTRACKER = "https://github.com/indygreg/python-zstandard/issues"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3ae87c50fd64b6f0942823686871e758"

DEPENDS = "python3-cffi"

RDEPENDS_${PN} = "\
    libzstd (>= 1.4.0) \
    python3-cffi \
"

inherit setuptools3 pypi

SRC_URI[sha256sum] = "b2d8c62d08e7255f68f7a740bae85b3c9b8e5466baa9cbf7f57f1cde0ac6bc09"

# Because the pyproject.toml is still in development and it contains invalid
# requirements.
INSANE_SKIP += "pep517-backend"
