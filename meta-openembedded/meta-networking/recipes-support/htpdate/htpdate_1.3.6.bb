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
HOMEPAGE = "https://github.com/twekkel/htpdate"
BUGTRACKER = "https://github.com/twekkel/htpdate/issues"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://htpdate.c;beginline=26;endline=30;md5=2b6cdb94bd5349646d7e33f9f501eef7"

SRC_URI = "http://www.vervest.org/htp/archive/c/htpdate-${PV}.tar.gz"
SRC_URI[sha256sum] = "3cdc558ec8e53ef374a42490b2f28c0b23981fa8754a6d7182044707828ad1e9"

TARGET_CC_ARCH += "${LDFLAGS}"

do_configure () {
	:
}

do_compile () {
	oe_runmake
}

do_install () {
	oe_runmake install 'INSTALL=install' 'STRIP=echo' 'DESTDIR=${D}'
}
