SUMMARY = "Automatic C++ library api documentation generation: breathe doxygen in and exhale it out. "
DESCRIPTION = "\
    Automatic C++ library API documentation generator using Doxygen, Sphinx, and Breathe. \
    Exhale revives Doxygen's class, file, and page hierarchies in reStructuredText documents \
    to make these hierarchies available in Sphinx documented projects."
HOMEPAGE = "https://github.com/svenevs/exhale"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fa7943919a123ae99ee86a0daf9e1871"

SRC_URI[sha256sum] = "752a96d0a59456511d933311d4a81f642cd668296eacd2561905727d5ed6b0d8"

inherit pypi python_setuptools_build_meta

PYPI_PACKAGE = "exhale"

BBCLASSEXTEND =+ "native"
