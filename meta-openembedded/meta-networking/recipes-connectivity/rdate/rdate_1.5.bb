SUMMARY = "Tool for getting the date/time from a remote machine."
DESCRIPTION = "The rdate utility retrieves the date and time from \
  another machine on your network, using the protocol described in RFC 868. \
  If you run rdate as root, it will set your machine local time to the \
  time of the machine that you queried. \
"

HOMEPAGE= "https://www.aelius.com/njh/rdate/"
SECTION = "Applications/System"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3"

SRC_URI = "https://www.aelius.com/njh/${BPN}/${BP}.tar.gz \
           file://0001-src-rdate-Fix-printf-format-errors.patch \
           file://0001-rdate-define-logwtmp-on-libc-glibc.patch \
           "
SRC_URI[md5sum] = "0a4f612480777fdf25264ac03c57eae6"
SRC_URI[sha256sum] = "6e800053eaac2b21ff4486ec42f0aca7214941c7e5fceedd593fa0be99b9227d"

inherit autotools
