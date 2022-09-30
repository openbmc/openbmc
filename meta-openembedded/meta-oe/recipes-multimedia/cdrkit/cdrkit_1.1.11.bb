SUMMARY = "CD/DVD command line tools"
HOMEPAGE = "http://cdrkit.org/"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b30d3b2750b668133fc17b401e1b98f8"

# While writing download from cdrkit.org was broken so get sources from debian
SRC_URI = "${DEBIAN_MIRROR}/main/c/${BPN}/${BPN}_${PV}.orig.tar.gz \
           file://0001-do-not-create-a-run-test-to-determine-order-of-bitfi.patch \
           file://0001-genisoimage-Fix-fprintf-format-errors.patch \
           file://0001-define-__THROW-to-avoid-build-issue-with-musl.patch \
           file://0002-Do-not-use-rcmd-on-build-with-musl.patch \
           file://0001-genisoimage-Add-missing-extern-definition.patch \
           file://0001-add-new-option-eltorito-platform.patch \
           file://0001-genisoimage-Add-checksum.h-and-md5.h-for-function-pr.patch \
           "
SRC_URI:append:class-nativesdk = " \
           file://0001-install-netscsid-to-bin-for-nativesdk.patch \
"
SRC_URI[md5sum] = "efe08e2f3ca478486037b053acd512e9"
SRC_URI[sha256sum] = "d1c030756ecc182defee9fe885638c1785d35a2c2a297b4604c0e0dcc78e47da"

inherit cmake

DEPENDS = "libcap file bzip2"
RDEPENDS:dirsplit = "perl"

RDEPENDS:${PN}-dev = ""

PACKAGES =+ "dirsplit genisoimage icedax wodim"

FILES:dirsplit = " \
    ${bindir}/dirsplit \
"

FILES:genisoimage = " \
    ${bindir}/devdump \
    ${bindir}/genisoimage \
    ${bindir}/isodebug \
    ${bindir}/isodump \
    ${bindir}/isoinfo \
    ${bindir}/isovfy \
    ${bindir}/mkisofs \
"

FILES:icedax = " \
    ${bindir}/cdda2mp3 \
    ${bindir}/cdda2ogg \
    ${bindir}/icedax \
    ${bindir}/pitchplay \
    ${bindir}/readmult \
"

FILES:wodim = " \
    ${bindir}/readom \
    ${bindir}/wodim \
    ${sbindir}/netscsid \
"

do_install:append() {
    ln -sf --relative ${D}${bindir}/genisoimage ${D}${bindir}/mkisofs
}

BBCLASSEXTEND = "native nativesdk"
