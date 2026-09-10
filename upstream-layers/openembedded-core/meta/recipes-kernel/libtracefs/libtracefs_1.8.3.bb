SUMMARY = "Library to access the kernel tracefs directory"
HOMEPAGE = "https://git.kernel.org/pub/scm/libs/libtrace/libtracefs.git/"
LICENSE = "GPL-2.0-or-later AND LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://LICENSES/GPL-2.0;md5=e6a75371ba4d16749254a51215d13f97 \
                    file://LICENSES/LGPL-2.1;md5=b370887980db5dd40659b50909238dbd"
SECTION = "libs"
DEPENDS = "libtraceevent bison-native flex-native"

SRCREV = "6fad6a14ba0d4c4b437d9e4eed7098d4bb07b4fc"
SRC_URI = "git://git.kernel.org/pub/scm/libs/libtrace/libtracefs.git;branch=${BPN};protocol=https"

PACKAGECONFIG ??= ""
PACKAGECONFIG[doc] = ",-Ddoc=false,asciidoc-native xmlto-native"
PACKAGECONFIG[samples] = ",-Dsamples=false"
PACKAGECONFIG[utest] = ",-Dutest=false,cunit"

EXTRA_OEMESON = "--bindir=${sbindir}"

inherit meson pkgconfig bash-completion

