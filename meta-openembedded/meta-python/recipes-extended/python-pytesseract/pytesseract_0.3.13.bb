SUMMARY = "Python-tesseract is an optical character recognition (OCR) tool for python. That is, it will recognize and "read" the text embedded in images."

HOMEPAGE = "https://github.com/madmaze/pytesseract"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI[md5sum] = "73f9645e59b437f064d05882b95832ce"

PYPI_PACKAGE = "pytesseract"

inherit pypi setuptools3 

BBCLASSEXTEND = "native"
