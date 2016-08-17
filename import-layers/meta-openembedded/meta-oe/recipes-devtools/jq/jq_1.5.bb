SUMMARY = "Lightweight and flexible command-line JSON processor"
DESCRIPTION = "jq is like sed for JSON data, you can use it to slice and \
               filter and map and transform structured data with the same \
               ease that sed, awk, grep and friends let you play with text."
HOMEPAGE = "http://stedolan.github.io/jq/"
BUGTRACKER = "https://github.com/stedolan/jq/issues"
SECTION = "utils"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=29dd0c35d7e391bb8d515eacf7592e00"

DEPENDS = "flex-native bison-native onig"

SRC_URI = "https://github.com/stedolan/${BPN}/releases/download/${BPN}-${PV}/${BPN}-${PV}.tar.gz \
"

SRC_URI[md5sum] = "0933532b086bd8b6a41c1b162b1731f9"
SRC_URI[sha256sum] = "c4d2bfec6436341113419debf479d833692cc5cdab7eb0326b5a4d4fbe9f493c"

inherit autotools

# Don't build documentation (generation requires ruby)
EXTRA_OECONF = "--disable-docs --disable-maintainer-mode"
