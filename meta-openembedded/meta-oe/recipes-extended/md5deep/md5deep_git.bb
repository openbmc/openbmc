SUMMARY = "md5deep and hashdeep to compute and audit hashsets of amounts of files."
DESCRIPTION = "md5deep is a set of programs to compute MD5, SHA-1, SHA-256, Tiger, or Whirlpool message digests on an arbitrary number of files. This package also includes hashdeep which is also able to audit hashsets."
HOMEPAGE = "http://md5deep.sourceforge.net"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=9190f660105b9a56cdb272309bfd5491"

PV = "4.4+git${SRCPV}"

SRCREV = "877613493ff44807888ce1928129574be393cbb0"

SRC_URI = "git://github.com/jessek/hashdeep.git;branch=master;protocol=https \
           file://wrong-variable-expansion.patch \
           file://0001-Fix-literal-and-identifier-spacing-as-dictated-by-C-.patch \
           "

S = "${WORKDIR}/git"

inherit autotools
