SUMMARY = "A Perl DBI driver for MySQL"
DESCRIPTION = "DBD::mysql is the Perl5 Database Interface driver for \
the MySQL database. In other words: DBD::mysql is an interface between \
the Perl programming language and the MySQL programming API that comes \
with the MySQL relational database management system. Most functions \
provided by this programming API are supported. Some rarely used \
functions are missing, mainly because no-one ever requested them. \
"
HOMEPAGE = "https://github.com/perl5-dbi/DBD-mysql"

SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+"
DEPENDS += "libdev-checklib-perl-native libdbi-perl-native libmysqlclient"

LIC_FILES_CHKSUM = "file://LICENSE;md5=d0a06964340e5c0cde88b7af611f755c"

SRCREV = "9b5b70ea372f49fe9bc9e592dae3870596d1e3d6"
SRC_URI = "git://github.com/perl5-dbi/DBD-mysql.git;protocol=https;branch=master"

S = "${WORKDIR}/git"

inherit cpan
