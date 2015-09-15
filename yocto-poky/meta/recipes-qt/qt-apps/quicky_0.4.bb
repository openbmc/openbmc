SUMMARY = "A simple note-taking application with Wiki-style syntax and behaviour"
HOMEPAGE = "http://qt-apps.org/content/show.php/Quicky?content=80325"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://version.h;endline=19;md5=878bdaff438dab86298301fd1a210e14"
SECTION = "x11/apps"

PR = "r2"

SRC_URI = "http://qt-apps.org/CONTENT/content-files/80325-quicky-0.4.tar.gz"

SRC_URI[md5sum] = "824d9e477ee9c4994f73a3cb215161d9"
SRC_URI[sha256sum] = "9c66376e0035d44547612bf629890769a6178c3e7eafbcf95f1c6207ac0f352a"

inherit qt4x11

do_install() {
	install -d ${D}${bindir}
	install -m 0755 ${S}/${BPN} ${D}${bindir}
}
