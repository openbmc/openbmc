# Version 5 of the Berkeley DB from Sleepycat
#
# At present this package only installs the DB code
# itself (shared libraries, .a in the dev package),
# documentation and headers.
#
# The headers have the same names as those as v3
# of the DB, only one version can be used *for dev*
# at once - DB3 and DB5 can both be installed on the
# same system at the same time if really necessary.
SECTION = "libs"
SUMMARY = "Berkeley Database v5"
DESCRIPTION = "Provides the foundational storage services for your application, no matter how demanding and unique your requirements may seem to be"
HOMEPAGE = "https://www.oracle.com/database/technologies/related/berkeleydb.html"
LICENSE = "Sleepycat"
RCONFLICTS:${PN} = "db3"

CVE_PRODUCT = "oracle_berkeley_db berkeley_db"
CVE_VERSION = "11.2.${PV}"

PE = "1"

SRC_URI = "https://download.oracle.com/berkeley-db/db-${PV}.tar.gz"
SRC_URI += "file://fix-parallel-build.patch \
            file://0001-atomic-Rename-local-__atomic_compare_exchange-to-avo.patch \
            file://0001-configure-Add-explicit-tag-options-to-libtool-invoca.patch \
            file://sequence-type.patch \
            file://0001-Fix-libc-compatibility-by-renaming-atomic_init-API.patch \
            file://0001-clock-Do-not-define-own-timespec.patch \
           "
# We are not interested in official latest 6.x versions;
# let's track what debian is using.
UPSTREAM_CHECK_URI = "${DEBIAN_MIRROR}/main/d/db5.3/"
UPSTREAM_CHECK_REGEX = "db5\.3_(?P<pver>\d+(\.\d+)+).+\.orig"

SRC_URI[md5sum] = "b99454564d5b4479750567031d66fe24"
SRC_URI[sha256sum] = "e0a992d740709892e81f9d93f06daf305cf73fb81b545afe72478043172c3628"

LIC_FILES_CHKSUM = "file://LICENSE;md5=ed1158e31437f4f87cdd4ab2b8613955"

inherit autotools

# The executables go in a separate package - typically there
# is no need to install these unless doing real database
# management on the system.
inherit lib_package

PACKAGES =+ "${PN}-cxx"
FILES:${PN}-cxx = "${libdir}/*cxx*so"

# The dev package has the .so link (as in db3) and the .a's -
# it is therefore incompatible (cannot be installed at the
# same time) as the db3 package
# sort out the .so since they do version prior to the .so
SOLIBS = "-5*.so"
FILES_SOLIBSDEV = "${libdir}/libdb.so ${libdir}/libdb_cxx.so"

#configuration - set in local.conf to override
# All the --disable-* options replace --enable-smallbuild, which breaks a bunch of stuff (eg. postfix)
DB5_CONFIG ?= "--enable-o_direct --disable-cryptography --disable-queue --disable-replication --disable-compat185 --disable-sql"

EXTRA_OECONF = "${DB5_CONFIG} --enable-shared --enable-cxx --with-sysroot STRIP=true"

PACKAGECONFIG ??= ""
PACKAGECONFIG[verify] = "--enable-verify, --disable-verify"
PACKAGECONFIG[dbm] = "--enable-dbm,--disable-dbm,"

EXTRA_AUTORECONF += "--exclude=autoheader  -I ${S}/dist/aclocal -I${S}/dist/aclocal_java"
AUTOTOOLS_SCRIPT_PATH = "${S}/dist"

# Cancel the site stuff - it's set for db3 and destroys the
# configure.
CONFIG_SITE = ""

oe_runconf:prepend() {
	. ${S}/dist/RELEASE
	# Edit version information we couldn't pre-compute.
	sed -i -e "s/__EDIT_DB_VERSION_FAMILY__/$DB_VERSION_FAMILY/g" \
	    -e "s/__EDIT_DB_VERSION_RELEASE__/$DB_VERSION_RELEASE/g" \
	    -e "s/__EDIT_DB_VERSION_MAJOR__/$DB_VERSION_MAJOR/g" \
	    -e "s/__EDIT_DB_VERSION_MINOR__/$DB_VERSION_MINOR/g" \
	    -e "s/__EDIT_DB_VERSION_PATCH__/$DB_VERSION_PATCH/g" \
	    -e "s/__EDIT_DB_VERSION_STRING__/$DB_VERSION_STRING/g" \
	    -e "s/__EDIT_DB_VERSION_FULL_STRING__/$DB_VERSION_FULL_STRING/g" \
	    -e "s/__EDIT_DB_VERSION_UNIQUE_NAME__/$DB_VERSION_UNIQUE_NAME/g" \
	    -e "s/__EDIT_DB_VERSION__/$DB_VERSION/g" ${S}/dist/configure
}

do_compile:prepend() {
    # Stop libtool adding RPATHs
    sed -i -e 's|hardcode_into_libs=yes|hardcode_into_libs=no|' ${B}/libtool
}

do_install:append() {
	mkdir -p ${D}/${includedir}/db51
	mv ${D}/${includedir}/db.h ${D}/${includedir}/db51/.
	mv ${D}/${includedir}/db_cxx.h ${D}/${includedir}/db51/.
	ln -s db51/db.h ${D}/${includedir}/db.h
	ln -s db51/db_cxx.h ${D}/${includedir}/db_cxx.h

	# The docs end up in /usr/docs - not right.
	if test -d "${D}/${prefix}/docs"
	then
		mkdir -p "${D}/${datadir}"
		test ! -d "${D}/${docdir}" || rm -rf "${D}/${docdir}"
		mv "${D}/${prefix}/docs" "${D}/${docdir}"
	fi

	chown -R root:root ${D}
	if ${@bb.utils.contains('PACKAGECONFIG', 'verify', 'false', 'true', d)}; then
		rm -f ${D}${bindir}/db_verify
	fi
}

INSANE_SKIP:${PN} = "dev-so"
INSANE_SKIP:${PN}-cxx = "dev-so"

BBCLASSEXTEND = "native nativesdk"

# many configure tests are failing with gcc-14
CFLAGS += "-Wno-error=implicit-int -Wno-error=implicit-function-declaration"
BUILD_CFLAGS += "-Wno-error=implicit-int -Wno-error=implicit-function-declaration"
