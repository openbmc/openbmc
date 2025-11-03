LICENSE= "MIT"
SUMMARY = "X Server Nokia 770 extensions library"
SECTION = "x11/libs"
DEPENDS = "virtual/libx11 libxext xpext"
LIC_FILES_CHKSUM = "file://COPYING;md5=ea2bda168c508c7cd8afa567b2fcc549"
SRC_URI = "http://repository.maemo.org/pool/maemo/ossw/source/x/xsp/${BPN}_${PV}.tar.gz \
           file://xsp-fix-pc.patch"
S = "${WORKDIR}/Xsp"

inherit autotools pkgconfig features_check
# depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[md5sum] = "2a0d8d02228d4cbd28b6e07bb7c17cf5"
SRC_URI[sha256sum] = "8b722b952b64841d996c70c3278499886c81bb5012991beed5f66f4158418f59"

CVE_STATUS[CVE-2006-2658] = "cpe-incorrect: The recipe used in the `meta-openembedded` is a different xsp package compared to the one which has the CVE issue."
