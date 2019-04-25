SUMMARY = "Debian's start-stop-daemon utility extracted from the dpkg \
package"
LICENSE = "PD"
LIC_FILES_CHKSUM = "file://utils/start-stop-daemon.c;endline=21;md5=8fbd0497a7d0b01e99820bffcb58e9ad"
DEPENDS = "zlib bzip2 perl ncurses"
# start-stop-daemon is usually shipped by dpkg
RCONFLICTS_${PN} = "dpkg"

SRC_URI = " \
    ${DEBIAN_MIRROR}/main/d/dpkg/dpkg_${PV}.tar.xz \
    file://0001-dpkg-start-stop-daemon-Accept-SIG-prefixed-signal-na.patch \
    file://noman.patch \
    file://remove-tar-no-timestamp.patch \
    file://arch_pm.patch \
    file://add_armeb_triplet_entry.patch \
    file://0002-Adapt-to-linux-wrs-kernel-version-which-has-characte.patch \
    file://0003-Our-pre-postinsts-expect-D-to-be-set-when-running-in.patch \
    file://0004-The-lutimes-function-doesn-t-work-properly-for-all-s.patch \
    file://0005-dpkg-compiler.m4-remove-Wvla.patch \
    file://0006-add-musleabi-to-known-target-tripets.patch \
    file://0007-dpkg-deb-build.c-Remove-usage-of-clamp-mtime-in-tar.patch \
    file://0001-dpkg-Support-muslx32-build.patch \
"
SRC_URI[md5sum] = "e463f58b04acb23659df23d2a7a05cff"
SRC_URI[sha256sum] = "c49c371953aea03f543814dcae37c069e86069333fb2e24e9252e76647663492"

inherit autotools gettext perlnative pkgconfig perl-version

S = "${WORKDIR}/dpkg-${PV}"

EXTRA_OECONF = "\
                --disable-dselect \
                --enable-start-stop-daemon \
                --with-libz \
                --with-libbz2 \
                --without-libselinux \
                "

PACKAGECONFIG = "liblzma"
PACKAGECONFIG[liblzma] = "--with-liblzma,--without-liblzma, xz"

export PERL = "${bindir}/perl"
export PERL_LIBDIR = "${libdir}/perl/${@get_perl_version(d)}"
EXTRA_OECONF += "TAR=tar"

EXTRA_OECONF_append_class-target = " DEB_HOST_ARCH=${DPKG_ARCH}"

DPKG_ARCH ??= "${@deb_arch_map(d.getVar('TARGET_ARCH'), d.getVar('TUNE_FEATURES'))}"

def deb_arch_map(arch, tune):
    tune_features = tune.split()
    if arch == "allarch":
        return "all"
    if arch in ["i586", "i686"]:
        return "i386"
    if arch == "x86_64":
        if "mx32" in tune_features:
            return "x32"
        return "amd64"
    if arch.startswith("mips"):
        endian = ["el", ""]["bigendian" in tune_features]
        if "n64" in tune_features:
            return "mips64" + endian
        if "n32" in tune_features:
            return "mipsn32" + endian
        return "mips" + endian
    if arch == "powerpc":
        return arch + ["", "spe"]["spe" in tune_features]
    if arch == "aarch64":
        return "arm64"
    if arch == "arm":
        return arch + ["el", "hf"]["callconvention-hard" in tune_features]
    return arch

do_install_append () {
    # remove everything that is not related to start-stop-daemon, since there
    # is no explicit rule for only installing ssd
    find ${D} -type f -not -name "*start-stop-daemon*" -exec rm {} \;
    find ${D} -depth -type d -empty -exec rmdir {} \;

    # support for buggy init.d scripts that refer to an alternative
    # explicit path to start-stop-daemon
    if [ "${base_sbindir}" != "${sbindir}" ]; then
        mkdir -p ${D}${base_sbindir}
        ln -sf ${sbindir}/start-stop-daemon ${D}${base_sbindir}/start-stop-daemon
    fi
}
