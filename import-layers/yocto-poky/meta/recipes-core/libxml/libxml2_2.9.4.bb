SUMMARY = "XML C Parser Library and Toolkit"
DESCRIPTION = "The XML Parser Library allows for manipulation of XML files.  Libxml2 exports Push and Pull type parser interfaces for both XML and HTML.  It can do DTD validation at parse time, on a parsed document instance or with an arbitrary DTD.  Libxml2 includes complete XPath, XPointer and Xinclude implementations.  It also has a SAX like interface, which is designed to be compatible with Expat."
HOMEPAGE = "http://www.xmlsoft.org/"
BUGTRACKER = "http://bugzilla.gnome.org/buglist.cgi?product=libxml2"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://Copyright;md5=2044417e2e5006b65a8b9067b683fcf1 \
                    file://hash.c;beginline=6;endline=15;md5=96f7296605eae807670fb08947829969 \
                    file://list.c;beginline=4;endline=13;md5=cdbfa3dee51c099edb04e39f762ee907 \
                    file://trio.c;beginline=5;endline=14;md5=6c025753c86d958722ec76e94cae932e"

DEPENDS = "zlib virtual/libiconv"

SRC_URI = "ftp://xmlsoft.org/libxml2/libxml2-${PV}.tar.gz;name=libtar \
           http://www.w3.org/XML/Test/xmlts20080827.tar.gz;name=testtar \
           file://libxml-64bit.patch \
           file://ansidecl.patch \
           file://runtest.patch \
           file://run-ptest \
           file://python-sitepackages-dir.patch \
           file://libxml-m4-use-pkgconfig.patch \
          "

SRC_URI[libtar.md5sum] = "ae249165c173b1ff386ee8ad676815f5"
SRC_URI[libtar.sha256sum] = "ffb911191e509b966deb55de705387f14156e1a56b21824357cdf0053233633c"
SRC_URI[testtar.md5sum] = "ae3d1ebe000a3972afa104ca7f0e1b4a"
SRC_URI[testtar.sha256sum] = "96151685cec997e1f9f3387e3626d61e6284d4d6e66e0e440c209286c03e9cc7"

BINCONFIG = "${bindir}/xml2-config"

inherit autotools pkgconfig binconfig-disabled pythonnative ptest

RDEPENDS_${PN}-ptest += "python-core"

RDEPENDS_${PN}-python += "python-core"

RDEPENDS_${PN}-ptest_append_libc-glibc = " glibc-gconv-ebcdic-us glibc-gconv-ibm1141"

export PYTHON_SITE_PACKAGES="${PYTHON_SITEPACKAGES_DIR}"

PACKAGECONFIG ??= "python \
    ${@bb.utils.contains('DISTRO_FEATURES', 'ipv6', 'ipv6', '', d)} \
"
PACKAGECONFIG[python] = "--with-python=${PYTHON},--without-python,python"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"

# WARNING: zlib is require for RPM use
EXTRA_OECONF = "--without-debug --without-legacy --with-catalog --without-docbook --with-c14n --without-lzma --with-fexceptions"
EXTRA_OECONF_class-native = "--without-legacy --without-docbook --with-c14n --without-lzma --with-zlib"
EXTRA_OECONF_class-nativesdk = "--without-legacy --without-docbook --with-c14n --without-lzma --with-zlib"
EXTRA_OECONF_linuxstdbase = "--with-debug --with-legacy --with-docbook --with-c14n --without-lzma --with-zlib"

python populate_packages_prepend () {
    # autonamer would call this libxml2-2, but we don't want that
    if d.getVar('DEBIAN_NAMES', True):
        d.setVar('PKG_libxml2', '${MLPREFIX}libxml2')
}

PACKAGES += "${PN}-utils ${PN}-python"

FILES_${PN}-staticdev += "${PYTHON_SITEPACKAGES_DIR}/*.a"
FILES_${PN}-dev += "${libdir}/xml2Conf.sh ${libdir}/cmake/*"
FILES_${PN}-utils += "${bindir}/*"
FILES_${PN}-python += "${PYTHON_SITEPACKAGES_DIR}"

do_configure_prepend () {
	# executables take longer to package: these should not be executable
	find ${WORKDIR}/xmlconf/ -type f -exec chmod -x {} \+
}

do_install_ptest () {
	cp -r ${WORKDIR}/xmlconf ${D}${PTEST_PATH}
}

BBCLASSEXTEND = "native nativesdk"
