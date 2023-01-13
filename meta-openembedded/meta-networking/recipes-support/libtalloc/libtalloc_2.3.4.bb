SUMMARY = "Hierarchical, reference counted memory pool system with destructors"
HOMEPAGE = "https://talloc.samba.org"
SECTION = "libs"
LICENSE = "LGPL-3.0-or-later & GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://talloc.h;beginline=3;endline=27;md5=a301712782cad6dd6d5228bfa7825249 \
                    file://pytalloc.h;beginline=1;endline=18;md5=21ab13bd853679d7d47a1739cb3b7db6 \
                    "

export PYTHONHASHSEED="1"

SRC_URI = "https://www.samba.org/ftp/talloc/talloc-${PV}.tar.gz \
           file://0001-talloc-Add-configure-options-for-packages.patch \
           file://0002-Fix-pyext_PATTERN-for-cross-compilation.patch \
"
SRC_URI[sha256sum] = "179f9ebe265e67e4ab2c26cad2b7de4b6a77c6c212f966903382869f06be6505"

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

#cross_compile cannot use preforked process, since fork process earlier than point subproces.popen
#to cross Popen
export WAF_NO_PREFORK="yes"

EXTRA_OECONF += "--disable-rpath \
                 --disable-rpath-install \
                 --bundled-libraries=NONE \
                 --builtin-libraries=replace \
                 --disable-silent-rules \
                 --with-libiconv=${STAGING_DIR_HOST}${prefix}\
                "

PACKAGES += "pytalloc pytalloc-dev"

RPROVIDES:${PN}-dbg += "pytalloc-dbg"

FILES:pytalloc = "${libdir}/python${PYTHON_BASEVERSION}/site-packages/* \
                  ${libdir}/libpytalloc-util.so.2 \
                  ${libdir}/libpytalloc-util.so.2.1.1 \
                 "
FILES:pytalloc-dev = "${libdir}/libpytalloc-util.so"
RDEPENDS:pytalloc = "python3"
