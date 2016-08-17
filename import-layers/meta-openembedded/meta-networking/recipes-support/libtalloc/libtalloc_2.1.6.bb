SUMMARY = "Hierarchical, reference counted memory pool system with destructors"
HOMEPAGE = "http://talloc.samba.org"
SECTION = "libs"
LICENSE = "LGPL-3.0+ & GPL-3.0+"

DEPENDS += "libbsd"

SRC_URI = "http://samba.org/ftp/talloc/talloc-${PV}.tar.gz"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/LGPL-3.0;md5=bfccfe952269fff2b407dd11f2f3083b \
                    file://${COREBASE}/meta/files/common-licenses/GPL-3.0;md5=c79ff39f19dfec6d293b95dea7b07891"

SRC_URI[md5sum] = "707010c6ede5821fd34397f5d9ec6ab8"
SRC_URI[sha256sum] = "3b8e1a50bacb359d99942e0dd9941cef779ae4b5eb20f138873bd8270cb1d47b"


inherit waf-samba

PACKAGECONFIG[attr] = ",,attr"

SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'attr', '', 'file://avoid-attr-unless-wanted.patch', d)}"

S = "${WORKDIR}/talloc-${PV}"

EXTRA_OECONF += "--disable-rpath \
                 --disable-rpath-install \
                 --bundled-libraries=NONE \
                 --builtin-libraries=replace \
                 --disable-silent-rules \
                 --with-libiconv=${STAGING_DIR_HOST}${prefix}\
                "
DISABLE_STATIC = ""

PACKAGES += "pytalloc pytalloc-dbg pytalloc-dev"

FILES_pytalloc = "${libdir}/python${PYTHON_BASEVERSION}/site-packages/* \
                  ${libdir}/libpytalloc-util.so.2 \
                  ${libdir}/libpytalloc-util.so.2.1.1 \
                 "
FILES_pytalloc-dbg = "${libdir}/python${PYTHON_BASEVERSION}/site-packages/.debug \
                      ${libdir}/.debug/libpytalloc-util.so.2.1.1"
FILES_pytalloc-dev = "${libdir}/libpytalloc-util.so"
RDEPENDS_pytalloc = "python"
