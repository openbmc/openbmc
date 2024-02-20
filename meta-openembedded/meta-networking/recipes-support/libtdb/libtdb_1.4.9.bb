SUMMARY = "The tdb library"
HOMEPAGE = "https://tdb.samba.org/"
SECTION = "libs"
LICENSE = "LGPL-3.0-or-later & GPL-3.0-or-later"

LIC_FILES_CHKSUM = "file://tools/tdbdump.c;endline=18;md5=b59cd45aa8624578126a8c98f48018c4 \
                    file://include/tdb.h;endline=27;md5=f5bb544641d3081821bcc1dd58310be6"

export PYTHONHASHSEED="1"
export PYTHONARCHDIR = "${PYTHON_SITEPACKAGES_DIR}"

SRC_URI = "https://samba.org/ftp/tdb/tdb-${PV}.tar.gz \
           file://0001-tdb-Add-configure-options-for-packages.patch \
           file://0002-Fix-pyext_PATTERN-for-cross-compilation.patch \
           file://run-ptest \
"

SRC_URI[sha256sum] = "0ac226073e3a2db8648da7af744cb95f50766a52feeb001d558b2b321b74a765"

PACKAGECONFIG ??= "\
    ${@bb.utils.filter('DISTRO_FEATURES', 'acl', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'xattr', 'attr', '', d)} \
"

PACKAGECONFIG[acl] = "--with-acl,--without-acl,acl"
PACKAGECONFIG[attr] = "--with-attr,--without-attr,attr"
PACKAGECONFIG[libbsd] = "--with-libbsd,--without-libbsd,libbsd"
PACKAGECONFIG[libcap] = "--with-libcap,--without-libcap,libcap"
PACKAGECONFIG[valgrind] = "--with-valgrind,--without-valgrind,valgrind"

S = "${WORKDIR}/tdb-${PV}"

inherit waf-samba pkgconfig ptest

# Cross_compile cannot use preforked process, since fork process earlier than point subproces.popen
# to cross Popen
export WAF_NO_PREFORK="yes"

EXTRA_OECONF += "--disable-rpath \
                 --disable-rpath-install \
                 --bundled-libraries=NONE \
                 --builtin-libraries=replace \
                 --with-libiconv=${STAGING_DIR_HOST}${prefix}\
                "

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    install -m 0755 ${B}/bin/tdb1-* ${D}${PTEST_PATH}/tests/
    install -m 0755 ${B}/bin/replace_testsuite ${D}${PTEST_PATH}/tests/
    install -d ${D}${PTEST_PATH}/tests/test
    install -m 0644 ${B}/test/*.tdb ${D}${PTEST_PATH}/tests/test/
    install -m 0644 ${B}/test/*.corrupt ${D}${PTEST_PATH}/tests/test/
}

PACKAGES += "tdb-tools python3-tdb"

RPROVIDES:${PN}-dbg += "python3-tdb-dbg"

FILES:${PN} = "${libdir}/*.so.*"
FILES:tdb-tools = "${bindir}/*"
FILES:python3-tdb = "${libdir}/python${PYTHON_BASEVERSION}/site-packages/*"
RDEPENDS:python3-tdb = "python3"
INSANE_SKIP:${MLPREFIX}python3-tdb = "dev-so"
