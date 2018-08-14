SUMMARY = "Rdfind is a program that finds duplicate files"
HOMEPAGE = "https://rdfind.pauldreik.se/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=9ac2e7cff1ddaf48b6eab6028f23ef88"

DEPENDS = "nettle"

SRC_URI = "https://rdfind.pauldreik.se/${BP}.tar.gz \
           file://reproducible_build.patch \
"

SRC_URI[md5sum] = "97c0cb35933588413583c61d3b5f9adf"
SRC_URI[sha256sum] = "a5f0b3f72093d927b93898c993479b35682cccb47f7393fb72bd4803212fcc7d"

inherit autotools

BBCLASSEXTEND = "native"
