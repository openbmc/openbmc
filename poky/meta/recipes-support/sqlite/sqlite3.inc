SUMMARY = "Embeddable SQL database engine"
HOMEPAGE = "http://www.sqlite.org"
SECTION = "libs"

PE = "3"

def sqlite_download_version(d):
    pvsplit = d.getVar('PV').split('.')
    if len(pvsplit) < 4:
        pvsplit.append('0')
    return pvsplit[0] + ''.join([part.rjust(2,'0') for part in pvsplit[1:]])

SQLITE_PV = "${@sqlite_download_version(d)}"

S = "${WORKDIR}/sqlite-autoconf-${SQLITE_PV}"

UPSTREAM_CHECK_URI = "http://www.sqlite.org/"
UPSTREAM_CHECK_REGEX = "releaselog/(?P<pver>(\d+[\.\-_]*)+)\.html"

CVE_PRODUCT = "sqlite"

inherit autotools pkgconfig

PACKAGECONFIG ?= ""
PACKAGECONFIG_class-native = ""

PACKAGECONFIG[editline] = "--enable-editline,--disable-editline,libedit"
PACKAGECONFIG[readline] = "--enable-readline,--disable-readline,readline ncurses"

EXTRA_OECONF = " \
    --enable-shared \
    --enable-threadsafe \
    --disable-static-shell \
"

CFLAGS_append = " -fPIC"

# pread() is in POSIX.1-2001 so any reasonable system must surely support it
CFLAGS += "-DUSE_PREAD"

# Provide column meta-data API
CFLAGS += "-DSQLITE_ENABLE_COLUMN_METADATA"

PACKAGES = "lib${BPN} lib${BPN}-dev lib${BPN}-doc ${PN}-dbg lib${BPN}-staticdev ${PN}"

FILES_${PN} = "${bindir}/*"
FILES_lib${BPN} = "${libdir}/*.so.*"
FILES_lib${BPN}-dev = "${libdir}/*.la ${libdir}/*.so \
                       ${libdir}/pkgconfig ${includedir}"
FILES_lib${BPN}-doc = "${docdir} ${mandir} ${infodir}"
FILES_lib${BPN}-staticdev = "${libdir}/lib*.a"

AUTO_LIBNAME_PKGS = "${MLPREFIX}lib${BPN}"

BBCLASSEXTEND = "native nativesdk"
