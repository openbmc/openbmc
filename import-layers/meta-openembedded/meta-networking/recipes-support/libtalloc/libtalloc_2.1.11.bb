SUMMARY = "Hierarchical, reference counted memory pool system with destructors"
HOMEPAGE = "http://talloc.samba.org"
SECTION = "libs"
LICENSE = "LGPL-3.0+ & GPL-3.0+"
LIC_FILES_CHKSUM = "file://talloc.h;beginline=3;endline=27;md5=a301712782cad6dd6d5228bfa7825249 \
                    file://pytalloc.h;beginline=1;endline=18;md5=2c498cc6f2263672483237b20f46b43d"


SRC_URI = "https://samba.org/ftp/talloc/talloc-${PV}.tar.gz \
           file://options-2.1.10.patch \
"
SRC_URI[md5sum] = "452596f904ca2b10322bdaabf147a721"
SRC_URI[sha256sum] = "639eb35556a0af999123c4d883e79be05ff9f00ab4f9e4ac2e5775f9c5eeeed3"

inherit waf-samba

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

S = "${WORKDIR}/talloc-${PV}"

EXTRA_OECONF += "--disable-rpath \
                 --disable-rpath-install \
                 --bundled-libraries=NONE \
                 --builtin-libraries=replace \
                 --disable-silent-rules \
                 --with-libiconv=${STAGING_DIR_HOST}${prefix}\
                "

PACKAGES += "pytalloc pytalloc-dbg pytalloc-dev"

FILES_pytalloc = "${libdir}/python${PYTHON_BASEVERSION}/site-packages/* \
                  ${libdir}/libpytalloc-util.so.2 \
                  ${libdir}/libpytalloc-util.so.2.1.1 \
                 "
FILES_pytalloc-dbg = "${libdir}/python${PYTHON_BASEVERSION}/site-packages/.debug \
                      ${libdir}/.debug/libpytalloc-util.so.2.1.1"
FILES_pytalloc-dev = "${libdir}/libpytalloc-util.so"
RDEPENDS_pytalloc = "python"
