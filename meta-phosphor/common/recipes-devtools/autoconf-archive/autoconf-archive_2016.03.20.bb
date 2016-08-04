require autoconf-archive.inc

PARALLEL_MAKE = ""

LICENSE = "GPLv2 & GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI[md5sum] = "99c4167b8107189bb6dcb98b09956829"
SRC_URI[sha256sum] = "88fb2efff640eddd28a52ae550ff5561bca3bd2bba09e1d7b0580e719875e437"

EXTRA_OECONF += "ac_cv_path_M4=m4"

BBCLASSEXTEND = "native nativesdk"
