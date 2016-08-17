SUMMARY = "FBreader is an ebook reader"
HOMEPAGE = "http://www.fbreader.org"
SECTION = "x11/utils"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://fbreader/LICENSE;md5=94d55d512a9ba36caa9b7df079bae19f"
DEPENDS = "gtk+ enca expat bzip2 libgpewidget virtual/libiconv liblinebreak libfribidi curl sqlite3"
SRCREV = "2cf1ec0e306e1122dbed850bfa005cd59a6168ee"
PV = "0.99.5+gitr${SRCPV}"

PR = "r1"

DEFAULT_PREFERENCE = "-1"

SRC_URI = "git://github.com/geometer/FBReader.git;protocol=http;branch=master \
           file://0001-Fix-installation-of-the-icons-when-RESOLUTION-is-set.patch"

# Set the defaults
READER_RESOLUTION ?= "1024x600"
READER_ARCH       ?= "desktop"
READER_UI         ?= "gtk"
READER_STATUS     ?= "release"

FILES_${PN} += "${datadir}/FBReader ${datadir}/zlibrary ${libdir}/zlibrary"
FILES_${PN}-dbg += "${libdir}/zlibrary/ui/.debug/"

CFLAGS_append = " RESOLUTION=${READER_RESOLUTION} INSTALLDIR=${prefix}"
EXTRA_OEMAKE = "CC='${CXX}' LD='${CXX}' INCPATH='${STAGING_INCDIR}' LIBPATH='${STAGING_LIBDIR}'"
inherit pkgconfig

S = "${WORKDIR}/git"

do_configure() {
    cd "${S}"

    echo "TARGET_ARCH = ${READER_ARCH}" > makefiles/target.mk
    echo "UI_TYPE = ${READER_UI}" >> makefiles/target.mk
    echo "TARGET_STATUS = ${READER_STATUS}" >> makefiles/target.mk
}

do_install() {
    oe_runmake install DESTDIR=${D} RESOLUTION=${READER_RESOLUTION}
}
