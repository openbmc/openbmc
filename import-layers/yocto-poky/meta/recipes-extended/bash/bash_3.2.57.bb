require bash.inc

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=fd5d9bcabd8ed5a54a01ce8d183d592a"

SRC_URI = "${GNU_MIRROR}/${BPN}/${BP}.tar.gz \
           file://mkbuiltins_have_stringize.patch \
           file://build-tests.patch \
           file://test-output.patch \
           file://run-ptest \
           file://dont-include-target-CFLAGS-in-host-LDFLAGS.patch \
           file://string-format.patch \
          "

SRC_URI[md5sum] = "237a8767c990b43ae2c89895c2dbc062"
SRC_URI[sha256sum] = "3fa9daf85ebf35068f090ce51283ddeeb3c75eb5bc70b1a4a7cb05868bfe06a4"

PARALLEL_MAKE = ""
