SUMMARY = "Selectively remove #ifdef statements from sources"
SECTION = "devel"
LICENSE = "BSD-2-Clause"
HOMEPAGE = "http://dotat.at/prog/unifdef/"

LIC_FILES_CHKSUM = "file://COPYING;md5=78fc6c2c1dc4f18c891ed5b7a469fe32 \
                    file://unifdef.c;endline=32;md5=aaec84d8b68d8b6dea71f45e9949ebfe"

SRC_URI = "http://dotat.at/prog/${BPN}/${BP}.tar.xz"
SRC_URI[md5sum] = "337053fd8a7d9ab3adf5e50f88af95b7"
SRC_URI[sha256sum] = "828ffc270ac262b88fe011136acef2780c05b0dc3c5435d005651740788d4537"

do_install() {
	oe_runmake install DESTDIR=${D} prefix=${prefix}
}

BBCLASSEXTEND = "native"
