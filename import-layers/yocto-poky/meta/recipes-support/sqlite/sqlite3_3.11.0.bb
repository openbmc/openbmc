require sqlite3.inc

LICENSE = "PD"
LIC_FILES_CHKSUM = "file://sqlite3.h;endline=11;md5=65f0a57ca6928710b418c094b3570bb0"

SRC_URI = "http://www.sqlite.org/2016/sqlite-autoconf-${SQLITE_PV}.tar.gz \
           file://fix-disable-static-shell.patch \
"

SRC_URI[md5sum] = "a6cdc3e0a6e5087d620037ae0c48720d"
SRC_URI[sha256sum] = "508d4dcbcf7a7181e95c717a1dc4ae3c0880b3d593be0c4b40abb6c3a0e201fb"
