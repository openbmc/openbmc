SUMMARY = "Key/value database library with extensible hashing"
HOMEPAGE = "http://www.gnu.org/software/gdbm/"
SECTION = "libs"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=241da1b9fe42e642cbb2c24d5e0c4d24"


SRC_URI = "${GNU_MIRROR}/gdbm/gdbm-${PV}.tar.gz \
           file://run-ptest \
           file://ptest.patch \
          "

SRC_URI[md5sum] = "8929dcda2a8de3fd2367bdbf66769376"
SRC_URI[sha256sum] = "9d252cbd7d793f7b12bcceaddda98d257c14f4d1890d851c386c37207000a253"

inherit autotools gettext texinfo lib_package ptest

# Needed for dbm python module
EXTRA_OECONF = "-enable-libgdbm-compat"

# Stop presence of dbm/nbdm on the host contaminating builds
CACHED_CONFIGUREVARS += "ac_cv_lib_ndbm_main=no ac_cv_lib_dbm_main=no"

BBCLASSEXTEND = "native nativesdk"

do_install_append () {
    # Create a symlink to ndbm.h and gdbm.h in include/gdbm to let other packages to find
    # these headers
    install -d ${D}${includedir}/gdbm
    ln -sf ../ndbm.h ${D}/${includedir}/gdbm/ndbm.h
    ln -sf ../gdbm.h ${D}/${includedir}/gdbm/gdbm.h
}

RDEPENDS_${PN}-ptest += "diffutils"

do_compile_ptest() {
    oe_runmake -C tests buildtests
}

PACKAGES =+ "${PN}-compat \
            "
FILES_${PN}-compat = "${libdir}/libgdbm_compat${SOLIBS} \
                     "
