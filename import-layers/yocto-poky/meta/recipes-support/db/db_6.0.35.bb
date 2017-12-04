# Version 6 of the Berkeley DB from Oracle (formerly Sleepycat)
#
# At present this package only installs the DB code
# itself (shared libraries, .a in the dev package),
# documentation and headers.
#
# The headers have the same names as those as v3
# of the DB, only one version can be used *for dev*
# at once - DB3 and DB6 can both be installed on the
# same system at the same time if really necessary.
SECTION = "libs"
SUMMARY = "Berkeley Database v6"
HOMEPAGE = "http://www.oracle.com/technetwork/database/database-technologies/berkeleydb/overview/index.html"
LICENSE = "AGPL-3.0"
VIRTUAL_NAME ?= "virtual/db"
RCONFLICTS_${PN} = "db3"

PR = "r1"

# Note, when upgraded to 6.1.x, a patch in RPM will need to be removed to activate db 6.1 support.
SRC_URI = "http://distfiles.gentoo.org/distfiles/db-${PV}.tar.gz \
           file://arm-thumb-mutex_db5.patch \
           file://fix-parallel-build.patch \
           file://Makefile-let-libso_target-depend-on-bt_rec.patch \
           file://Makefile-let-libdb-6.0.la-depend-os_map.l.patch \
           "

SRC_URI[md5sum] = "c65a4d3e930a116abaaf69edfc697f25"
SRC_URI[sha256sum] = "24421affa8ae436fe427ae4f5f2d1634da83d3d55a5ad6354a98eeedb825de55"

# Exclude NC versions which lack AES encryption
UPSTREAM_CHECK_REGEX = "db-(?P<pver>\d+\.\d+(\.\d+)?).tar"
UPSTREAM_CHECK_URI = "http://www.oracle.com/technetwork/products/berkeleydb/downloads/index-082944.html"

LIC_FILES_CHKSUM = "file://LICENSE;md5=1ec8b0b17cc31513fe35ab10716f8490"

# Put virtual/db in any appropriate provider of a
# relational database, use it as a dependency in
# place of a specific db and use:
#
# PREFERRED_PROVIDER_virtual/db
#
# to select the correct db in the build (distro) .conf
PROVIDES += "${VIRTUAL_NAME}"

inherit autotools

# The executables go in a separate package - typically there
# is no need to install these unless doing real database
# management on the system.
inherit lib_package

PACKAGES =+ "${PN}-cxx"
FILES_${PN}-cxx = "${libdir}/*cxx*so"

# The dev package has the .so link (as in db3) and the .a's -
# it is therefore incompatible (cannot be installed at the
# same time) as the db3 package
# sort out the .so since they do version prior to the .so
SOLIBS = "-6*.so"
FILES_SOLIBSDEV = "${libdir}/libdb.so ${libdir}/libdb_cxx.so"

#configuration - set in local.conf to override
# All the --disable-* options replace --enable-smallbuild, which breaks a bunch of stuff (eg. postfix)
DB6_CONFIG ?= "--enable-o_direct --disable-cryptography --disable-queue --disable-replication --disable-verify --disable-compat185 --disable-sql"

EXTRA_OECONF = "${DB6_CONFIG} --enable-shared --enable-cxx --with-sysroot"

# Cancel the site stuff - it's set for db3 and destroys the configure.
CONFIG_SITE = ""

AUTOTOOLS_SCRIPT_PATH = "${S}/dist"

do_configure() {
	cd ${B}
	gnu-configize --force ${AUTOTOOLS_SCRIPT_PATH}
	oe_runconf
}

EXTRA_OEMAKE_append_class-target = " LIBTOOL=${STAGING_BINDIR_CROSS}/${HOST_SYS}-libtool"
EXTRA_OEMAKE += "STRIP=true"

do_compile_prepend() {
	# Stop libtool adding RPATHs
	sed -i -e 's|hardcode_into_libs=yes|hardcode_into_libs=no|' ${B}/libtool
}

do_install_append() {
	mkdir -p ${D}/${includedir}/db60
	mv ${D}/${includedir}/db.h ${D}/${includedir}/db60/.
	mv ${D}/${includedir}/db_cxx.h ${D}/${includedir}/db60/.
	ln -s db60/db.h ${D}/${includedir}/db.h
	ln -s db60/db_cxx.h ${D}/${includedir}/db_cxx.h

	# The docs end up in /usr/docs - not right.
	if test -d "${D}/${prefix}/docs"
	then
		mkdir -p "${D}/${datadir}"
		test ! -d "${D}/${docdir}" || rm -rf "${D}/${docdir}"
		mv "${D}/${prefix}/docs" "${D}/${docdir}"
	fi

	chown -R root:root ${D}
}

# libdb isn't versioned in the typical manner (libdb-6.0.so is the full library
# name) so skip these checks.
INSANE_SKIP_${PN} = "dev-so"
INSANE_SKIP_${PN}-cxx = "dev-so"

BBCLASSEXTEND = "native nativesdk"

RECIPE_NO_UPDATE_REASON = "latest db does not work with RPM-5.x due to changes in locking semantics"
