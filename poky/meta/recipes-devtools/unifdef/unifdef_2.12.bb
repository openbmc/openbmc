SUMMARY = "Selectively remove #ifdef statements from sources"
SECTION = "devel"
LICENSE = "BSD-2-Clause"
HOMEPAGE = "http://dotat.at/prog/unifdef/"

LIC_FILES_CHKSUM = "file://COPYING;md5=3498caf346f6b77934882101749ada23 \
                    file://unifdef.c;endline=32;md5=6f4ee8085d6e6ab0f7cb4390e1a9c497 \
                    "

SRC_URI = "http://dotat.at/prog/${BPN}/${BP}.tar.xz"
SRC_URI[md5sum] = "ae8c0b3b4c43c1f6bc5f32412a820818"
SRC_URI[sha256sum] = "43ce0f02ecdcdc723b2475575563ddb192e988c886d368260bc0a63aee3ac400"

UPSTREAM_CHECK_REGEX = "unifdef-(?P<pver>((\d+\.*)+)(?![a-f0-9]{6,})).tar"

do_install() {
	oe_runmake install DESTDIR=${D} prefix=${prefix}
}

BBCLASSEXTEND = "native"
