SUMMARY = "Hierarchical, reference counted memory pool system with destructors"
HOMEPAGE = "http://ldb.samba.org"
SECTION = "libs"
LICENSE = "LGPL-3.0+ & LGPL-2.1+ & GPL-3.0+"

DEPENDS += "libtdb libtalloc libtevent popt"
RDEPENDS_pyldb += "python3"

SRC_URI = "http://samba.org/ftp/ldb/ldb-${PV}.tar.gz \
           file://do-not-import-target-module-while-cross-compile.patch \
           file://libldb-fix-musl-libc-unkown-type-error.patch \
           file://libldb-fix-musl-libc-conflict-type-error.patch \
           file://0001-waf-add-support-of-cross_compile.patch \
           file://options-1.5.4.patch \ 
          "

PACKAGECONFIG ??= "\
    ${@bb.utils.filter('DISTRO_FEATURES', 'acl', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'xattr', 'attr', '', d)} \
"
PACKAGECONFIG[acl] = "--with-acl,--without-acl,acl"
PACKAGECONFIG[attr] = "--with-attr,--without-attr,attr"
PACKAGECONFIG[ldap] = ",,openldap"
PACKAGECONFIG[libaio] = "--with-libaio,--without-libaio,libaio"
PACKAGECONFIG[libbsd] = "--with-libbsd,--without-libbsd,libbsd"
PACKAGECONFIG[libcap] = "--with-libcap,--without-libcap,libcap"
PACKAGECONFIG[valgrind] = "--with-valgrind,--without-valgrind,valgrind"
PACKAGECONFIG[lmdb] = ",--without-ldb-lmdb,lmdb,"

SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'ldap', '', 'file://avoid-openldap-unless-wanted.patch', d)}"

LIC_FILES_CHKSUM = "file://pyldb.h;endline=24;md5=dfbd238cecad76957f7f860fbe9adade \
                    file://man/ldb.3.xml;beginline=261;endline=262;md5=137f9fd61040c1505d1aa1019663fd08 \
                    file://tools/ldbdump.c;endline=19;md5=a7d4fc5d1f75676b49df491575a86a42"

SRC_URI[md5sum] = "fc58ef432c1fcb03fc3bb6cccce08977"
SRC_URI[sha256sum] = "ff82474d0bf109e415a2d50334bde5715f486a53ff4bb8c7f74459dd229e975b"

inherit waf-samba

S = "${WORKDIR}/ldb-${PV}"

#cross_compile cannot use preforked process, since fork process earlier than point subproces.popen
#to cross Popen
export WAF_NO_PREFORK="yes"

EXTRA_OECONF += "--disable-rpath \
                 --disable-rpath-install \
                 --bundled-libraries=cmocka \
                 --builtin-libraries=replace \
                 --with-modulesdir=${libdir}/ldb/modules \
                 --with-privatelibdir=${libdir}/ldb \
                 --with-libiconv=${STAGING_DIR_HOST}${prefix}\
                "

PACKAGES =+ "pyldb pyldb-dbg pyldb-dev"

NOAUTOPACKAGEDEBUG = "1"

FILES_${PN} += "${libdir}/ldb/*"
FILES_${PN}-dbg += "${bindir}/.debug/* \
                    ${libdir}/.debug/* \
                    ${libdir}/ldb/.debug/* \
                    ${libdir}/ldb/modules/ldb/.debug/*"

FILES_pyldb = "${libdir}/python${PYTHON_BASEVERSION}/site-packages/* \
               ${libdir}/libpyldb-util.*.so.* \
              "
FILES_pyldb-dbg = "${libdir}/python${PYTHON_BASEVERSION}/site-packages/.debug \
                   ${libdir}/.debug/libpyldb-util.*.so.*"
FILES_pyldb-dev = "${libdir}/libpyldb-util.*.so"
