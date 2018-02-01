SUMMARY = "Hierarchical, reference counted memory pool system with destructors"
HOMEPAGE = "http://tevent.samba.org"
SECTION = "libs"
LICENSE = "LGPLv3+"

DEPENDS += "libtalloc"
RDEPENDS_python-tevent = "python"

SRC_URI = "https://samba.org/ftp/tevent/tevent-${PV}.tar.gz \
           file://tevent-Add-configure-options-for-packages.patch \
"
LIC_FILES_CHKSUM = "file://tevent.h;endline=26;md5=4e458d658cb25e21efc16f720e78b85a"

SRC_URI[md5sum] = "5453847664ae6bc4ec0153e59ac635b9"
SRC_URI[sha256sum] = "4a13f2256ad804c860e84068258bd0f8be31cf0c79aa8f3019fa32f6928c2bfc"

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

PACKAGES += "python-tevent python-tevent-dbg"

FILES_python-tevent = "${libdir}/python${PYTHON_BASEVERSION}/site-packages/*"
FILES_python-tevent-dbg = "${libdir}/python${PYTHON_BASEVERSION}/site-packages/.debug"
