SUMMARY = "7-zip is a commandline utility handling 7z archives."
HOMEPAGE = "http://www.7-zip.org/"
LICENSE = "LGPL-2.1+ & unRAR"
LIC_FILES_CHKSUM = "file://DOC/copying.txt;md5=4fbd65380cdd255951079008b364516c \
                    file://DOC/unRarLicense.txt;md5=9c87ddde469ef94aed153b0951d088de \
                    file://DOC/License.txt;md5=879598edf1f54dddb6930d7581357f8b"

SRC_URI = "http://downloads.sourceforge.net/p7zip/p7zip/${PV}/p7zip_${PV}_src_all.tar.bz2 \
           file://do_not_override_compiler_and_do_not_strip.patch \
           file://CVE-2017-17969.patch \
           file://0001-Fix-narrowing-errors-Wc-11-narrowing.patch \
           "

SRC_URI[md5sum] = "a0128d661cfe7cc8c121e73519c54fbf"
SRC_URI[sha256sum] = "5eb20ac0e2944f6cb9c2d51dd6c4518941c185347d4089ea89087ffdd6e2341f"

S = "${WORKDIR}/${BPN}_${PV}"

do_install() {
	install -d ${D}${bindir}
	install -m 0755 ${S}/bin/* ${D}${bindir}
	ln -s 7za ${D}${bindir}/7z
}

BBCLASSEXTEND = "native"
