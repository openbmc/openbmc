DESCRIPTION = "GNU gperf is a perfect hash function generator"
HOMEPAGE = "http://www.gnu.org/software/gperf"
SUMMARY  = "Generate a perfect hash function from a set of keywords"
LICENSE  = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://src/main.cc;beginline=8;endline=19;md5=dec8f611845d047387ed56b5b85fa99b"

SRC_URI  = "${GNU_MIRROR}/${BPN}/${BP}.tar.gz \
            file://0001-Make-the-code-C-17-compliant.patch"
SRC_URI[md5sum] = "9e251c0a618ad0824b51117d5d9db87e"
SRC_URI[sha256sum] = "588546b945bba4b70b6a3a616e80b4ab466e3f33024a352fc2198112cdbb3ae2"

SRC_URI += "file://1862c6e57a308a05889c80c048dbc58bdc378dcb.patch"

inherit autotools

# The nested configures don't find the parent aclocal.m4 out of the box, so tell
# it where to look explicitly (mirroring the behaviour of upstream's Makefile.devel).
EXTRA_AUTORECONF += " -I ${S} --exclude=aclocal"

BBCLASSEXTEND = "native nativesdk"
