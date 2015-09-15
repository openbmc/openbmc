SUMMARY = "Language for writing Python extension modules"
DESCRIPTION = "Pyrex is a language specially designed for writing Python extension modules. \
It's designed to bridge the gap between the nice, high-level, easy-to-use world of Python \
and the messy, low-level world of C."
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=771d472f53f933033f57eeee7808e5bd"
SRCNAME = "Pyrex"
PR = "r4"

SRC_URI = "\
  http://www.cosc.canterbury.ac.nz/greg.ewing/python/${SRCNAME}/${SRCNAME}-${PV}.tar.gz \
  file://pyrex-fix-optimized-mode.patch \
"

SRC_URI[md5sum] = "515dee67d15d4393841e2d60e8341947"
SRC_URI[sha256sum] = "5f87df06831d0b3412eb4bc9d3fc2ee7bfae1b913d7da8c23ab2bf5699fb6b50"
S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit distutils

FILES_${PN} += "${datadir}${base_libdir}/${PYTHON_DIR}/site-packages/Pyrex/Compiler/Lexicon.pickle"
