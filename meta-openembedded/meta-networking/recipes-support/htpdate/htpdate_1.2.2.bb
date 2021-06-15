# Copyright (C) 2018 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "HTTP based time synchronization tool"

DESCRIPTION = "The  HTTP Time Protocol (HTP) is used to synchronize a computer's time with\
 web servers as reference time source. This program can be used instead\
 ntpdate or similar, in networks that has a firewall blocking the NTP port.\
 Htpdate will synchronize the computer time to Greenwich Mean Time (GMT),\
 using the timestamps from HTTP headers found in web servers response (the\
 HEAD method will be used to get the information).\
 Htpdate works through proxy servers. Accuracy of htpdate will be usually\
 within 0.5 seconds (better with multiple servers).\
"

HOMEPAGE = "http://www.vervest.org/htp/"

LICENSE = "GPL-2.0+"
LIC_FILES_CHKSUM = "file://htpdate.c;beginline=26;endline=30;md5=d7018a4d2c5a6eab392709a05e5e168a"

SRC_URI = "http://www.vervest.org/htp/archive/c/htpdate-${PV}.tar.xz \
           file://0001-Make-environment-variables-assignments-to-be-weak.patch \
           file://0001-Replace-ntp_adjtime-with-adjtimex.patch \
           "
SRC_URI[md5sum] = "aad8c33933648532ac8716c809b15be1"
SRC_URI[sha256sum] = "5f1f959877852abb3153fa407e8532161a7abe916aa635796ef93f8e4119f955"

do_configure () {
	:
}

do_compile () {
	oe_runmake
}

do_install () {
	oe_runmake install 'INSTALL=install' 'STRIP=echo' 'DESTDIR=${D}'
}

