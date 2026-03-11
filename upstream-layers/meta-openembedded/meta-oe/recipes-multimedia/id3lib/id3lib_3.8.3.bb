SUMMARY = "Library for interacting with ID3 tags"
SECTION = "libs/multimedia"
LICENSE = "LGPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=3bf50002aefd002f49e7bb854063f7e7"
DEPENDS = "zlib"


SRC_URI = "${SOURCEFORGE_MIRROR}/id3lib/id3lib-${PV}.tar.gz;name=archive \
           ${DEBIAN_MIRROR}/main/i/id3lib3.8.3/id3lib3.8.3_3.8.3-16.2.debian.tar.xz;name=patch;subdir=${BP} \
           file://acdefine.patch \
"
SRC_URI[archive.md5sum] = "19f27ddd2dda4b2d26a559a4f0f402a7"
SRC_URI[archive.sha256sum] = "2749cc3c0cd7280b299518b1ddf5a5bcfe2d1100614519b68702230e26c7d079"
SRC_URI[patch.md5sum] = "997c764d3be11c9a51779d93facf1118"
SRC_URI[patch.sha256sum] = "ac2ee23ec89ba2af51d2c6dd5b1b6bf9f8a9f813de251bc182941439a4053176"

CVE_STATUS[CVE-2007-4460] = "patched: fix is included in debian patch"

inherit autotools

# Unlike other Debian packages, id3lib*.diff.gz contains another series of
# patches maintained by quilt. So manually apply them before applying other local
# patches. Also remove all temp files before leaving, because do_patch() will pop
# up all previously applied patches in the start
do_patch[depends] += "quilt-native:do_populate_sysroot"
id3lib_do_patch() {
    cd ${S}
    # it's important that we only pop the existing patches when they've
    # been applied, otherwise quilt will climb the directory tree
    # and reverse out some completely different set of patches
    if [ -d ${S}/patches ]; then
        # whilst this is the default directory, doing it like this
        # defeats the directory climbing that quilt will otherwise
        # do; note the directory must exist to defeat this, hence
        # the test inside which we operate
        QUILT_PATCHES=${S}/patches quilt pop -a
    fi
    if [ -d ${S}/.pc-${BPN} ]; then
        rm -rf ${S}/.pc
        mv ${S}/.pc-${BPN} ${S}/.pc
        QUILT_PATCHES=${S}/debian/patches quilt pop -a
        rm -rf ${S}/.pc ${S}/debian
    fi
    QUILT_PATCHES=${S}/debian/patches quilt push -a
    mv ${S}/.pc ${S}/.pc-${BPN}
}

do_unpack[cleandirs] += "${S}"

# We invoke base do_patch at end, to incorporate any local patch
python do_patch() {
    bb.build.exec_func('id3lib_do_patch', d)
    bb.build.exec_func('patch_do_patch', d)
}
