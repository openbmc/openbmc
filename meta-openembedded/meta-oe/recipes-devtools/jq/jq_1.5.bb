SUMMARY = "Lightweight and flexible command-line JSON processor"
DESCRIPTION = "jq is like sed for JSON data, you can use it to slice and \
               filter and map and transform structured data with the same \
               ease that sed, awk, grep and friends let you play with text."
HOMEPAGE = "https://stedolan.github.io/jq/"
BUGTRACKER = "https://github.com/stedolan/jq/issues"
SECTION = "utils"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=29dd0c35d7e391bb8d515eacf7592e00"

SRC_URI = "https://github.com/stedolan/${BPN}/releases/download/${BP}/${BP}.tar.gz \
           file://Support-without-oniguruma.patch \
"

SRC_URI[md5sum] = "0933532b086bd8b6a41c1b162b1731f9"
SRC_URI[sha256sum] = "c4d2bfec6436341113419debf479d833692cc5cdab7eb0326b5a4d4fbe9f493c"

inherit autotools

PACKAGECONFIG ?= "oniguruma"

PACKAGECONFIG[docs] = "--enable-docs,--disable-docs,ruby-native"
PACKAGECONFIG[maintainer-mode] = "--enable-maintainer-mode,--disable-maintainer-mode,flex-native bison-native"
PACKAGECONFIG[oniguruma] = "--with-oniguruma,--without-oniguruma,onig"

OE_EXTRACONF += " \
    --disable-valgrind \
"

BBCLASSEXTEND = "native"
