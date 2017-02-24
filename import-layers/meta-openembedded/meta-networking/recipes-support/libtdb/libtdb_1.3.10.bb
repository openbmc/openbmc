SUMMARY = "The tdb library"
SECTION = "libs"
LICENSE = "LGPL-3.0+ & GPL-3.0+"

LIC_FILES_CHKSUM = "file://tools/tdbdump.c;endline=18;md5=b59cd45aa8624578126a8c98f48018c4 \
                    file://include/tdb.h;endline=27;md5=f5bb544641d3081821bcc1dd58310be6"

SRC_URI = "http://samba.org/ftp/tdb/tdb-${PV}.tar.gz \
           file://do-not-check-xsltproc-manpages.patch \
           file://tdb-Add-configure-options-for-packages.patch \
"

SRC_URI[md5sum] = "c98f24eb469022b0f1741d3a504cfda5"
SRC_URI[sha256sum] = "5578fb726bd04835f250bea11fe7d1398fe0bb3a7c9390404241c83cd6b50c1c"

PACKAGECONFIG ??= "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'acl', 'acl', '', d)} \
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

EXTRA_OECONF += "--disable-rpath \
                 --bundled-libraries=NONE \
                 --builtin-libraries=replace \
                 --with-libiconv=${STAGING_DIR_HOST}${prefix}\
                "
DISABLE_STATIC = ""

PACKAGES += "tdb-tools python-tdb python-tdb-dbg"

FILES_${PN} = "${libdir}/*.so.*"
FILES_tdb-tools = "${bindir}/*"
FILES_python-tdb = "${libdir}/python${PYTHON_BASEVERSION}/site-packages/*"
FILES_python-tdb-dbg = "${libdir}/python${PYTHON_BASEVERSION}/site-packages/.debug/*"
RDEPENDS_python-tdb = "python"
