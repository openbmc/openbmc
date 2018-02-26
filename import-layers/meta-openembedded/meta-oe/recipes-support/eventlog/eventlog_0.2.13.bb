SUMMARY = "Replacement syslog API"
HOMEPAGE = "http://www.balabit.com/network-security/syslog-ng/opensource-logging-system"
DESCRIPTION = "The EventLog library aims to be a replacement of the \
              simple syslog() API provided on UNIX systems. The \
              major difference between EventLog and syslog is that \
              EventLog tries to add structure to messages. EventLog \
              provides an interface to build, format and output an \
              event record. The exact format and output method can \
              be customized by the administrator via a configuration \
              file. his package is the runtime part of the library. \
"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=b8ba8e77bcda9a53fac0fe39fe957767"

SRC_URI = "http://www.balabit.com/downloads/files/syslog-ng/open-source-edition/3.4.2/source/${BPN}_${PV}.tar.gz"

inherit autotools pkgconfig

SRC_URI[md5sum] = "68ec8d1ea3b98fa35002bb756227c315"
SRC_URI[sha256sum] = "7cb4e6f316daede4fa54547371d5c986395177c12dbdec74a66298e684ac8b85"
