SUMMARY = "Tool to edit rpath in ELF binaries"
DESCRIPTION = "chrpath allows you to change the rpath (where the \
application looks for libraries) in an application. It does not \
(yet) allow you to add an rpath if there isn't one already."
HOMEPAGE = "https://tracker.debian.org/pkg/chrpath"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

SRC_URI = "${DEBIAN_MIRROR}/main/c/${BPN}/${BPN}_${PV}.orig.tar.gz \
           file://standarddoc.patch"

SRC_URI[sha256sum] = "f09c49f0618660ca11fc6d9580ddde904c7224d4c6d0f6f2d1f9bcdc9102c9aa"

inherit autotools
S = "${WORKDIR}/chrpath"

# We don't have a staged chrpath-native for ensuring our binary is
# relocatable, so use the one we've just built
CHRPATH_BIN:class-native = "${B}/chrpath"

PROVIDES:append:class-native = " chrpath-replacement-native"
NATIVE_PACKAGE_PATH_SUFFIX = "/${PN}"

BBCLASSEXTEND = "native nativesdk"
