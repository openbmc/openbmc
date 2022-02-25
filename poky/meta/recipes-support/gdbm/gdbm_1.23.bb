SUMMARY = "Key/value database library with extensible hashing"
DESCRIPTION = "Library of database functions that use extensible hashing \
and work similar to the standard UNIX dbm. These routines are provided \
to a programmer needing to create and manipulate a hashed database."
HOMEPAGE = "http://www.gnu.org/software/gdbm/"
SECTION = "libs"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=241da1b9fe42e642cbb2c24d5e0c4d24"


SRC_URI = "${GNU_MIRROR}/gdbm/gdbm-${PV}.tar.gz \
           file://run-ptest \
           file://ptest.patch \
          "

SRC_URI[sha256sum] = "74b1081d21fff13ae4bd7c16e5d6e504a4c26f7cde1dca0d963a484174bbcacd"

inherit autotools gettext texinfo lib_package ptest

# Needed for dbm python module
EXTRA_OECONF = "--enable-libgdbm-compat --without-readline"

# Stop presence of dbm/nbdm on the host contaminating builds
CACHED_CONFIGUREVARS += "ac_cv_lib_ndbm_main=no ac_cv_lib_dbm_main=no"

BBCLASSEXTEND = "native nativesdk"

do_install:append () {
    # Create a symlink to ndbm.h and gdbm.h in include/gdbm to let other packages to find
    # these headers
    install -d ${D}${includedir}/gdbm
    ln -sf ../ndbm.h ${D}/${includedir}/gdbm/ndbm.h
    ln -sf ../gdbm.h ${D}/${includedir}/gdbm/gdbm.h
}

RDEPENDS:${PN}-ptest += "diffutils ${PN}-bin"

do_compile_ptest() {
    oe_runmake -C tests buildtests
}

PACKAGES =+ "${PN}-compat \
            "
FILES:${PN}-compat = "${libdir}/libgdbm_compat${SOLIBS} \
                     "
