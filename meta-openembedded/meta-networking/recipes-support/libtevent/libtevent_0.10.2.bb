SUMMARY = "Hierarchical, reference counted memory pool system with destructors"
HOMEPAGE = "http://tevent.samba.org"
SECTION = "libs"
LICENSE = "LGPL-3.0-or-later"

DEPENDS += "libtalloc libtirpc"
RDEPENDS:python3-tevent = "python3"

SRC_URI = "https://samba.org/ftp/tevent/tevent-${PV}.tar.gz \
           file://options-0.10.0.patch \
           file://0001-libtevent-fix-musl-libc-compile-error.patch \
           file://0001-Fix-pyext_PATTERN-for-cross-compilation.patch \
"
LIC_FILES_CHKSUM = "file://tevent.h;endline=26;md5=4e458d658cb25e21efc16f720e78b85a"

SRC_URI[md5sum] = "105c7a4dbb96f1751eb27dfd05e7fa84"
SRC_URI[sha256sum] = "f8427822e5b2878fb8b28d6f50d96848734f3f3130612fb574fdd2d2148a6696"

inherit pkgconfig waf-samba

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

SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'attr', '', 'file://avoid-attr-unless-wanted.patch', d)}"

S = "${WORKDIR}/tevent-${PV}"

#cross_compile cannot use preforked process, since fork process earlier than point subproces.popen
#to cross Popen
export WAF_NO_PREFORK="yes"

EXTRA_OECONF += "--disable-rpath \
                 --bundled-libraries=NONE \
                 --builtin-libraries=replace \
                 --with-libiconv=${STAGING_DIR_HOST}${prefix}\
                 --without-gettext \
                "

PACKAGES += "python3-tevent"

RPROVIDES:${PN}-dbg += "python3-tevent-dbg"

FILES:python3-tevent = "${libdir}/python${PYTHON_BASEVERSION}/site-packages/*"

INSANE_SKIP:${MLPREFIX}python3-tevent = "dev-so"
