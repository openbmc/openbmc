#
# Copyright (C) 2012 - 2016 Wind River Systems, Inc.
#
SUMMARY = "A modern implementation of DBM"

DESCRIPTION = "Tokyo Cabinet is a library of routines for managing a database. \
The database is a simple data file containing records, each is a pair of a key \
and a value. Every key and value is serial bytes with variable length. \
Both binary data and character string can be used as a key and a value. \
There is neither concept of data tables nor data types. \
Records are organized in hash table, B+ tree, or fixed-length array."

HOMEPAGE = "http://fallabs.com/tokyocabinet/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=7fbc338309ac38fefcd64b04bb903e34"

SRC_URI = "http://fallabs.com/tokyocabinet/${BP}.tar.gz \
           file://remove-hard-coded-include-and-lib-paths.patch \
"

SRC_URI[md5sum] = "fd03df6965f8f56dd5b8518ca43b4f5e"
SRC_URI[sha256sum] = "a003f47c39a91e22d76bc4fe68b9b3de0f38851b160bbb1ca07a4f6441de1f90"

DEPENDS = "bzip2 zlib"

inherit autotools-brokensep
