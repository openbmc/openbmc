SUMMARY = "Command line XML toolkit"
DESCRIPTION = "XMLStarlet is a command line XML toolkit which can be used to \
               transform, query, validate, and edit XML documents and files \
               using  simple set of shell commands in similar way it is done \
               for plain text files using grep/sed/awk/tr/diff/patch."
HOMEPAGE = "http://xmlstar.sourceforge.net/"
BUGTRACKER = "http://xmlstar.sourceforge.net/bugs/"

SECTION = "console/utils"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=c04760d09e8b0fe73283d0cc9e8bea53"

DEPENDS = "libxml2 libxslt"

SRC_URI = "${SOURCEFORGE_MIRROR}/xmlstar/${BP}.tar.gz \
           file://configure.ac.patch \
           file://0001-usage2c.awk-fix-wrong-basename-regexp.patch"
SRC_URI[md5sum] = "0c6db295d0cf9ff0d439edb755b7e8f6"
SRC_URI[sha256sum] = "47b4ed042ea2909257f2a386001af49fceb303f84da7214779ccf99fffc6bbba"

inherit autotools

# doc build: requires (native) xstlproc, fop, pdf2ps
EXTRA_OECONF="--disable-build-docs \
  --with-libxml-prefix=${STAGING_LIBDIR}/.. \
  --with-libxslt-prefix=${STAGING_LIBDIR}/.."

#Makefile:2116: recipe for target 'src/elem-usage.c' failed
#make[1]: *** [src/elem-usage.c] Error 1
#/bin/bash: src/escape-usage.c: No such file or directory
#Makefile:2116: recipe for target 'src/escape-usage.c' failed
#make[1]: *** [src/escape-usage.c] Error 1
PARALLEL_MAKE = ""
