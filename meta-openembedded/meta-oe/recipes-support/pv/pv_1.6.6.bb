SUMMARY = "Terminal-based tool for monitoring the progress of data through a pipeline"

LICENSE = "Artistic-2.0"
LIC_FILES_CHKSUM = "file://doc/COPYING;md5=9c50db2589ee3ef10a9b7b2e50ce1d02"

SRC_URI = "http://www.ivarch.com/programs/sources/${BP}.tar.bz2"
SRC_URI[md5sum] = "ff3564fddcc2b9bd4a9c1d143aba4b4c"
SRC_URI[sha256sum] = "608ef935f7a377e1439c181c4fc188d247da10d51a19ef79bcdee5043b0973f1"

UPSTREAM_CHECK_URI = "http://www.ivarch.com/programs/pv.shtml"
UPSTREAM_CHECK_REGEX = "pv-(?P<pver>\d+(\.\d+)+).tar.bz2"

inherit autotools

LDEMULATION_mipsarchn32 = "${@bb.utils.contains('TUNE_FEATURES', 'bigendian', 'elf32btsmipn32', 'elf32ltsmipn32', d)}"
export LDEMULATION
