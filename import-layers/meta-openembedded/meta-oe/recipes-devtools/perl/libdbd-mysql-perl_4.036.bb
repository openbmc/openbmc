SUMMARY = "A Perl DBI driver for MySQL"
DESCRIPTION = "DBD::mysql is the Perl5 Database Interface driver for \
the MySQL database. In other words: DBD::mysql is an interface between \
the Perl programming language and the MySQL programming API that comes \
with the MySQL relational database management system. Most functions \
provided by this programming API are supported. Some rarely used \
functions are missing, mainly because no-one ever requested them. \
"
HOMEPAGE = "http://search.cpan.org/~michielb/DBD-mysql-4.036/lib/DBD/mysql.pm"

SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+"
DEPENDS += "libdbi-perl-native libmysqlclient"

LIC_FILES_CHKSUM = "file://LICENSE;md5=d0a06964340e5c0cde88b7af611f755c"

SRC_URI = "http://search.cpan.org/CPAN/authors/id/M/MI/MICHIELB/DBD-mysql-${PV}.tar.gz \
"

SRC_URI[md5sum] = "fdee1d8dc4ae54bc6cb7cd5a3f3d3342"
SRC_URI[sha256sum] = "5c48a823f86b8110ccb6504c6176ca248b52f56829dd4548bc39c3509f4154cf"

S = "${WORKDIR}/DBD-mysql-${PV}"

inherit cpan
