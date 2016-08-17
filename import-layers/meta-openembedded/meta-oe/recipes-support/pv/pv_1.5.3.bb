SUMMARY = "Terminal-based tool for monitoring the progress of data through a pipeline"

LICENSE = "Artistic-2.0"
LIC_FILES_CHKSUM = "file://doc/COPYING;md5=9c50db2589ee3ef10a9b7b2e50ce1d02"

SRC_URI = "http://www.ivarch.com/programs/sources/pv-${PV}.tar.bz2"
SRC_URI[md5sum] = "efe8e9e4cad5f3264a32258a63bf2c8e"
SRC_URI[sha256sum] = "76f3999b1c3b3027163dce6ef667cdf8dafb75218ee25e54a03bfe590478f90e"

inherit autotools

