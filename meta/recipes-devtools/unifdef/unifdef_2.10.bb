SUMMARY = "Selectively remove #ifdef statements from sources"
SECTION = "devel"
LICENSE = "BSD-2-Clause"

LIC_FILES_CHKSUM = "file://unifdef.c;endline=32;md5=2cc23f0382a6f560f6a9ecf4e040c0da"

SRC_URI = "http://dotat.at/prog/${BPN}/${BP}.tar.xz"
SRC_URI[md5sum] = "bb5d895e5ebbba5c5cc0c2771cf97ebe"
SRC_URI[sha256sum] = "3b9b2b6b1952e9b9c1b9f734edec270689a35bdbf33ae66b50e19b2ed0d2df06"

do_install() {
	oe_runmake install DESTDIR=${D} prefix=${prefix}
}

BBCLASSEXTEND = "native"
