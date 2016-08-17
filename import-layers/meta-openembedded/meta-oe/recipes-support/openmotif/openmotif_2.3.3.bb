SECTION = "libs"
SUMMARY = "OSM/Motif implementation"
LICENSE = "OGPL"
DEPENDS = "xbitmaps virtual/libx11 libxt libxft xproto"

LIC_FILES_CHKSUM = "file://LICENSE;md5=14f692c82491db3d52419929d2f3b343"

PR = "r3"

PNBLACKLIST[openmotif] ?= "BROKEN: doesn't build with B!=S"

SRC_URI = "http://motif.ics.com/sites/default/files/openmotif-2.3.3.tar.gz \
           file://configure.patch;patch=1"

SRC_URI[md5sum] = "fd27cd3369d6c7d5ef79eccba524f7be"
SRC_URI[sha256sum] = "c85f5545e218fa0c59a3789192132d472fc5a33e914a221a568eee4fc10cd103"

inherit autotools

PACKAGECONFIG ??= ""
PACKAGECONFIG[jpeg] = "--enable-jpeg,--disable-jpeg,jpeg"
PACKAGECONFIG[png] = "--enable-png,--disable-png,libpng"

EXTRA_OECONF = "X_CFLAGS=-I${STAGING_INCDIR} --disable-printing"

PACKAGES += "${PN}-bin"

FILES_${PN}-bin = "${bindir}"

do_compile() {
    (
        # HACK: build a native binaries need during the build
        unset CC LD CXX CCLD CFLAGS
        oe_runmake -C config/util CC="${BUILD_CC}" LD="${BUILD_LD}" CXX="${BUILD_CXX}" LIBS="" makestrs
    )
    if [ "$?" != "0" ]; then
        exit 1
    fi
    oe_runmake -C lib
    oe_runmake -C include
}

do_install() {
    oe_runmake DESTDIR=${D} -C lib install
    oe_runmake DESTDIR=${D} -C include install
}

LEAD_SONAME = "libXm.so.4"
