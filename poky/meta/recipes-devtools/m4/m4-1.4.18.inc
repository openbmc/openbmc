SUMMARY = "Traditional Unix macro processor"
HOMEPAGE = "https://www.gnu.org/software/m4/m4.html"
DESCRIPTION = "GNU m4 is an implementation of the traditional Unix macro processor.  It is mostly SVR4 \
compatible although it has some extensions (for example, handling more than 9 positional parameters to macros). \
GNU M4 also has built-in functions for including files, running shell commands, doing arithmetic, etc."

inherit autotools texinfo

SRC_URI = "${GNU_MIRROR}/m4/m4-${PV}.tar.gz \
           file://ac_config_links.patch \
           file://remove-gets.patch \
           file://m4-1.4.18-glibc-change-work-around.patch \
           "

SRC_URI_append_class-target = " file://0001-Unset-need_charset_alias-when-building-for-musl.patch"

SRC_URI[md5sum] = "a077779db287adf4e12a035029002d28"
SRC_URI[sha256sum] = "ab2633921a5cd38e48797bf5521ad259bdc4b979078034a3b790d7fec5493fab"

LICENSE = "GPLv3"

LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504\
	            file://examples/COPYING;md5=4031593b2166d6c47cae282d944a7ede"

# Fix "Argument list too long" error when len(TMPDIR) = 410
acpaths = "-I ./m4"

EXTRA_OECONF += "--without-libsigsegv-prefix"

EXTRA_OEMAKE += "'infodir=${infodir}'"
