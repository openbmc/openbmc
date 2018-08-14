require ${PN}.inc

LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://license.txt;md5=6db3822fc7512e83087ba798da013692"

SRC_URI = "http://ftp.postgresql.org/pub/odbc/versions/src/${BPN}-${PV}.tar.gz \
    file://psqlodbc-remove-some-checks-for-cross-compiling.patch \
    file://psqlodbc-donot-use-the-hardcode-libdir.patch \
    file://psqlodbc-fix-for-ptest-support.patch \
    file://run-ptest \
"

SRC_URI[md5sum] = "4c6e0b22187d7bb1c998ffac89e50f6b"
SRC_URI[sha256sum] = "9521f328bf28aaaf5c8488dc89792b614f9d6271742c0baf9bb41c97537764a8"
