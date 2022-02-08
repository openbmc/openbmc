SUMMARY = "PAM cached credentials module"
HOMEPAGE = "https://www.padl.com/OSS/pam_ccreds.html"
SECTION = "libs"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

DEPENDS = "libpam openssl db"

inherit features_check
REQUIRED_DISTRO_FEATURES = "pam"

SRCREV = "e2145df09469bf84878e4729b4ecd814efb797d1"

SRC_URI = "git://github.com/PADL/pam_ccreds;branch=master;protocol=https"

S = "${WORKDIR}/git"

inherit autotools

EXTRA_OECONF += "--libdir=${base_libdir} "

FILES_${PN} += "${base_libdir}/security/pam*"
