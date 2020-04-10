SUMMARY = "This is the set of GNU shar utilities."
HOMEPAGE = "http://www.gnu.org/software/sharutils/"
SECTION = "console/utils"
LICENSE="GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

inherit gettext autotools

SRC_URI = "${GNU_MIRROR}/${BPN}/${BP}.tar.gz \
           file://0001-Fix-build-with-clang.patch \
           file://CVE-2018-1000097.patch \
           file://0001-Fix-build-with-recent-gettext.patch \
           "
SRC_URI[md5sum] = "32a51b23e25ad5e6af4b89f228be1800"
SRC_URI[sha256sum] = "ee336e68549664e7a19b117adf02edfdeac6307f22e5ba78baca457116914637"

do_install_append() {
    if [ -e ${D}${libdir}/charset.alias ]
    then
        rm -rf ${D}${libdir}/charset.alias
        rmdir --ignore-fail-on-non-empty ${D}${libdir}
    fi
}

BBCLASSEXTEND = "native nativesdk"
