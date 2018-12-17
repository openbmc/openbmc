SUMMARY = "Aircrack-ng is a set of tools for auditing wireless networks"
DESCRIPTION = "Aircrack-ng is an 802.11 WEP and WPA-PSK keys cracking program that can recover keys once enough data packets have been captured. It implements the standard FMS attack along with some optimizations like KoreK attacks, as well as the PTW attack, thus making the attack much faster compared to other WEP cracking tools."
SECTION = "security"
LICENSE = "GPL-2.0"

LIC_FILES_CHKSUM = "file://LICENSE;beginline=1;endline=2;md5=1fbd81241fe252ec0f5658a521ab7dd8"

DEPENDS = "libnl openssl sqlite3 libpcre libpcap"

SRC_URI = "http://download.aircrack-ng.org/${BP}.tar.gz"

SRC_URI[md5sum] = "c7c5b076dee0c25ee580b0f56f455623"
SRC_URI[sha256sum] = "8ae08a7c28741f6ace2769267112053366550e7f746477081188ad38410383ca"

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

FILES_${PN} += "/usr/local/"

RDEPENDS_${PN} = "libpcap"
