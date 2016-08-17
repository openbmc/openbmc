#
# glibc specific configuration
#

LIBCEXTENSION = "${@['', '-gnu'][(d.getVar('ABIEXTENSION', True) or '') != '']}"

# Add glibc overrides to the overrides for glibc.
LIBCOVERRIDE = ":libc-glibc"
OVERRIDES .= "${LIBCOVERRIDE}"

PREFERRED_PROVIDER_virtual/libiconv ?= "glibc"
PREFERRED_PROVIDER_virtual/nativesdk-libiconv ?= "nativesdk-glibc"
PREFERRED_PROVIDER_virtual/nativesdk-libintl ?= "nativesdk-glibc"
PREFERRED_PROVIDER_virtual/libintl ?= "glibc"
PREFERRED_PROVIDER_virtual/libc ?= "glibc"
PREFERRED_PROVIDER_virtual/nativesdk-libc ?= "nativesdk-glibc"
PREFERRED_PROVIDER_virtual/libc-locale ?= "glibc-locale"

CXXFLAGS += "-fvisibility-inlines-hidden"

LIBC_DEPENDENCIES = "libsegfault \
		     glibc \
		     glibc-dbg \
		     glibc-dev \
		     glibc-utils \
		     glibc-thread-db \
		     ${@get_libc_locales_dependencies(d)}"

LIBC_LOCALE_DEPENDENCIES = "\
	glibc-localedata-i18n \
	glibc-gconv-ibm850 \
	glibc-gconv-cp1252 \
	glibc-gconv-iso8859-1 \
	glibc-gconv-iso8859-15"

def get_libc_locales_dependencies(d):
    if 'libc-locales' in (d.getVar('DISTRO_FEATURES', True) or '').split() :
        return d.getVar('LIBC_LOCALE_DEPENDENCIES', True) or ''
    else:
        return ''
