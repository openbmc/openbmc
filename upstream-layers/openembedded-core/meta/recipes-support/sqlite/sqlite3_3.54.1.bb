SUMMARY = "Embeddable SQL database engine"
DESCRIPTION = "A library that implements a small, fast, self-contained, high-reliability, full-featured, SQL database engine. SQLite is the most used database engine in the world. SQLite is built into all mobile phones and most computers and comes bundled inside countless other applications that people use every day"
HOMEPAGE = "http://www.sqlite.org"
SECTION = "libs"

LICENSE = "blessing"
LIC_FILES_CHKSUM = "file://sqlite3.h;endline=11;md5=786d3dc581eff03f4fd9e4a77ed00c66"

PE = "3"

def sqlite_download_version(d):
    pvsplit = d.getVar('PV').split('.')
    if len(pvsplit) < 4:
        pvsplit.append('0')
    return pvsplit[0] + ''.join([part.rjust(2,'0') for part in pvsplit[1:]])

SQLITE_PV = "${@sqlite_download_version(d)}"

S = "${UNPACKDIR}/sqlite-autoconf-${SQLITE_PV}"
B = "${WORKDIR}/build"

SRC_URI = "http://www.sqlite.org/2026/sqlite-autoconf-${SQLITE_PV}.tar.gz"
SRC_URI += "file://0001-Add-option-to-disable-zlib.patch"

SRC_URI[sha256sum] = "926ef8d3eab93fbf6e06011901273eec076b25594a4e1cdadd50c987f4f6d64e"

UPSTREAM_CHECK_URI = "http://www.sqlite.org/"
UPSTREAM_CHECK_REGEX = "releaselog/(?P<pver>(\d+[\.\-_]*)+)\.html"

CVE_PRODUCT = "sqlite"

inherit pkgconfig siteinfo upstream-stable-release-point

PACKAGECONFIG ?= "fts4 fts5 rtree dyn_ext unlock-notify"
PACKAGECONFIG:class-native ?= "fts4 fts5 rtree dyn_ext"

PACKAGECONFIG[editline] = "--enable-editline --with-readline-header=${STAGING_INCDIR}/editline/readline.h,--disable-editline,libedit ncurses"
PACKAGECONFIG[readline] = "--enable-readline --with-readline-header=${STAGING_INCDIR}/readline/readline.h,--disable-readline,readline ncurses"
PACKAGECONFIG[fts3] = "--enable-fts3,--disable-fts3"
PACKAGECONFIG[fts4] = "--enable-fts4,--disable-fts4"
PACKAGECONFIG[fts5] = "--enable-fts5,--disable-fts5"
PACKAGECONFIG[rtree] = "--enable-rtree,--disable-rtree"
PACKAGECONFIG[session] = "--enable-session,--disable-session"
PACKAGECONFIG[zlib] = "--enable-zlib,--disable-zlib,zlib"
PACKAGECONFIG[dyn_ext] = "--enable-load-extension,--disable-load-extension"
PACKAGECONFIG[unlock-notify] = ""

EXTRA_OECONF = " \
    --enable-shared \
    --enable-threadsafe \
    --disable-static-shell \
    --disable-rpath \
    --soname=legacy \
"

# pread() is in POSIX.1-2001 so any reasonable system must surely support it
CFLAGS += "-DUSE_PREAD"

# Provide column meta-data API
CFLAGS += "-DSQLITE_ENABLE_COLUMN_METADATA"

# Provide the unlock_notify API
CFLAGS += "${@bb.utils.contains('PACKAGECONFIG', 'unlock-notify', '-DSQLITE_ENABLE_UNLOCK_NOTIFY', '', d)}"

# Unless SQLITE_BYTEORDER is predefined, the code falls back to build time
# heuristics, which are not always correct
CFLAGS += "${@oe.utils.conditional('SITEINFO_ENDIANNESS', 'le', '-DSQLITE_BYTEORDER=1234', '-DSQLITE_BYTEORDER=4321', d)}"

PACKAGES = "lib${BPN} lib${BPN}-dev lib${BPN}-doc ${PN}-dbg lib${BPN}-staticdev ${PN}"

FILES:${PN} = "${bindir}/*"
FILES:lib${BPN} = "${libdir}/*${SOLIBS}"
FILES:lib${BPN}-dev = "${libdir}/*.la ${libdir}/*${SOLIBSDEV} \
                       ${libdir}/pkgconfig ${includedir}"
FILES:lib${BPN}-doc = "${docdir} ${mandir} ${infodir}"
FILES:lib${BPN}-staticdev = "${libdir}/lib*.a"

AUTO_LIBNAME_PKGS = "${MLPREFIX}lib${BPN}"

do_configure() {
	${S}/configure \
		--build=${BUILD_SYS} \
		--host=${TARGET_SYS} \
		--prefix=${prefix} \
		--bindir=${bindir} \
		--libdir=${libdir} \
		--includedir=${includedir} \
		--mandir=${mandir} \
		${EXTRA_OECONF} \
		${PACKAGECONFIG_CONFARGS}
}
do_configure[cleandirs] = "${B}"

do_install() {
	oe_runmake DESTDIR=${D} install
}

BBCLASSEXTEND = "native nativesdk"
