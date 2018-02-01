require m4.inc

EXTRA_OECONF += "--without-libsigsegv-prefix"

LICENSE = "GPLv3"

LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504\
	            file://examples/COPYING;md5=4031593b2166d6c47cae282d944a7ede"

SRC_URI += "file://ac_config_links.patch \
            file://remove-gets.patch \
           "

SRC_URI_append_class-target = "\
           file://0001-Unset-need_charset_alias-when-building-for-musl.patch \
	   "

# Fix "Argument list too long" error when len(TMPDIR) = 410
acpaths = "-I ./m4"

SRC_URI[md5sum] = "a077779db287adf4e12a035029002d28"
SRC_URI[sha256sum] = "ab2633921a5cd38e48797bf5521ad259bdc4b979078034a3b790d7fec5493fab"
