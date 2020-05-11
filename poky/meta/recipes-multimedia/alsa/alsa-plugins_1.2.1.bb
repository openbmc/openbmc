SUMMARY = "ALSA Plugins"
HOMEPAGE = "http://alsa-project.org"
BUGTRACKER = "http://alsa-project.org/main/index.php/Bug_Tracking"
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
        file://COPYING;md5=a916467b91076e631dd8edb7424769c7 \
        file://COPYING.GPL;md5=59530bdf33659b29e73d4adb9f9f6552 \
        file://m4/attributes.m4;endline=33;md5=b25958da44c02231e3641f1bccef53eb \
        file://rate/rate_samplerate.c;endline=35;md5=fd77bce85f4a338c0e8ab18430b69fae \
"

SRC_URI = "https://www.alsa-project.org/files/pub/plugins/${BP}.tar.bz2"
SRC_URI[md5sum] = "5b11cd3ec92e5f9190ec378565b529e8"
SRC_URI[sha256sum] = "4d94de7ad41734b8604a652521200bb6554fcf0c2c00fdbd302b1710d76548da"

DEPENDS += "alsa-lib"

inherit autotools pkgconfig

PACKAGECONFIG ??= "\
        samplerate \
        speexdsp \
        ${@bb.utils.filter('DISTRO_FEATURES', 'pulseaudio', d)} \
"
PACKAGECONFIG[aaf] = "--enable-aaf,--disable-aaf,avtp"
PACKAGECONFIG[jack] = "--enable-jack,--disable-jack,jack"
PACKAGECONFIG[libav] = "--enable-libav,--disable-libav,libav"
PACKAGECONFIG[maemo-plugin] = "--enable-maemo-plugin,--disable-maemo-plugin"
PACKAGECONFIG[maemo-resource-manager] = "--enable-maemo-resource-manager,--disable-maemo-resource-manager,dbus"
PACKAGECONFIG[pulseaudio] = "--enable-pulseaudio,--disable-pulseaudio,pulseaudio"
PACKAGECONFIG[samplerate] = "--enable-samplerate,--disable-samplerate,libsamplerate0"
PACKAGECONFIG[speexdsp] = "--with-speex=lib,--with-speex=no,speexdsp"

PACKAGES += "${@bb.utils.contains('PACKAGECONFIG', 'pulseaudio', 'alsa-plugins-pulseaudio-conf', '', d)}"

PACKAGES_DYNAMIC = "^libasound-module-.*"

# The alsa-plugins package doesn't itself contain anything, it just depends on
# all built plugins.
FILES_${PN} = ""
ALLOW_EMPTY_${PN} = "1"

do_install_append() {
	rm ${D}${libdir}/alsa-lib/*.la

	if [ "${@bb.utils.contains('PACKAGECONFIG', 'pulseaudio', 'yes', 'no', d)}" = "yes" ]; then
		# We use the example as is. Upstream installs the file under
		# /etc, but we move it under /usr/share and add a symlink under
		# /etc to be consistent with other installed configuration
		# files.
		mv ${D}${sysconfdir}/alsa/conf.d/99-pulseaudio-default.conf.example ${D}${datadir}/alsa/alsa.conf.d/99-pulseaudio-default.conf
		ln -s ${datadir}/alsa/alsa.conf.d/99-pulseaudio-default.conf ${D}${sysconfdir}/alsa/conf.d/99-pulseaudio-default.conf
	fi
}

python populate_packages_prepend() {
    plugindir = d.expand('${libdir}/alsa-lib/')
    packages = " ".join(do_split_packages(d, plugindir, r'^libasound_module_(.*)\.so$', 'libasound-module-%s', 'Alsa plugin for %s', extra_depends=''))
    d.setVar("RDEPENDS_alsa-plugins", packages)
}

# Many plugins have a configuration file (plus a symlink in /etc) associated
# with them. We put the plugin and it's configuration usually in the same
# package, but that's problematic when the configuration file is related to
# multiple plugins, as is the case with the pulse, oss and maemo plugins. In
# case of the pulse plugins, we have a separate alsa-plugins-pulseaudio-conf
# package that depends on all the pulse plugins, which ensures that all plugins
# that the configuration references are installed. The oss and maemo
# configuration files, on the other hand, are in the respective pcm plugin
# packages. Therefore it's possible to install the configuration file without
# the ctl plugin that the configuration file references. This is unlikely to
# cause big problems, but some kind of improvement to the packaging could
# probably be done here (at least it would be good to handle the different
# plugins in a consistent way).
FILES_${MLPREFIX}libasound-module-ctl-arcam-av += "\
        ${datadir}/alsa/alsa.conf.d/50-arcam-av-ctl.conf \
        ${sysconfdir}/alsa/conf.d/50-arcam-av-ctl.conf \
"
FILES_${MLPREFIX}libasound-module-pcm-a52 += "\
        ${datadir}/alsa/alsa.conf.d/60-a52-encoder.conf \
        ${sysconfdir}/alsa/conf.d/60-a52-encoder.conf \
"
FILES_${MLPREFIX}libasound-module-pcm-alsa-dsp += "\
        ${datadir}/alsa/alsa.conf.d/98-maemo.conf \
        ${sysconfdir}/alsa/conf.d/98-maemo.conf \
"
FILES_${MLPREFIX}libasound-module-pcm-jack += "\
        ${datadir}/alsa/alsa.conf.d/50-jack.conf \
        ${sysconfdir}/alsa/conf.d/50-jack.conf \
"
FILES_${MLPREFIX}libasound-module-pcm-oss += "\
        ${datadir}/alsa/alsa.conf.d/50-oss.conf \
        ${sysconfdir}/alsa/conf.d/50-oss.conf \
"
FILES_${MLPREFIX}libasound-module-pcm-speex += "\
        ${datadir}/alsa/alsa.conf.d/60-speex.conf \
        ${sysconfdir}/alsa/conf.d/60-speex.conf \
"
FILES_${MLPREFIX}libasound-module-pcm-upmix += "\
        ${datadir}/alsa/alsa.conf.d/60-upmix.conf \
        ${sysconfdir}/alsa/conf.d/60-upmix.conf \
"
FILES_${MLPREFIX}libasound-module-pcm-usb-stream += "\
        ${datadir}/alsa/alsa.conf.d/98-usb-stream.conf \
        ${sysconfdir}/alsa/conf.d/98-usb-stream.conf \
"
FILES_${MLPREFIX}libasound-module-pcm-vdownmix += "\
        ${datadir}/alsa/alsa.conf.d/60-vdownmix.conf \
        ${sysconfdir}/alsa/conf.d/60-vdownmix.conf \
"
FILES_${MLPREFIX}libasound-module-rate-lavrate += "\
        ${datadir}/alsa/alsa.conf.d/10-rate-lav.conf \
        ${sysconfdir}/alsa/conf.d/10-rate-lav.conf \
"
FILES_${MLPREFIX}libasound-module-rate-samplerate += "\
        ${datadir}/alsa/alsa.conf.d/10-samplerate.conf \
        ${sysconfdir}/alsa/conf.d/10-samplerate.conf \
"
FILES_${MLPREFIX}libasound-module-rate-speexrate += "\
        ${datadir}/alsa/alsa.conf.d/10-speexrate.conf \
        ${sysconfdir}/alsa/conf.d/10-speexrate.conf \
"

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
FILES_${MLPREFIX}libasound-module-rate-lavrate += "${libdir}/alsa-lib/*rate_lavrate_*.so"
FILES_${MLPREFIX}libasound-module-rate-samplerate += "${libdir}/alsa-lib/*rate_samplerate_*.so"
FILES_${MLPREFIX}libasound-module-rate-speexrate += "${libdir}/alsa-lib/*rate_speexrate_*.so"
INSANE_SKIP_${MLPREFIX}libasound-module-rate-lavrate = "dev-so"
INSANE_SKIP_${MLPREFIX}libasound-module-rate-samplerate = "dev-so"
INSANE_SKIP_${MLPREFIX}libasound-module-rate-speexrate = "dev-so"

# 50-pulseaudio.conf defines a device named "pulse" that applications can use
# if they explicitly want to use the PulseAudio plugin.
# 99-pulseaudio-default.conf configures the "default" device to use the
# PulseAudio plugin.
FILES_${PN}-pulseaudio-conf += "\
        ${datadir}/alsa/alsa.conf.d/50-pulseaudio.conf \
        ${datadir}/alsa/alsa.conf.d/99-pulseaudio-default.conf \
        ${sysconfdir}/alsa/conf.d/50-pulseaudio.conf \
        ${sysconfdir}/alsa/conf.d/99-pulseaudio-default.conf \
"

RDEPENDS_${PN}-pulseaudio-conf += "\
        libasound-module-conf-pulse \
        libasound-module-ctl-pulse \
        libasound-module-pcm-pulse \
"
