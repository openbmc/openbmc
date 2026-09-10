LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=310c7c93cf5181c6b0cc8229a1f3c6f6"

SRC_URI = "git://gitlab.freedesktop.org/pwithnall/malcontent.git;protocol=https;nobranch=1;tag=${PV};name=malcontent"
SRC_URI += "git://gitlab.gnome.org/pwithnall/libgsystemservice.git;name=libgsystemservice;subdir=${S}/subprojects/libgsystemservice;protocol=https;nobranch=1"
SRC_URI += "git://gitlab.gnome.org/GNOME/gvdb.git;protocol=https;subdir=${S}/subprojects/gvdb;name=gvdb;nobranch=1"
SRC_URI += "https://www.corpit.ru/mjt/tinycdb/tinycdb-0.81.tar.gz;subdir=${S}/subprojects;name=tinycdb"
SRCREV_malcontent = "d28f6635359d02d1068d8b21fb5810e5074b7903"
SRCREV_libgsystemservice = "f4c5764652b5f347347b459ee011e4f02b29c3a0"
SRCREV_gvdb = "466fc22016cf0981424e7121557611942191992f"
SRC_URI[tinycdb.sha256sum] = "469de2d445bf54880f652f4b6dc95c7cdf6f5502c35524a45b2122d70d47ebc2"
SRCREV_FORMAT = "malcontent"

PV = "0.14.0"

do_configure:prepend() {
    cp ${S}/subprojects/packagefiles/tinycdb/meson.build ${S}/subprojects/tinycdb-0.81
}
