SUMMARY = "Hardware Error Isolator for POWER Systems"

DESCRIPTION = \
    "The library provides a set of tools to isolate hardware attentions driven \
    by POWER Systems chip (processor chips, memory chips, etc.)."

HOMEPAGE = "https://github.com/openbmc/openpower-libhei"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

include openpower-libhei-rev.inc

S = "${WORKDIR}/git"

inherit pkgconfig meson
inherit perlnative

DEPENDS = "libxml2-native libxml-simple-perl-native libjson-perl-native"

# Don't build CI tests
EXTRA_OEMESON = "-Dtests=disabled"

