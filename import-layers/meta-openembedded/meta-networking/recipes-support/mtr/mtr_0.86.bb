SUMMARY = "Combined traceroute and ping utility"
DESCRIPTION = "mtr combines the functionality of the 'traceroute' and 'ping' programs in a single network diagnostic tool."
HOMEPAGE = "http://www.bitwizard.nl/mtr/"
SECTION = "net"
DEPENDS = "ncurses"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3 \
                    file://mtr.c;beginline=5;endline=16;md5=af1fafbbfa1bfd48af839f4bb3221106"

SRC_URI = "ftp://ftp.bitwizard.nl/mtr/mtr-${PV}.tar.gz"

SRC_URI[md5sum] = "8d63592c9d4579ef20cf491b41843eb2"
SRC_URI[sha256sum] = "c5d948920b641cc35f8b380fc356ddfe07cce6a9c6474afe242fc58113f28c06"

inherit autotools

EXTRA_OECONF = "--without-gtk"

