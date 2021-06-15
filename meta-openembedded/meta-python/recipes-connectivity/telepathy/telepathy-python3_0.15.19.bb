SUMMARY = "Telepathy IM framework - Python package"
HOMEPAGE = "http://telepathy.freedesktop.org/wiki/"
LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1 \
                    file://src/utils.py;beginline=1;endline=17;md5=9a07d1a9791a7429a14e7b25c6c86822"

DEPENDS = "libxslt-native"

SRC_URI = "http://telepathy.freedesktop.org/releases/telepathy-python/telepathy-python-${PV}.tar.gz \
           file://parallel_make.patch \
           file://remove_duplicate_install.patch \
           file://telepathy-python_fix_for_automake_1.12.patch"

PR = "r6"

S = "${WORKDIR}/telepathy-python-${PV}"

inherit autotools python3native

SRC_URI[md5sum] = "f7ca25ab3c88874015b7e9728f7f3017"
SRC_URI[sha256sum] = "244c0e1bf4bbd78ae298ea659fe10bf3a73738db550156767cc2477aedf72376"

FILES_${PN} += "\
    ${libdir}/python*/site-packages/telepathy/*.py \
    ${libdir}/python*/site-packages/telepathy/*/*.py \
"

do_install_append () {
    rm -fr ${D}${libdir}/python*/site-packages/telepathy/__pycache__
    rm -fr ${D}${libdir}/python*/site-packages/telepathy/__pycache__
    rm -fr ${D}${libdir}/python*/site-packages/telepathy/*/__pycache__
    rm -fr ${D}${libdir}/python*/site-packages/telepathy/*/__pycache__
}
RDEPENDS_${PN} += "python3-dbus"
