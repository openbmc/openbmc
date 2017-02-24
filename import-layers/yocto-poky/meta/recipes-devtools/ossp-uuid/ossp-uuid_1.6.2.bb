SUMMARY = "Universally Unique Identifier (UUID) library"
DESCRIPTION = "OSSP uuid is a ISO-C:1999 application programming interface \
(API) and corresponding command line interface (CLI) for the generation of \
DCE 1.1, ISO/IEC 11578:1996 and RFC 4122 compliant Universally Unique \
Identifier (UUID). It supports DCE 1.1 variant UUIDs of version 1 (time \
and node based), version 3 (name based, MD5), version 4 (random number \
based) and version 5 (name based, SHA-1)."
DESCRIPTION_uuid = "This package contains a tool to create Universally \
Unique Identifiers (UUID) from the command-line."

HOMEPAGE = "http://www.ossp.org/pkg/lib/uuid/"
SECTION = "libs"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://README;beginline=30;endline=55;md5=b394fadb039bbfca6ad9d9d769ee960e \
	   file://uuid_md5.c;beginline=1;endline=28;md5=9c1f4b2218546deae24c91be1dcf00dd"

PR = "r2"
UPSTREAM_CHECK_URI = "${DEBIAN_MIRROR}/main/o/ossp-uuid/"

SRC_URI = "http://snapshot.debian.org/archive/debian/20160728T043443Z/pool/main/o/${BPN}/${BPN}_${PV}.orig.tar.gz \
	   file://0001-Change-library-name.patch \
	   file://0002-uuid-preserve-m-option-status-in-v-option-handling.patch \
	   file://0003-Fix-whatis-entries.patch \
	   file://0004-fix-data-uuid-from-string.patch \
	   file://uuid-libtool.patch \
	   file://uuid-nostrip.patch \
           file://install-pc.patch \
           file://ldflags.patch \
	  "
SRC_URI[md5sum] = "5db0d43a9022a6ebbbc25337ae28942f"
SRC_URI[sha256sum] = "11a615225baa5f8bb686824423f50e4427acd3f70d394765bdff32801f0fd5b0"

S = "${WORKDIR}/uuid-${PV}"

inherit autotools update-alternatives

EXTRA_OECONF = "--without-dce --without-cxx --without-perl --without-perl-compat --without-php --without-pgsql"
EXTRA_OECONF = "--includedir=${includedir}/ossp"

do_configure_prepend() {
  # This package has a completely custom aclocal.m4, which should be acinclude.m4.
  if [ ! -e ${S}/acinclude.m4 ]; then
    mv ${S}/aclocal.m4 ${S}/acinclude.m4
  fi

  rm -f ${S}/libtool.m4
}

do_install_append() {
  mkdir -p  ${D}${includedir}/ossp
  mv ${D}${libdir}/pkgconfig/uuid.pc ${D}${libdir}/pkgconfig/ossp-uuid.pc
}

PACKAGES =+ "uuid"
FILES_uuid = "${bindir}/uuid"
FILES_${PN} = "${libdir}/libossp-uuid.so.16*"
FILES_${PN}-dev += "${bindir}/uuid-config"

BBCLASSEXTEND = "native nativesdk"

ALTERNATIVE_${PN}-doc = "uuid.3"
ALTERNATIVE_PRIORITY_${PN}-doc = "200"
ALTERNATIVE_LINK_NAME[uuid.3] = "${mandir}/man3/uuid.3"
