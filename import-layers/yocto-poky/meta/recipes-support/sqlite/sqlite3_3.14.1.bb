require sqlite3.inc

LICENSE = "PD"
LIC_FILES_CHKSUM = "file://sqlite3.h;endline=11;md5=65f0a57ca6928710b418c094b3570bb0"

SRC_URI = "\
  http://www.sqlite.org/2016/sqlite-autoconf-${SQLITE_PV}.tar.gz \
  file://0001-revert-ad601c7962-that-brings-2-increase-of-build-ti.patch \
  "

SRC_URI[md5sum] = "3634a90a3f49541462bcaed3474b2684"
SRC_URI[sha256sum] = "bc7182476900017becb81565ecea7775d46ab747a97281aa610f4f45881c47a6"
