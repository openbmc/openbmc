SUMMARY = "A Type1 Font Rastering Library"
SECTION = "libs"
DEPENDS = "virtual/libx11 libxaw"

LICENSE = "LGPLv2 & GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8ca43cbc842c2336e835926c2166c28b \
                    file://LGPL;md5=6e29c688d912da12b66b73e32b03d812 \
"

SRC_URI = "${DEBIAN_MIRROR}/main/t/t1lib/t1lib_${PV}.orig.tar.gz \
           file://configure.patch \
           file://libtool.patch \
           file://format_security.patch"
SRC_URI[md5sum] = "a5629b56b93134377718009df1435f3c"
SRC_URI[sha256sum] = "821328b5054f7890a0d0cd2f52825270705df3641dbd476d58d17e56ed957b59"

inherit autotools-brokensep distro_features_check
# depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

# Fix GNU_HASH problem
TARGET_CC_ARCH += "${LDFLAGS}"

EXTRA_OECONF = "--with-x --without-athena"
EXTRA_OEMAKE = "without_doc"

FILES_${PN} += " ${datadir}/t1lib/t1lib.config"
FILES_${PN}-doc = "${datadir}/t1lib/doc/t1lib_doc.pdf"

