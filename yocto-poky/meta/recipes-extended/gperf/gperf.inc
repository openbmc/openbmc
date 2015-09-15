DESCRIPTION = "GNU gperf is a perfect hash function generator"
HOMEPAGE = "http://www.gnu.org/software/gperf"
SUMMARY  = "Generate a perfect hash function from a set of keywords"
LICENSE  = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://src/main.cc;beginline=8;endline=19;md5=dec8f611845d047387ed56b5b85fa99b"

SRC_URI  = "${GNU_MIRROR}/gperf/gperf-${PV}.tar.gz"

inherit autotools

# autoreconf couldn't find acinclude.m4 when stepping into subdirectory. Instead of
# duplicating acinclude.m4 in every subdirectory, use absolute include path to aclocal
EXTRA_AUTORECONF += " -I ${S}"

do_configure_prepend() {
        if [ ! -e ${S}/acinclude.m4 ]; then
                cat ${S}/aclocal.m4 > ${S}/acinclude.m4
        fi
}

BBCLASSEXTEND = "native"
