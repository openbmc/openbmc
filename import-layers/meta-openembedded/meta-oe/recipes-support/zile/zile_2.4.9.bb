SUMMARY = "Zile is lossy Emacs"
HOMEPAGE = "http://zile.sourceforge.net/"
DEPENDS = "ncurses bdwgc"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "http://ftp.gnu.org/gnu/zile/${BP}.tar.gz \
           file://remove-help2man.patch \
"

SRC_URI[md5sum] = "84a0af58fb4fbe3af16bde2ef2b8f5ae"
SRC_URI[sha256sum] = "c71959c7aca02ac66be526ecccbc7954fb0ea7591ed3c13311a95e8f040b0049"

inherit autotools
