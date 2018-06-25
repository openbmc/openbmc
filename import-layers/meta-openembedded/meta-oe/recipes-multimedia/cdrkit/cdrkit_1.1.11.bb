SUMMARY = "CD/DVD command line tools"
HOMEPAGE = "http://cdrkit.org/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b30d3b2750b668133fc17b401e1b98f8"

# While writing download from cdrkit.org was broken so get sources from debian
SRC_URI = "${DEBIAN_MIRROR}/main/c/${BPN}/${BPN}_${PV}.orig.tar.gz \
           file://0001-do-not-create-a-run-test-to-determine-order-of-bitfi.patch \
           file://0001-genisoimage-Fix-fprintf-format-errors.patch \
           file://0001-define-__THROW-to-avoid-build-issue-with-musl.patch \
           file://0002-Do-not-use-rcmd-on-build-with-musl.patch \
           "
SRC_URI[md5sum] = "efe08e2f3ca478486037b053acd512e9"
SRC_URI[sha256sum] = "d1c030756ecc182defee9fe885638c1785d35a2c2a297b4604c0e0dcc78e47da"

inherit cmake

DEPENDS = "libcap file bzip2"
RDEPENDS_${PN} = "perl"

do_install_append() {
    ln -sf ${bindir}/genisoimage ${D}${bindir}/mkisofs
}
