DESCRIPTION = "GNU gperf is a perfect hash function generator"
HOMEPAGE = "http://www.gnu.org/software/gperf"
SUMMARY  = "Generate a perfect hash function from a set of keywords"
LICENSE  = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://src/main.cc;beginline=8;endline=19;md5=ca1c43fa02be95aa2e10d567684e6fd5"

SRC_URI  = "${GNU_MIRROR}/${BPN}/${BP}.tar.gz"
SRC_URI[sha256sum] = "fd87e0aba7e43ae054837afd6cd4db03a3f2693deb3619085e6ed9d8d9604ad8"

inherit autotools

CFLAGS += "-std=gnu17"

BBCLASSEXTEND = "native nativesdk"
