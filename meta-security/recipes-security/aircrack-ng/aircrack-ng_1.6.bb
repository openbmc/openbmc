SUMMARY = "Aircrack-ng is a set of tools for auditing wireless networks"
DESCRIPTION = "Aircrack-ng is an 802.11 WEP and WPA-PSK keys cracking program that can recover keys once enough data packets have been captured. It implements the standard FMS attack along with some optimizations like KoreK attacks, as well as the PTW attack, thus making the attack much faster compared to other WEP cracking tools."
SECTION = "security"
LICENSE = "GPL-2.0"

LIC_FILES_CHKSUM = "file://LICENSE;beginline=1;endline=2;md5=1fbd81241fe252ec0f5658a521ab7dd8"

DEPENDS = "libnl openssl sqlite3 libpcre libpcap"

SRC_URI = "http://download.aircrack-ng.org/${BP}.tar.gz"

SRC_URI[md5sum] = "22ddc85549b51ed0da0931d01ef215e5"
SRC_URI[sha256sum] = "4f0bfd486efc6ea7229f7fbc54340ff8b2094a0d73e9f617e0a39f878999a247"

inherit autotools-brokensep pkgconfig

PACKAGECONFIG ?= ""
CFLAGS += " -I${S}/src/include"

OEMAKE_EXTRA = "sqlite=true experimental=true pcre=true \
                prefix=${prefix} \
                "

do_compile () {
    make ${OEMAKE_EXTRA} TOOL_PREFIX=${TARGET_SYS}-
}

do_install () {
    make DESTDIR=${D} ${OEMAKE_EXTRA} ext_scripts=true install
}

FILES:${PN} += "${libdir}/*.so"
FILES_SOLIBSDEV = ""
INSANE_SKIP:${PN} += "dev-so"

RDEPENDS:${PN} = "libpcap"
