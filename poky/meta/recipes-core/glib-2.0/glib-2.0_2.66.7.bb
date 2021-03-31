require glib.inc

PE = "1"

SHRT_VER = "${@oe.utils.trim_version("${PV}", 2)}"

SRC_URI = "${GNOME_MIRROR}/glib/${SHRT_VER}/glib-${PV}.tar.xz \
           file://run-ptest \
           file://0001-Fix-DATADIRNAME-on-uclibc-Linux.patch \
           file://Enable-more-tests-while-cross-compiling.patch \
           file://0001-Remove-the-warning-about-deprecated-paths-in-schemas.patch \
           file://0001-Install-gio-querymodules-as-libexec_PROGRAM.patch \
           file://0001-Do-not-ignore-return-value-of-write.patch \
           file://0010-Do-not-hardcode-python-path-into-various-tools.patch \
           file://0001-Set-host_machine-correctly-when-building-with-mingw3.patch \
           file://0001-Do-not-write-bindir-into-pkg-config-files.patch \
           file://0001-meson-Run-atomics-test-on-clang-as-well.patch \
           file://0001-gio-tests-resources.c-comment-out-a-build-host-only-.patch \
           file://0001-gio-tests-codegen.py-bump-timeout-to-100-seconds.patch \
           file://0001-tests-codegen.py-removing-unecessary-print-statement.patch \
           "
SRC_URI += "\
           file://0001-gobject-Drop-use-of-volatile-from-get_type-macros.patch \
           file://0002-tests-Fix-non-atomic-access-to-a-shared-variable.patch \
           file://0003-tests-Fix-non-atomic-access-to-a-shared-variable.patch \
           file://0004-tests-Drop-unnecessary-volatile-qualifiers-from-test.patch \
           file://0005-tests-Fix-non-atomic-access-to-some-shared-variables.patch \
           file://0006-tests-Drop-unnecessary-volatile-qualifiers-from-test.patch \
           file://0007-gdbusconnection-Drop-unnecessary-volatile-qualifiers.patch \
           file://0008-gdbuserror-Drop-unnecessary-volatile-qualifiers-from.patch \
           file://0009-gio-Drop-unnecessary-volatile-qualifiers-from-intern.patch \
           file://0010-kqueue-Fix-unlocked-access-to-shared-variable.patch \
           file://0011-tests-Drop-unnecessary-volatile-qualifiers-from-test.patch \
           file://0012-tests-Fix-non-atomic-access-to-some-shared-variables.patch \
           file://0013-gatomic-Drop-unnecessary-volatile-qualifiers-from-in.patch \
           file://0014-gatomic-Drop-unnecessary-volatile-qualifiers-from-ma.patch \
           file://0015-glib-Drop-unnecessary-volatile-qualifiers-from-inter.patch \
           file://0016-gobject-Drop-unnecessary-volatile-qualifiers-from-in.patch \
           file://0017-gmessages-Drop-unnecessary-volatile-qualifiers-from-.patch \
           file://0018-gtypes-Drop-volatile-qualifier-from-gatomicrefcount.patch \
           file://0019-gatomicarray-Drop-volatile-qualifier-from-GAtomicArr.patch \
           file://0020-gobject-Drop-volatile-qualifier-from-GObject.ref_cou.patch \
           file://0021-tests-Drop-unnecessary-volatile-qualifiers-from-test.patch \
           file://0022-build-Drop-unnecessary-volatile-qualifiers-from-conf.patch \
           file://0023-gdbusprivate-Avoid-a-warning-about-a-statement-with-.patch \
           file://0024-tests-Add-comment-to-volatile-atomic-tests.patch \
           file://0025-gthread-Use-g_atomic-primitives-correctly-in-destruc.patch \
           file://0026-gtype-Fix-some-typos-in-comments.patch \
           file://0027-gtype-Add-some-missing-atomic-accesses-to-init_state.patch \
           file://0028-gresource-Fix-a-pointer-mismatch-with-an-atomic-load.patch \
           file://0029-docs-Document-not-to-use-volatile-qualifiers.patch \
"
SRC_URI_append_class-native = " file://relocate-modules.patch"

SRC_URI[sha256sum] = "09f158769f6f26b31074e15b1ac80ec39b13b53102dfae66cfe826fb2cc65502"

# Find any meson cross files in FILESPATH that are relevant for the current
# build (using siteinfo) and add them to EXTRA_OEMESON.
inherit siteinfo
def find_meson_cross_files(d):
    if bb.data.inherits_class('native', d):
        return ""

    thisdir = os.path.normpath(d.getVar("THISDIR"))
    import collections
    sitedata = siteinfo_data(d)
    # filename -> found
    files = collections.OrderedDict()
    for path in d.getVar("FILESPATH").split(":"):
        for element in sitedata:
            filename = os.path.normpath(os.path.join(path, "meson.cross.d", element))
            sanitized_path = filename.replace(thisdir, "${THISDIR}")
            if sanitized_path == filename:
                if os.path.exists(filename):
                    bb.error("Cannot add '%s' to --cross-file, because it's not relative to THISDIR '%s' and sstate signature would contain this full path" % (filename, thisdir))
                continue
            files[filename.replace(thisdir, "${THISDIR}")] = os.path.exists(filename)

    items = ["--cross-file=" + k for k,v in files.items() if v]
    d.appendVar("EXTRA_OEMESON", " " + " ".join(items))
    items = ["%s:%s" % (k, "True" if v else "False") for k,v in files.items()]
    d.appendVarFlag("do_configure", "file-checksums", " " + " ".join(items))

python () {
    find_meson_cross_files(d)
}
