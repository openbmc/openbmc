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
SRC_URI[md5sum] = "f3c5dfa3b1a2ee06cd57c255cc8b70a0"
SRC_URI[sha256sum] = "15d838c4f3375332fd95554619179b69e4ec91418a3a5296e7c631b7ed19e7ca"

inherit autotools

# doc build: requires (native) xstlproc, fop, pdf2ps
EXTRA_OECONF="--disable-build-docs \
  --with-libxml-prefix=${STAGING_LIBDIR}/.. \
  --with-libxslt-prefix=${STAGING_LIBDIR}/.."

# http://errors.yoctoproject.org/Errors/Details/157121/
# /bin/bash: src/usage.c: No such file or directory
# Makefile:2121: recipe for target 'src/usage.c' failed
PARALLEL_MAKE = ""
