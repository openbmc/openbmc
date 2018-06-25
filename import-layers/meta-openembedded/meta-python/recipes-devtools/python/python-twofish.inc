SUMMARY = "Bindings for the Twofish implementation by Niels Ferguson"
DESCRIPTION = "Bindings for the Twofish implementation by Niels Ferguson\
 libtwofish-dev."
HOMEPAGE = "http://github.com/keybase/python-twofish"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=33a63abf6d7567b1689d8ce69f00e43b"

SRC_URI[md5sum] = "d7d22f16dc4ffa0e3ae2200654033abe"
SRC_URI[sha256sum] = "b09d8bb50d33b23ff34cafb1f9209f858f752935c6a5c901efb92a41acb830fa"

inherit pypi

BBCLASSEXTEND = "native nativesdk"
