SUMMARY = "An HTTP library implementation in C"
DESCRIPTION = "libsoup is an HTTP client/server library for GNOME. It uses GObjects \
and the glib main loop, to integrate well with GNOME applications."
HOMEPAGE = "https://wiki.gnome.org/Projects/libsoup"
BUGTRACKER = "https://bugzilla.gnome.org/"
SECTION = "x11/gnome/libs"
LICENSE = "LGPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=5f30f0716dfdd0d91eb439ebec522ec2"

DEPENDS = "glib-2.0 glib-2.0-native libxml2 sqlite3 libpsl nghttp2"

inherit gettext gnomebase upstream-version-is-even gobject-introspection gi-docgen vala ptest-gnome

SRC_URI[archive.sha256sum] = "5013dcef9136f6f861e6529fca578af50c1f52a39219027fb5aff652d1371a04"

SRC_URI += "file://run-ptest \
            file://0001-tests-ssl-test-Accept-partial-input-when-the-client-.patch \
            "

PROVIDES = "libsoup-3.0"

GIR_MESON_ENABLE_FLAG = 'enabled'
GIR_MESON_DISABLE_FLAG = 'disabled'

PACKAGECONFIG ??= "${@bb.utils.contains('PTEST_ENABLED', '1', 'tests', '', d)}"
PACKAGECONFIG[brotli] = "-Dbrotli=enabled,-Dbrotli=disabled,brotli"
PACKAGECONFIG[gssapi] = "-Dgssapi=enabled,-Dgssapi=disabled,krb5"
PACKAGECONFIG[ntlm] = "-Dntlm=enabled,-Dntlm=disabled"
PACKAGECONFIG[sysprof] = "-Dsysprof=enabled,-Dsysprof=disabled,sysprof"
PACKAGECONFIG[tests] = "-Dtests=true -Dinstalled_tests=true,-Dtests=false -Dinstalled_tests=false"

# Tell libsoup where the target ntlm_auth is installed
do_write_config:append:class-target() {
    cat >${WORKDIR}/soup.cross <<EOF
[binaries]
ntlm_auth = '${bindir}/ntlm_auth'
EOF
}
EXTRA_OEMESON:append:class-target = " --cross-file ${WORKDIR}/soup.cross"

EXTRA_OEMESON += "-Dtls_check=false"
# tests are controlled via the 'tests' PACKAGECONFIG (enabled for ptest)
EXTRA_OEMESON += "-Dautobahn=disabled -Dpkcs11_tests=disabled"

GIDOCGEN_MESON_OPTION = 'docs'
GIDOCGEN_MESON_ENABLE_FLAG = 'enabled'
GIDOCGEN_MESON_DISABLE_FLAG = 'disabled'

# These tests abort under ptest and leave the binaries in place:
# - header-parsing-test: the /header-parsing/param-list subtest aborts as
#   soup_header_parse_semi_param_list() returns one parameter instead of
#   three for a header with invalid-UTF-8 extended (*=) parameters:
#   https://gitlab.gnome.org/GNOME/libsoup/-/work_items/560
# - logger-test (long-invalid-body-length) and http2-body-stream-test
#   (large_data) exercise multi-GiB allocations and are killed by the OOM
#   killer on the memory-constrained ptest machines.
PTEST_SKIP_TESTS = "header-parsing-test logger-test http2-body-stream-test"

# These tests intermittently abort (SIGABRT) during teardown under the slower
# emulated targets, so skip them there:
# - http2-test on qemuriscv64.
# - multithread-test on qemuarm64 (glibc and musl).
PTEST_SKIP_TESTS:append:riscv64 = " http2-test"
PTEST_SKIP_TESTS:append:aarch64 = " multithread-test"

do_install_ptest:append() {
    for t in ${PTEST_SKIP_TESTS}; do
        rm -f ${D}${datadir}/installed-tests/libsoup-3.0/$t.test
    done
}

# When built without gnome support, libsoup will contain only one shared lib
# and will therefore become subject to renaming by debian.bbclass. Prevent
# renaming in order to keep the package name consistent regardless of whether
# gnome support is enabled or disabled.
DEBIAN_NOAUTONAME:${PN} = "1"

# glib-networking is needed for SSL, proxies, etc.
RRECOMMENDS:${PN} = "glib-networking"

# The TLS-based tests (ssl, server-auth, websocket, hsts) need a glib TLS
# backend at runtime.
RDEPENDS:${PN}-ptest += "glib-networking"

BBCLASSEXTEND = "native nativesdk"

CVE_STATUS[CVE-2026-1467] = "fixed-version: fixed in 3.6.6"
CVE_STATUS[CVE-2026-1536] = "fixed-version: fixed in 3.6.6"
CVE_STATUS[CVE-2026-1801] = "fixed-version: fixed in 3.6.6"
CVE_STATUS[CVE-2026-2369] = "fixed-version: fixed in 3.6.6"
CVE_STATUS[CVE-2026-2436] = "fixed-version: fixed in 3.6.6"
CVE_STATUS[CVE-2026-2443] = "fixed-version: fixed in 3.6.6"
CVE_STATUS[CVE-2026-0719] = "fixed-version: fixed in 3.7.1"
CVE_STATUS[CVE-2026-1539] = "fixed-version: fixed in 3.7.1"
CVE_STATUS[CVE-2026-2708] = "fixed-version: fixed in 3.7.1"
CVE_STATUS[CVE-2026-4271] = "fixed-version: fixed in 3.7.1"
CVE_STATUS[CVE-2026-5119] = "fixed-version: fixed in 3.7.1"
