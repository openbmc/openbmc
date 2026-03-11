SUMMARY = "Cross-platform colored terminal text."
HOMEPAGE = "https://github.com/tartley/colorama"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=b4936429a56a652b84c5c01280dcaa26"

inherit pypi python_hatchling

SRC_URI[sha256sum] = "08695f5cb7ed6e0531a20572697297273c47b8cae5a63ffc6d6ed5c201be6e44"

BBCLASSEXTEND = "native nativesdk"
