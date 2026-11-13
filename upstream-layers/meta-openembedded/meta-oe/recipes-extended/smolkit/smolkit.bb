SUMMARY = "low-footprint polkit replacement"
HOMEPAGE = "https://github.com/bmwcarit/smolkit"
DESCRIPTION = "\
  Service that implements a subset of polkit's D-Bus interface with a static configuration language. \
"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

inherit cmake pkgconfig

DEPENDS = "\
  rapidjson \
  sdbus-c++ \
"

# Only claim polkit when selected, universe builds both recipes otherwise and
# bitbake reports that multiple .bb files provide polkit.
PROVIDES += "${@'polkit' if d.getVar('PREFERRED_PROVIDER_polkit') == 'smolkit' else ''}"
RPROVIDES:${PN} = "polkit"

VERSION = "0.3.0"
PV = "${VERSION}+git"

SRC_URI = "\
    git://github.com/bmwcarit/${BPN}.git;protocol=https;branch=main;tag=v${VERSION} \
    file://0001-SystemUserLookup.h-include-sys-types.h-for-uid_t-pi.patch \
"
SRCREV = "46bbfd5c922a8d03a534398ee129ded822dfcafe"
