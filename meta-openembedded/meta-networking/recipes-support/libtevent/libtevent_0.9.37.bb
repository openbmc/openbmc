SUMMARY = "Hierarchical, reference counted memory pool system with destructors"
HOMEPAGE = "http://tevent.samba.org"
SECTION = "libs"
LICENSE = "LGPLv3+"

DEPENDS += "libtalloc libtirpc"
RDEPENDS_python-tevent = "python"

SRC_URI = "https://samba.org/ftp/tevent/tevent-${PV}.tar.gz \
           file://options-0.9.36.patch \
           file://0001-libtevent-fix-musl-libc-compile-error.patch \
"
LIC_FILES_CHKSUM = "file://tevent.h;endline=26;md5=4e458d658cb25e21efc16f720e78b85a"

SRC_URI[md5sum] = "6859cd4081fdb2a76b1cb4bf1c803a59"
SRC_URI[sha256sum] = "168345ed65eac03785cf77b95238e7dc66cbb473a42811693a6b0916e5dae7e0"

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

S = "${WORKDIR}/tevent-${PV}"

EXTRA_OECONF += "--disable-rpath \
                 --bundled-libraries=NONE \
                 --builtin-libraries=replace \
                 --with-libiconv=${STAGING_DIR_HOST}${prefix}\
                 --without-gettext \
                "

PACKAGES += "python-tevent"

RPROVIDES_${PN}-dbg += "python-tevent-dbg"

FILES_python-tevent = "${libdir}/python${PYTHON_BASEVERSION}/site-packages/*"
