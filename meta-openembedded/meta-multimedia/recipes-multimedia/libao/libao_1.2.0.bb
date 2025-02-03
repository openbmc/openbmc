SUMMARY = "Cross-platform audio output library and plugins"
DESCRIPTION = "Libao is a cross-platform audio library that allows programs to \
               output audio using a simple API on a wide variety of platforms."
SECTION = "multimedia"
HOMEPAGE = "https://www.xiph.org/ao/"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

SRC_URI = "http://downloads.xiph.org/releases/ao/${BP}.tar.gz"
SRC_URI[md5sum] = "9f5dd20d7e95fd0dd72df5353829f097"
SRC_URI[sha256sum] = "03ad231ad1f9d64b52474392d63c31197b0bc7bd416e58b1c10a329a5ed89caf"

inherit autotools

PACKAGES += "${BPN}-ckport"
PACKAGES_DYNAMIC += "^${BPN}-plugin-.*"

do_install:append () {
    find "${D}" -name '*.la' -exec rm -f {} +
}

python populate_packages:prepend () {
    rootdir = bb.data.expand('${libdir}/ao/plugins-4', d)
    rootdir_dbg = bb.data.expand('${libdir}/ao/plugins-4/.debug', d)
    do_split_packages(d, rootdir, r'^(.*)\.so$', output_pattern='${BPN}-plugin-%s', description='AO %s plugin')
    do_split_packages(d, rootdir_dbg, r'^(.*)\.so$', output_pattern='${BPN}-plugin-%s-dbg', description='AO %s plugin debug data')
}

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'alsa pulseaudio', d)}"
PACKAGECONFIG[alsa] = "--enable-alsa,--disable-alsa,alsa-lib"
PACKAGECONFIG[pulseaudio] = "--enable-pulse,--disable-pulse,pulseaudio"
FILES:${BPN}-ckport = "${libdir}/ckport"
