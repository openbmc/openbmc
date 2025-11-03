require glib.inc

PE = "1"

SHRT_VER = "${@oe.utils.trim_version("${PV}", 2)}"

SRC_URI = "${GNOME_MIRROR}/glib/${SHRT_VER}/glib-${PV}.tar.xz \
           file://run-ptest \
           file://0001-Fix-DATADIRNAME-on-uclibc-Linux.patch \
           file://0001-Remove-the-warning-about-deprecated-paths-in-schemas.patch \
           file://0001-Install-gio-querymodules-as-libexec_PROGRAM.patch \
           file://0010-Do-not-hardcode-python-path-into-various-tools.patch \
           file://0001-Set-host_machine-correctly-when-building-with-mingw3.patch \
           file://0001-Do-not-write-bindir-into-pkg-config-files.patch \
           file://0001-meson-Run-atomics-test-on-clang-as-well.patch \
           file://0001-gio-tests-resources.c-comment-out-a-build-host-only-.patch \
           file://0001-Switch-from-the-deprecated-distutils-module-to-the-p.patch \
           file://memory-monitor.patch \
           file://skip-timeout.patch \
           file://CVE-2024-52533.patch \
           file://gdatetime-test-fail-0001.patch \
           file://gdatetime-test-fail-0002.patch \
           file://gdatetime-test-fail-0003.patch \
           file://CVE-2025-3360-01.patch \
           file://CVE-2025-3360-02.patch \
           file://CVE-2025-3360-03.patch \
           file://CVE-2025-3360-04.patch \
           file://CVE-2025-3360-05.patch \
           file://CVE-2025-3360-06.patch \
           file://CVE-2025-4373-01.patch \
           file://CVE-2025-4373-02.patch \
           file://CVE-2025-7039.patch \
           file://CVE-2025-6052-01.patch \
           file://CVE-2025-6052-02.patch \
           file://CVE-2025-6052-03.patch \
           "
SRC_URI:append:class-native = " file://relocate-modules.patch \
                                file://0001-meson.build-do-not-enable-pidfd-features-on-native-g.patch \
                              "

SRC_URI[sha256sum] = "244854654dd82c7ebcb2f8e246156d2a05eb9cd1ad07ed7a779659b4602c9fae"

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

CVE_STATUS[CVE-2025-4056] = "not-applicable-platform: Issue only applies on Windows"
