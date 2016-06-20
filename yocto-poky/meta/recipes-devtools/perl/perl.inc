SUMMARY = "Perl scripting language"
HOMEPAGE = "http://www.perl.org/"
SECTION = "devel"
LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://Copying;md5=5b122a36d0f6dc55279a0ebc69f3c60b \
		    file://Artistic;md5=2e6fd2475335af892494fe1f7327baf3"

SRC_URI = "http://www.cpan.org/src/5.0/${BP}.tar.xz"

inherit upstream-version-is-even
