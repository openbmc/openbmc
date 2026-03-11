SUMMARY = "libsass command line driver "
HOMEPAGE = "https://sass-lang.com/libsass/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2f8a76980411a3f1f1480b141ce06744"

DEPENDS = "libsass"

inherit autotools pkgconfig

SRC_URI = "git://github.com/sass/sassc.git;protocol=https;branch=master \
           file://0001-Remove-sassc_version.h-from-source-directory.patch"

SRCREV = "66f0ef37e7f0ad3a65d2f481eff09d09408f42d0"

CVE_STATUS[CVE-2022-43357] = "cpe-incorrect: this is CVE for libsass, not sassc wrapper"

BBCLASSEXTEND = "native"
