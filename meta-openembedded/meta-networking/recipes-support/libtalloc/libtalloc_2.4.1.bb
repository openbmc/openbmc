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
           file://run-ptest \
"
SRC_URI[sha256sum] = "410a547f08557007be0e88194f218868358edc0ab98c98ba8c167930db3d33f9"

inherit waf-samba pkgconfig ptest

PACKAGECONFIG ??= "\
    ${@bb.utils.filter('DISTRO_FEATURES', 'acl', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'xattr', 'attr', '', d)} \
"
PACKAGECONFIG[acl] = "--with-acl,--without-acl,acl"
PACKAGECONFIG[attr] = "--with-attr,--without-attr,attr"
PACKAGECONFIG[libbsd] = "--with-libbsd,--without-libbsd,libbsd"
PACKAGECONFIG[libcap] = "--with-libcap,--without-libcap,libcap"
PACKAGECONFIG[valgrind] = "--with-valgrind,--without-valgrind,valgrind"

SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'attr', '', 'file://avoid-attr-unless-wanted.patch', d)}"

S = "${WORKDIR}/talloc-${PV}"

# Cross_compile cannot use preforked process, since fork process earlier than point subproces.popen
# to cross Popen
export WAF_NO_PREFORK="yes"

EXTRA_OECONF += "--disable-rpath \
                 --disable-rpath-install \
                 --bundled-libraries=NONE \
                 --builtin-libraries=replace \
                 --disable-silent-rules \
                 --with-libiconv=${STAGING_DIR_HOST}${prefix}\
                "

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    install -m 0755 ${B}/bin/*_testsuite ${D}${PTEST_PATH}/tests/
}

PACKAGES += "pytalloc pytalloc-dev"

RPROVIDES:${PN}-dbg += "pytalloc-dbg"

FILES:pytalloc = "${libdir}/python${PYTHON_BASEVERSION}/site-packages/* \
                  ${libdir}/libpytalloc-util.so.2 \
                  ${libdir}/libpytalloc-util.so.2.1.1 \
                 "
FILES:pytalloc-dev = "${libdir}/libpytalloc-util.so"
RDEPENDS:pytalloc = "python3"
