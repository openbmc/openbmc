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

SRC_URI[md5sum] = "7d06d8709188e07df853d9e91db88927"
SRC_URI[sha256sum] = "6a3fc2616567f23993984ada3cea97d953a27669ffd1bfbbe961f26e0cf96cc5"

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

PACKAGES += "tdb-tools python-tdb"

RPROVIDES_${PN}-dbg += "python-tdb-dbg"

FILES_${PN} = "${libdir}/*.so.*"
FILES_tdb-tools = "${bindir}/*"
FILES_python-tdb = "${libdir}/python${PYTHON_BASEVERSION}/site-packages/*"
RDEPENDS_python-tdb = "python"
