require sqlite3.inc

LICENSE = "PD"
LIC_FILES_CHKSUM = "file://sqlite3.h;endline=11;md5=65f0a57ca6928710b418c094b3570bb0"

SRC_URI = "\
  http://www.sqlite.org/2017/sqlite-autoconf-${SQLITE_PV}.tar.gz \
  "
SRC_URI[md5sum] = "450a95a7bde697c9fe4de9ae2fffdcca"
SRC_URI[sha256sum] = "a4e485ad3a16e054765baf6371826b5000beed07e626510896069c0bf013874c"
