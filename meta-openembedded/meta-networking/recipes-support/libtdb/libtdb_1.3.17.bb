SUMMARY = "The tdb library"
HOMEPAGE = "http://tdb.samba.org/"
SECTION = "libs"
LICENSE = "LGPL-3.0+ & GPL-3.0+"

LIC_FILES_CHKSUM = "file://tools/tdbdump.c;endline=18;md5=b59cd45aa8624578126a8c98f48018c4 \
                    file://include/tdb.h;endline=27;md5=f5bb544641d3081821bcc1dd58310be6"

SRC_URI = "https://samba.org/ftp/tdb/tdb-${PV}.tar.gz \
           file://do-not-check-xsltproc-manpages.patch \
           file://tdb-Add-configure-options-for-packages.patch \
           file://0001-waf-add-support-of-cross_compile.patch \
           file://0001-Makefile-fix-problem-that-waf-cannot-found.patch \
"

SRC_URI[md5sum] = "519d373ac72a66b0a2739dbb495de127"
SRC_URI[sha256sum] = "1cb4399394c60a773430ca54848359adcf54fb6f136afdcfcbbe62b5f4245614"

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

PACKAGES += "tdb-tools python-tdb"

RPROVIDES_${PN}-dbg += "python-tdb-dbg"

FILES_${PN} = "${libdir}/*.so.*"
FILES_tdb-tools = "${bindir}/*"
FILES_python-tdb = "${libdir}/python${PYTHON_BASEVERSION}/site-packages/*"
RDEPENDS_python-tdb = "python"
