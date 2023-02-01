SUMMARY = "Hierarchical, reference counted memory pool system with destructors"
HOMEPAGE = "https://tevent.samba.org"
SECTION = "libs"
LICENSE = "LGPL-3.0-or-later"

DEPENDS += "libtalloc libtirpc"
RDEPENDS:python3-tevent = "python3"

export PYTHONHASHSEED="1"

SRC_URI = "https://samba.org/ftp/tevent/tevent-${PV}.tar.gz \
           file://0001-Add-configure-options-for-packages.patch \
           file://0002-Fix-pyext_PATTERN-for-cross-compilation.patch \
          "

SRC_URI:append:libc-musl = " file://cmocka-fix-musl-libc-conflicting-types-error.patch"

LIC_FILES_CHKSUM = "file://tevent.h;endline=26;md5=47386b7c539bf2706b7ce52dc9341681"

SRC_URI[md5sum] = "9f413f3184f79a4deecd9444242a5dca"
SRC_URI[sha256sum] = "b9437a917fa55344361beb64ec9e0042e99cae8879882a62dd38f6abe2371d0c"

inherit pkgconfig ptest waf-samba

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
                 --disable-rpath-install \
                 --bundled-libraries=cmocka \
                 --builtin-libraries=replace \
                 --with-libiconv=${STAGING_DIR_HOST}${prefix}\
                 --without-gettext \
                "

do_install:append() {
    install -Dm 0755 ${B}/bin/test_tevent_trace ${D}${bindir}/test_tevent_trace
    install -Dm 0755 ${B}/bin/test_tevent_tag ${D}${bindir}/test_tevent_tag
    install -Dm 0755 ${B}/bin/replace_testsuite ${D}${bindir}/replace_testsuite
}

PACKAGES += "python3-tevent"

RPROVIDES:${PN}-dbg += "python3-tevent-dbg"

FILES:${PN} += "${libdir}/tevent/*"
FILES:${PN}-ptest += "${bindir}/replace_testsuite \
                      ${bindir}/test_tevent_tag \
                      ${bindir}/test_tevent_trace \
                      ${libdir}/libcmocka-tevent.so"
FILES:python3-tevent = "${libdir}/python${PYTHON_BASEVERSION}/site-packages/*"

INSANE_SKIP:${MLPREFIX}python3-tevent = "dev-so"
