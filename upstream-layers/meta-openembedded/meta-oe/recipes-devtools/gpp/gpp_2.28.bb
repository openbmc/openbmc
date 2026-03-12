SUMMARY = "Generic Preprocessor"
DESCRIPTION = "GPP is a general-purpose preprocessor with customizable syntax, suitable for a wide range of preprocessing tasks"
HOMEPAGE = "https://logological.org/gpp"
BUGTRACKER = "https://github.com/logological/gpp/issues"
LICENSE = "LGPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "https://github.com/logological/gpp/releases/download/${PV}/gpp-${PV}.tar.bz2"
SRC_URI[sha256sum] = "343d33d562e2492ca9b51ff2cc4b06968a17a85fdc59d5d4e78eed3b1d854b70"

inherit autotools

BBCLASSEXTEND = "native"
