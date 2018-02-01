SUMMARY = "Library for low-level interaction with nftables Netlink's API over libmnl"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=79808397c3355f163c012616125c9e26"
SECTION = "libs"
DEPENDS = "libmnl"
PV .= "+git${SRCPV}"
SRCREV = "4b89c0cb0883f638ff1abbc2ff47c43cdc26aac5"
SRC_URI = "git://git.netfilter.org/libnftnl \
           file://0001-Declare-the-define-visivility-attribute-together.patch \
           file://0001-avoid-naming-local-function-as-one-of-printf-family.patch \
           "
SRC_URI[md5sum] = "82183867168eb6644926c48b991b8aac"
SRC_URI[sha256sum] = "9bb66ecbc64b8508249402f0093829f44177770ad99f6042b86b3a467d963982"

S = "${WORKDIR}/git"

inherit autotools pkgconfig
