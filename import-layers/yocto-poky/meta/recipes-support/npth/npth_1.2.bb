SUMMARY = "New GNU Portable Threads library"
HOMEPAGE = "http://www.gnupg.org/software/pth/"
SECTION = "libs"
LICENSE = "LGPLv3+ & GPLv2+"
LIC_FILES_CHKSUM = "\
    file://COPYING;md5=751419260aa954499f7abaabaa882bbe\
    file://COPYING.LESSER;md5=6a6a8e020838b23406c81b19c1d46df6\
    "
SRC_URI = "ftp://ftp.gnupg.org/gcrypt/npth/npth-${PV}.tar.bz2 \
           file://pkgconfig.patch \
          "

SRC_URI[md5sum] = "226bac7374b9466c6ec26b1c34dab844"
SRC_URI[sha256sum] = "6ddbdddb2cf49a4723f9d1ad6563c480d6760dcb63cb7726b8fc3bc2e1b6c08a"

BINCONFIG = "${bindir}/npth-config"

inherit autotools binconfig-disabled

FILES_${PN} = "${libdir}/libnpth.so.*"
FILES_${PN}-dev += "${bindir}/npth-config"
