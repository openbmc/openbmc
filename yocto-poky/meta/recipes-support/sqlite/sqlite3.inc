SUMMARY = "Embeddable SQL database engine"
HOMEPAGE = "http://www.sqlite.org"
SECTION = "libs"
DEPENDS = "readline ncurses"
DEPENDS_class-native = ""
LICENSE = "PD"

inherit autotools pkgconfig

EXTRA_OECONF = "--enable-shared --enable-threadsafe"
EXTRA_OECONF_class-native = "--enable-shared --enable-threadsafe --disable-readline"
export config_BUILD_CC = "${BUILD_CC}"
export config_BUILD_CFLAGS = "${BUILD_CFLAGS}"
export config_BUILD_LIBS = "${BUILD_LDFLAGS}"
export config_TARGET_CC = "${CC}"
export config_TARGET_LINK = "${CCLD}"
export config_TARGET_CFLAGS = "${CFLAGS}"
export config_TARGET_LFLAGS = "${LDFLAGS}"

# pread() is in POSIX.1-2001 so any reasonable system must surely support it
BUILD_CFLAGS += "-DUSE_PREAD"
TARGET_CFLAGS += "-DUSE_PREAD"

PACKAGES = "lib${BPN} lib${BPN}-dev lib${BPN}-doc ${PN}-dbg lib${BPN}-staticdev ${PN}"

FILES_${PN} = "${bindir}/*"
FILES_lib${BPN} = "${libdir}/*.so.*"
FILES_lib${BPN}-dev = "${libdir}/*.la ${libdir}/*.so \
	                	   ${libdir}/pkgconfig ${includedir}"
FILES_lib${BPN}-doc = "${docdir} ${mandir} ${infodir}"
FILES_lib${BPN}-staticdev = "${libdir}/lib*.a"
AUTO_LIBNAME_PKGS = "${MLPREFIX}lib${BPN}"

BBCLASSEXTEND = "native nativesdk"
