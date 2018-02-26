SUMMARY = "The tdb library"
HOMEPAGE = "http://tdb.samba.org/"
SECTION = "libs"
LICENSE = "LGPL-3.0+ & GPL-3.0+"

LIC_FILES_CHKSUM = "file://tools/tdbdump.c;endline=18;md5=b59cd45aa8624578126a8c98f48018c4 \
                    file://include/tdb.h;endline=27;md5=f5bb544641d3081821bcc1dd58310be6"

SRC_URI = "https://samba.org/ftp/tdb/tdb-${PV}.tar.gz \
           file://do-not-check-xsltproc-manpages.patch \
           file://tdb-Add-configure-options-for-packages.patch \
"

SRC_URI[md5sum] = "dd23f2ba8ca2bfdcb833bc79d5b5ac6f"
SRC_URI[sha256sum] = "3a7d4bb79229460df530c7e1c7067ba9fb9d370aa61fff537fdc2bdf918acbe9"

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

EXTRA_OECONF += "--disable-rpath \
                 --bundled-libraries=NONE \
                 --builtin-libraries=replace \
                 --with-libiconv=${STAGING_DIR_HOST}${prefix}\
                "

PACKAGES += "tdb-tools python-tdb python-tdb-dbg"

FILES_${PN} = "${libdir}/*.so.*"
FILES_tdb-tools = "${bindir}/*"
FILES_python-tdb = "${libdir}/python${PYTHON_BASEVERSION}/site-packages/*"
FILES_python-tdb-dbg = "${libdir}/python${PYTHON_BASEVERSION}/site-packages/.debug/*"
RDEPENDS_python-tdb = "python"
