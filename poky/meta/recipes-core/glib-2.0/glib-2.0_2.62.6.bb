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
           file://tzdata-update.patch \
           "

SRC_URI_append_class-native = " file://relocate-modules.patch"

SRC_URI[md5sum] = "46bba5410ad4e75f65e4b2cc61a1afc8"
SRC_URI[sha256sum] = "104fa26fbefae8024ff898330c671ec23ad075c1c0bce45c325c6d5657d58b9c"

# Find any meson cross files in FILESPATH that are relevant for the current
# build (using siteinfo) and add them to EXTRA_OEMESON.
inherit siteinfo
def find_meson_cross_files(d):
    if bb.data.inherits_class('native', d):
        return ""

    corebase = d.getVar("COREBASE")
    import collections
    sitedata = siteinfo_data(d)
    # filename -> found
    files = collections.OrderedDict()
    for path in d.getVar("FILESPATH").split(":"):
        for element in sitedata:
            filename = os.path.normpath(os.path.join(path, "meson.cross.d", element))
            files[filename.replace(corebase, "${COREBASE}")] = os.path.exists(filename)

    items = ["--cross-file=" + k for k,v in files.items() if v]
    d.appendVar("EXTRA_OEMESON", " " + " ".join(items))
    items = ["%s:%s" % (k, "True" if v else "False") for k,v in files.items()]
    d.appendVarFlag("do_configure", "file-checksums", " " + " ".join(items))

python () {
    find_meson_cross_files(d)
}
