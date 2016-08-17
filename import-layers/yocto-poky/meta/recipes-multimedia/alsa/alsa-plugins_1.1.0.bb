SUMMARY = "ALSA Plugins"
HOMEPAGE = "http://alsa-project.org"
SECTION = "multimedia"

# The primary license of alsa-plugins is LGPLv2.1.
#
# m4/attributes.m4 is licensed under GPLv2+. m4/attributes.m4 is part of the
# build system, and doesn't affect the licensing of the build result.
#
# The samplerate plugin source code is licensed under GPLv2+ to be consistent
# with the libsamplerate license. However, if the licensee has a commercial
# license for libsamplerate, the samplerate plugin may be used under the terms
# of LGPLv2.1 like the rest of the plugins.
LICENSE = "LGPLv2.1 & GPLv2+"
LIC_FILES_CHKSUM = "\
        file://COPYING;md5=7fbc338309ac38fefcd64b04bb903e34 \
        file://COPYING.GPL;md5=94d55d512a9ba36caa9b7df079bae19f \
        file://m4/attributes.m4;endline=33;md5=b25958da44c02231e3641f1bccef53eb \
        file://rate/rate_samplerate.c;endline=35;md5=fd77bce85f4a338c0e8ab18430b69fae \
"

SRC_URI = "\
        ftp://ftp.alsa-project.org/pub/plugins/${BP}.tar.bz2 \
        file://0001-pph-include-config.h-from-rate_speexrate.c.patch \
"
SRC_URI[md5sum] = "b123e42ed881b9adbc99e4040f257c39"
SRC_URI[sha256sum] = "3b83c329953bef99f5fe25ae04ec4a455fe6514939f3b45a5321966652b2c9ee"

DEPENDS += "alsa-lib"

inherit autotools pkgconfig

PACKAGECONFIG ??= "\
        samplerate \
        speexdsp \
        ${@bb.utils.contains('DISTRO_FEATURES', 'pulseaudio', 'pulseaudio', '', d)} \
"
PACKAGECONFIG[avcodec] = "--enable-avcodec,--disable-avcodec,libav"
PACKAGECONFIG[jack] = "--enable-jack,--disable-jack,jack"
PACKAGECONFIG[maemo-plugin] = "--enable-maemo-plugin,--disable-maemo-plugin"
PACKAGECONFIG[maemo-resource-manager] = "--enable-maemo-resource-manager,--disable-maemo-resource-manager,dbus"
PACKAGECONFIG[pulseaudio] = "--enable-pulseaudio,--disable-pulseaudio,pulseaudio"
PACKAGECONFIG[samplerate] = "--enable-samplerate,--disable-samplerate,libsamplerate0"
PACKAGECONFIG[speexdsp] = "--with-speex=lib,--with-speex=no,speexdsp"

PACKAGES += "${@bb.utils.contains('PACKAGECONFIG', 'pulseaudio', 'alsa-plugins-pulseaudio-conf', '', d)}"

PACKAGES_DYNAMIC = "^libasound-module-.*"

# The alsa-plugins package doesn't itself contain anything, it just depends on
# all built plugins.
ALLOW_EMPTY_${PN} = "1"

do_install_append() {
	rm ${D}${libdir}/alsa-lib/*.la

	# We use the example as is, so just drop the .example suffix.
	if [ "${@bb.utils.contains('PACKAGECONFIG', 'pulseaudio', 'yes', 'no', d)}" = "yes" ]; then
		mv ${D}${datadir}/alsa/alsa.conf.d/99-pulseaudio-default.conf.example ${D}${datadir}/alsa/alsa.conf.d/99-pulseaudio-default.conf
	fi
}

python populate_packages_prepend() {
    plugindir = bb.data.expand('${libdir}/alsa-lib/', d)
    packages = " ".join(do_split_packages(d, plugindir, '^libasound_module_(.*)\.so$', 'libasound-module-%s', 'Alsa plugin for %s', extra_depends=''))
    d.setVar("RDEPENDS_alsa-plugins", packages)
}

# The rate plugins create some symlinks. For example, the samplerate plugin
# creates these links to the main plugin file:
#
#   libasound_module_rate_samplerate_best.so
#   libasound_module_rate_samplerate_linear.so
#   libasound_module_rate_samplerate_medium.so
#   libasound_module_rate_samplerate_order.so
#
# The other rate plugins create similar links. We have to add the links to
# FILES manually, because do_split_packages() skips the links (which is good,
# because we wouldn't want do_split_packages() to create separate packages for
# the symlinks).
#
# The symlinks cause QA errors, because usually it's a bug if a non
# -dev/-dbg/-nativesdk package contains links to .so files, but in this case
# the errors are false positives, so we disable the QA checks.
FILES_${MLPREFIX}libasound-module-rate-lavcrate += "${libdir}/alsa-lib/*rate_lavcrate_*.so"
FILES_${MLPREFIX}libasound-module-rate-samplerate += "${libdir}/alsa-lib/*rate_samplerate_*.so"
FILES_${MLPREFIX}libasound-module-rate-speexrate += "${libdir}/alsa-lib/*rate_speexrate_*.so"
INSANE_SKIP_${MLPREFIX}libasound-module-rate-lavcrate = "dev-so"
INSANE_SKIP_${MLPREFIX}libasound-module-rate-samplerate = "dev-so"
INSANE_SKIP_${MLPREFIX}libasound-module-rate-speexrate = "dev-so"

# 50-pulseaudio.conf defines a device named "pulse" that applications can use
# if they explicitly want to use the PulseAudio plugin.
# 99-pulseaudio-default.conf configures the "default" device to use the
# PulseAudio plugin.
FILES_${PN}-pulseaudio-conf += "\
        ${datadir}/alsa/alsa.conf.d/50-pulseaudio.conf \
        ${datadir}/alsa/alsa.conf.d/99-pulseaudio-default.conf \
"

RDEPENDS_${PN}-pulseaudio-conf += "\
        libasound-module-conf-pulse \
        libasound-module-ctl-pulse \
        libasound-module-pcm-pulse \
"
