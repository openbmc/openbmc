SUMMARY = "Automatically give your module the ability to have plugins"
DESCRIPTION = "Provides a simple but, hopefully, extensible way of \
having 'plugins' for your module. Obviously this isn't going to be the \
be all and end all of solutions but it works for me.\
\
Essentially all it does is export a method into your namespace that \
looks through a search path for .pm files and turn those into class \
names.\
\
Optionally it instantiates those classes for you."
SECTION = "libs"

AUTHOR = "Simon Wistow <simon@thegestalt.org>"
HOMEPAGE = "https://github.com/simonwistow/Module-Pluggable"

LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://README;beginline=322;endline=325;md5=086450ce010f6fda25db0b38fcc41086"

SRCNAME = "Module-Pluggable"
SRC_URI = "https://github.com/moto-timo/${SRCNAME}/archive/${PV}.tar.gz"
SRC_URI[md5sum] = "e32475d6ff5843f738cedacd3b7a2cdb"
SRC_URI[sha256sum] = "58c62292eea6d06959eba1b97598650813211265403242d57efb2f605c96059f"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit cpan

RDEPENDS_${PN} = " perl-module-base \
    perl-module-deprecate \
    perl-module-file-basename \
    perl-module-file-find \
    perl-module-file-spec \
    perl-module-file-spec-functions \
    perl-module-if \
    perl-module-test-more \
"

BBCLASSEXTEND = "native"

