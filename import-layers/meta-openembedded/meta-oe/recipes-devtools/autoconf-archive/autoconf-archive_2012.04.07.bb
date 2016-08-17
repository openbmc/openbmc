require autoconf-archive.inc


PARALLEL_MAKE = ""

LICENSE = "GPLv2 & GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI[md5sum] = "e842c5b9fae021007bd70550362e5e80"
SRC_URI[sha256sum] = "040b443bf68efd52fbfcb294b556bfbbbfe432db78445ca25e0cfe2e88f96a14"

EXTRA_OECONF += "ac_cv_path_M4=m4"
BBCLASSEXTEND = "native nativesdk"
