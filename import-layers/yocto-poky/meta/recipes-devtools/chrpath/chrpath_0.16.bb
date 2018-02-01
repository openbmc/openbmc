SUMMARY = "Tool to edit rpath in ELF binaries"
DESCRIPTION = "chrpath allows you to change the rpath (where the \
application looks for libraries) in an application. It does not \
(yet) allow you to add an rpath if there isn't one already."
HOMEPAGE = "http://alioth.debian.org/projects/chrpath/"
BUGTRACKER = "http://alioth.debian.org/tracker/?atid=412807&group_id=31052"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

SRC_URI = "https://alioth.debian.org/frs/download.php/file/3979/chrpath-0.16.tar.gz \
           file://standarddoc.patch"

SRC_URI[md5sum] = "2bf8d1d1ee345fc8a7915576f5649982"
SRC_URI[sha256sum] = "bb0d4c54bac2990e1bdf8132f2c9477ae752859d523e141e72b3b11a12c26e7b"

UPSTREAM_CHECK_URI = "http://alioth.debian.org/frs/?group_id=31052"

inherit autotools

# We don't have a staged chrpath-native for ensuring our binary is
# relocatable, so use the one we've just built
CHRPATH_BIN_class-native = "${B}/chrpath"

PROVIDES_append_class-native = " chrpath-replacement-native"
NATIVE_PACKAGE_PATH_SUFFIX = "/${PN}"

BBCLASSEXTEND = "native nativesdk"
