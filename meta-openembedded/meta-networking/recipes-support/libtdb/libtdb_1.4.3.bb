SUMMARY = "The tdb library"
HOMEPAGE = "http://tdb.samba.org/"
SECTION = "libs"
LICENSE = "LGPL-3.0-or-later & GPL-3.0-or-later"

LIC_FILES_CHKSUM = "file://tools/tdbdump.c;endline=18;md5=b59cd45aa8624578126a8c98f48018c4 \
                    file://include/tdb.h;endline=27;md5=f5bb544641d3081821bcc1dd58310be6"

SRC_URI = "https://samba.org/ftp/tdb/tdb-${PV}.tar.gz \
           file://tdb-Add-configure-options-for-packages.patch \
           file://0001-Fix-pyext_PATTERN-for-cross-compilation.patch \
"

SRC_URI[md5sum] = "e638e8890f743624a754304b3f994f4d"
SRC_URI[sha256sum] = "c8058393dfa15f47e11ebd2f1d132693f0b3b3b8bf22d0201bfb305026f88a1b"

PACKAGECONFIG ??= "\
    ${@bb.utils.filter('DISTRO_FEATURES', 'acl', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'xattr', 'attr', '', d)} \
"

PACKAGECONFIG[acl] = "--with-acl,--without-acl,acl"
PACKAGECONFIG[attr] = "--with-attr,--without-attr,attr"
PACKAGECONFIG[libaio] = "--with-libaio,--without-libaio,libaio"
PACKAGECONFIG[libbsd] = "--with-libbsd,--without-libbsd,libbsd"
PACKAGECONFIG[libcap] = "--with-libcap,--without-libcap,libcap"
PACKAGECONFIG[valgrind] = "--with-valgrind,--without-valgrind,valgrind"

S = "${WORKDIR}/tdb-${PV}"

inherit waf-samba

#cross_compile cannot use preforked process, since fork process earlier than point subproces.popen
#to cross Popen
export WAF_NO_PREFORK="yes"

EXTRA_OECONF += "--disable-rpath \
                 --bundled-libraries=NONE \
                 --builtin-libraries=replace \
                 --with-libiconv=${STAGING_DIR_HOST}${prefix}\
                "

PACKAGES += "tdb-tools python3-tdb"

RPROVIDES:${PN}-dbg += "python3-tdb-dbg"

FILES:${PN} = "${libdir}/*.so.*"
FILES:tdb-tools = "${bindir}/*"
FILES:python3-tdb = "${libdir}/python${PYTHON_BASEVERSION}/site-packages/*"
RDEPENDS:python3-tdb = "python3"
INSANE_SKIP:${MLPREFIX}python3-tdb = "dev-so"
